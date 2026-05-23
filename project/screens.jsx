// screens.jsx — Home, Search, Progress, Profile

// ─────────────────────────────────────────────────────────────
// Shared bits
// ─────────────────────────────────────────────────────────────

function Card({ children, style, ...rest }) {
  return (
    <div className="tf-card" style={style} {...rest}>{children}</div>
  );
}

function Tag({ children, tone = 'soft', style }) {
  const tones = {
    soft: { background: 'var(--soft)', color: 'var(--primary-deep)' },
    ghost: { background: 'var(--bg-soft)', color: 'var(--ink-2)' },
    primary: { background: 'var(--primary)', color: '#fff' },
  };
  return (
    <span style={{
      fontSize: 11, fontWeight: 600, padding: '4px 8px', borderRadius: 999,
      letterSpacing: '-0.01em', whiteSpace: 'nowrap',
      ...tones[tone], ...style,
    }}>{children}</span>
  );
}

function MacroBar({ label, value, goal, color }) {
  const pct = Math.min(100, (value / goal) * 100);
  return (
    <div style={{ flex: 1, minWidth: 0 }}>
      <div style={{ fontSize: 10, fontWeight: 600, color: 'var(--ink-3)', letterSpacing: '0.04em', textTransform: 'uppercase', marginBottom: 4 }}>
        {label}
      </div>
      <div style={{ height: 5, borderRadius: 4, background: 'var(--hairline)', overflow: 'hidden', marginBottom: 6 }}>
        <div style={{ width: `${pct}%`, height: '100%', background: color, borderRadius: 4, transition: 'width .5s cubic-bezier(.2,.8,.2,1)' }} />
      </div>
      <div style={{ fontSize: 11, fontVariantNumeric: 'tabular-nums', whiteSpace: 'nowrap' }}>
        <strong style={{ color: 'var(--ink)', fontWeight: 600 }}>{Math.round(value)}</strong>
        <span style={{ color: 'var(--ink-3)' }}> / {goal}g</span>
      </div>
    </div>
  );
}

// Calorie ring — clinical, big center number
function CalorieRing({ consumed, goal, size = 200 }) {
  const r = (size - 18) / 2;
  const c = 2 * Math.PI * r;
  const pct = Math.min(1, consumed / goal);
  const offset = c * (1 - pct);
  const remaining = Math.max(0, goal - consumed);
  return (
    <div style={{ width: size, height: size, position: 'relative' }}>
      <svg width={size} height={size} style={{ transform: 'rotate(-90deg)' }}>
        <circle cx={size / 2} cy={size / 2} r={r} stroke="var(--hairline)" strokeWidth={9} fill="none"/>
        <circle cx={size / 2} cy={size / 2} r={r} stroke="var(--primary)" strokeWidth={9} fill="none"
          strokeLinecap="round" strokeDasharray={c} strokeDashoffset={offset}
          style={{ transition: 'stroke-dashoffset 1s cubic-bezier(.2,.8,.2,1)' }}/>
      </svg>
      <div style={{
        position: 'absolute', inset: 0, display: 'flex', flexDirection: 'column',
        alignItems: 'center', justifyContent: 'center', textAlign: 'center',
      }}>
        <div style={{ fontFamily: 'var(--font-display)', fontSize: 52, fontWeight: 400, lineHeight: 0.9, color: 'var(--ink)', letterSpacing: '-0.02em', fontVariantNumeric: 'tabular-nums' }}>
          {fmtNum(remaining)}
        </div>
        <div style={{ fontSize: 10, color: 'var(--ink-3)', marginTop: 12, fontWeight: 600, letterSpacing: '0.08em', textTransform: 'uppercase', lineHeight: 1 }}>kkal tersisa</div>
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// Home screen
// ─────────────────────────────────────────────────────────────
function HomeScreen({ state, dispatch }) {
  const { profile, log, selectedDate, lang } = state;
  const t = useT(lang);

  const day = log[selectedDate] || { items: [] };
  const consumed = day.items.reduce((s, i) => s + i.kcal, 0);
  const protein = day.items.reduce((s, i) => s + (i.p || 0), 0);
  const carbs = day.items.reduce((s, i) => s + (i.c || 0), 0);
  const fat = day.items.reduce((s, i) => s + (i.f || 0), 0);
  const goal = profile.dailyGoal;

  // Macros (rough split: 25% P, 50% C, 25% F)
  const pGoal = Math.round((goal * 0.25) / 4);
  const cGoal = Math.round((goal * 0.5) / 4);
  const fGoal = Math.round((goal * 0.25) / 9);

  // Week date strip
  const today = new Date();
  const weekDays = [];
  for (let i = -3; i <= 3; i++) {
    const d = new Date(today);
    d.setDate(today.getDate() + i);
    weekDays.push(d);
  }

  const meals = [
    { key: 'sarapan',     label: t('meal_sarapan'),     emoji: '🌅' },
    { key: 'makan_siang', label: t('meal_makan_siang'), emoji: '☀️' },
    { key: 'makan_malam', label: t('meal_makan_malam'), emoji: '🌙' },
    { key: 'cemilan',     label: t('meal_cemilan'),     emoji: '🍪' },
  ];

  return (
    <div className="tf-screen">
      {/* Greeting header */}
      <div style={{ padding: '8px 20px 0', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div>
          <div style={{ fontSize: 13, color: 'var(--ink-2)', fontWeight: 500 }}>{getGreeting(lang, t)}</div>
          <h1 style={{ fontFamily: 'var(--font-display)', fontSize: 26, lineHeight: 1.1, fontWeight: 500, margin: '2px 0 0', color: 'var(--ink)', letterSpacing: '-0.02em' }}>
            {profile.name || 'Halo'}
          </h1>
        </div>
        <button className="tf-avatar" onClick={() => dispatch({ type: 'showGoogle' })} aria-label="profile">
          {profile.googleConnected
            ? <span style={{ fontSize: 12, fontWeight: 600 }}>{(profile.name || 'A')[0]}</span>
            : <IconUser size={20} />}
        </button>
      </div>

      {/* Week strip */}
      <div className="tf-weekstrip">
        {weekDays.map(d => {
          const dayKey = d.toISOString().slice(0, 10);
          const isSelected = dayKey === selectedDate;
          const isToday = dayKey === today.toISOString().slice(0, 10);
          const dayNames = lang === 'id'
            ? ['Min', 'Sen', 'Sel', 'Rab', 'Kam', 'Jum', 'Sab']
            : ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
          return (
            <button key={dayKey}
                    onClick={() => dispatch({ type: 'selectDate', date: dayKey })}
                    className={`tf-day ${isSelected ? 'on' : ''}`}>
              <div className="tf-day-name">{dayNames[d.getDay()]}</div>
              <div className="tf-day-num">{d.getDate()}</div>
              {isToday && <div className="tf-day-dot" />}
            </button>
          );
        })}
      </div>

      {/* Hero ring card */}
      <Card style={{ margin: '12px 16px 0', padding: '20px 18px 18px' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', position: 'relative' }}>
          <CalorieRing consumed={consumed} goal={goal} size={196} />
        </div>
        <div style={{ display: 'flex', gap: 14, marginTop: 18 }}>
          <MacroBar label={lang === 'id' ? 'Protein' : 'Protein'} value={protein} goal={pGoal} color="#0f9d6c"/>
          <MacroBar label={lang === 'id' ? 'Karbo' : 'Carbs'}     value={carbs}   goal={cGoal} color="#e8a838"/>
          <MacroBar label={lang === 'id' ? 'Lemak' : 'Fat'}       value={fat}     goal={fGoal} color="#d77a55"/>
        </div>
      </Card>

      {/* Streak chip */}
      <div style={{ display: 'flex', gap: 10, padding: '12px 16px 0' }}>
        <Card style={{ flex: 1, padding: '12px 14px', display: 'flex', alignItems: 'center', gap: 10, minWidth: 0 }}>
          <div className="tf-icon-tile small" style={{ background: '#fef3e6', color: '#d97706' }}>
            <IconFlame size={16}/>
          </div>
          <div style={{ minWidth: 0, flex: 1 }}>
            <div style={{ fontSize: 18, fontWeight: 600, fontFamily: 'var(--font-display)', lineHeight: 1, fontVariantNumeric: 'tabular-nums' }}>{profile.streak}</div>
            <div style={{ fontSize: 10.5, color: 'var(--ink-3)', marginTop: 3, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{t('streak')}</div>
          </div>
        </Card>
        <Card style={{ flex: 1, padding: '12px 14px', display: 'flex', alignItems: 'center', gap: 10, minWidth: 0 }}>
          <div className="tf-icon-tile small" style={{ background: 'var(--soft)', color: 'var(--primary-deep)' }}>
            <IconTarget size={16}/>
          </div>
          <div style={{ minWidth: 0, flex: 1 }}>
            <div style={{ fontSize: 18, fontWeight: 600, fontFamily: 'var(--font-display)', lineHeight: 1, fontVariantNumeric: 'tabular-nums' }}>{fmtNum(goal)}</div>
            <div style={{ fontSize: 10.5, color: 'var(--ink-3)', marginTop: 3, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{lang === 'id' ? 'target harian' : 'daily goal'}</div>
          </div>
        </Card>
      </div>

      {/* Meal sections */}
      <div style={{ padding: '16px 16px 0' }}>
        {meals.map(m => {
          const items = day.items.filter(i => i.meal === m.key);
          const mkcal = items.reduce((s, i) => s + i.kcal, 0);
          return (
            <Card key={m.key} style={{ marginBottom: 10, overflow: 'hidden' }}>
              <div style={{ padding: '12px 14px 8px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                  <div style={{ fontSize: 18 }}>{m.emoji}</div>
                  <div>
                    <div style={{ fontSize: 14, fontWeight: 600, color: 'var(--ink)' }}>{m.label}</div>
                    <div style={{ fontSize: 11, color: 'var(--ink-3)', marginTop: 1, whiteSpace: 'nowrap' }}>
                      {items.length === 0
                        ? t('empty_meal_prompt')
                        : `${items.length} item · ${fmtNum(mkcal)} kkal`}
                    </div>
                  </div>
                </div>
                <button className="tf-add-mini" onClick={() => dispatch({ type: 'showAdd', meal: m.key })}>
                  <IconPlus size={16}/>
                </button>
              </div>
              {items.length > 0 && (
                <div style={{ borderTop: '1px solid var(--hairline)' }}>
                  {items.map((it, i) => (
                    <FoodRow key={it.uid} item={it} onDelete={() => dispatch({ type: 'removeItem', uid: it.uid })} isLast={i === items.length - 1}/>
                  ))}
                </div>
              )}
            </Card>
          );
        })}
      </div>
    </div>
  );
}

function FoodRow({ item, onDelete, isLast }) {
  return (
    <div className="tf-foodrow" style={{ borderBottom: isLast ? 'none' : '1px solid var(--hairline)' }}>
      <div style={{ fontSize: 20, width: 28, textAlign: 'center' }}>{item.emoji || '🍽️'}</div>
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ fontSize: 14, fontWeight: 500, color: 'var(--ink)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
          {item.name}
        </div>
        <div style={{ fontSize: 11, color: 'var(--ink-3)', marginTop: 1 }}>
          {item.qty != null && item.qty !== 1 ? `${item.qty}× ` : ''}{item.serving}
        </div>
      </div>
      <div style={{ textAlign: 'right', fontVariantNumeric: 'tabular-nums', minWidth: 56 }}>
        <div style={{ fontSize: 14, fontWeight: 600, color: 'var(--ink)' }}>{fmtNum(item.kcal)}</div>
        <div style={{ fontSize: 10, color: 'var(--ink-3)' }}>kkal</div>
      </div>
      <button className="tf-row-del" onClick={onDelete} aria-label="delete">
        <IconX size={14}/>
      </button>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// Search screen
// ─────────────────────────────────────────────────────────────
function SearchScreen({ state, dispatch }) {
  const { lang } = state;
  const t = useT(lang);
  const [q, setQ] = React.useState('');
  const [cat, setCat] = React.useState('all');

  const filtered = FOODS.filter(f => {
    if (cat !== 'all' && f.category !== cat) return false;
    if (q.trim()) {
      const needle = q.toLowerCase();
      return f.name.toLowerCase().includes(needle) || f.name_en.toLowerCase().includes(needle);
    }
    return true;
  });

  return (
    <div className="tf-screen">
      <div style={{ padding: '8px 20px 0' }}>
        <h1 style={{ fontFamily: 'var(--font-display)', fontSize: 28, fontWeight: 500, margin: 0, color: 'var(--ink)', letterSpacing: '-0.02em' }}>
          {t('search_title')}
        </h1>
      </div>

      {/* Search input */}
      <div style={{ padding: '12px 16px 0' }}>
        <div className="tf-input-wrap">
          <IconSearch size={16} weight={2}/>
          <input
            type="text"
            value={q}
            onChange={e => setQ(e.target.value)}
            placeholder={t('search_placeholder')}
            className="tf-input"
          />
          {q && (
            <button className="tf-input-clear" onClick={() => setQ('')}>
              <IconX size={14}/>
            </button>
          )}
        </div>
      </div>

      {/* Category chips */}
      <div className="tf-chips">
        {CATEGORIES.map(c => (
          <button key={c.id}
                  className={`tf-chip ${cat === c.id ? 'on' : ''}`}
                  onClick={() => setCat(c.id)}>
            {lang === 'id' ? c.id_label : c.en_label}
          </button>
        ))}
      </div>

      {/* Results */}
      <div style={{ padding: '12px 16px 20px' }}>
        {filtered.length === 0 && (
          <div style={{ textAlign: 'center', padding: '40px 0', color: 'var(--ink-3)', fontSize: 14 }}>
            {t('search_empty')}
          </div>
        )}
        {filtered.length > 0 && (
          <Card style={{ overflow: 'hidden' }}>
            {filtered.map((f, i) => (
              <SearchRow key={f.id} food={f} lang={lang} isLast={i === filtered.length - 1}
                         onTap={() => dispatch({ type: 'showFoodDetail', foodId: f.id })}/>
            ))}
          </Card>
        )}
      </div>
    </div>
  );
}

function SearchRow({ food, lang, isLast, onTap }) {
  return (
    <button className="tf-search-row" onClick={onTap}
            style={{ borderBottom: isLast ? 'none' : '1px solid var(--hairline)' }}>
      <div style={{ fontSize: 22, width: 32, textAlign: 'center' }}>{food.emoji}</div>
      <div style={{ flex: 1, minWidth: 0, textAlign: 'left' }}>
        <div style={{ fontSize: 14, fontWeight: 500, color: 'var(--ink)' }}>
          {lang === 'id' ? food.name : food.name_en}
        </div>
        <div style={{ fontSize: 11, color: 'var(--ink-3)', marginTop: 1 }}>
          {lang === 'id' ? food.serving : food.serving_en}
        </div>
      </div>
      <div style={{ textAlign: 'right', fontVariantNumeric: 'tabular-nums' }}>
        <div style={{ fontSize: 14, fontWeight: 600, color: 'var(--ink)' }}>{food.kcal}</div>
        <div style={{ fontSize: 10, color: 'var(--ink-3)' }}>kkal</div>
      </div>
    </button>
  );
}

// ─────────────────────────────────────────────────────────────
// Progress screen
// ─────────────────────────────────────────────────────────────
function ProgressScreen({ state }) {
  const { lang, profile, log } = state;
  const t = useT(lang);

  // Build 7-day bar data from log + sample fills
  const today = new Date();
  const days = [];
  for (let i = 6; i >= 0; i--) {
    const d = new Date(today);
    d.setDate(today.getDate() - i);
    const key = d.toISOString().slice(0, 10);
    const items = log[key]?.items || [];
    const kcal = items.reduce((s, it) => s + it.kcal, 0);
    days.push({ date: d, key, kcal });
  }

  const goal = profile.dailyGoal;
  const maxKcal = Math.max(goal * 1.1, ...days.map(d => d.kcal), 1);
  const avg = Math.round(days.reduce((s, d) => s + d.kcal, 0) / Math.max(1, days.filter(d => d.kcal > 0).length || 1));

  // Weight trend mock data
  const startWeight = profile.weight + 1.8;
  const weights = [
    startWeight, startWeight - 0.3, startWeight - 0.7, startWeight - 0.9,
    startWeight - 1.2, startWeight - 1.5, profile.weight,
  ];
  const wMin = Math.min(...weights) - 0.5;
  const wMax = Math.max(...weights) + 0.5;

  const dayNames = lang === 'id'
    ? ['Min', 'Sen', 'Sel', 'Rab', 'Kam', 'Jum', 'Sab']
    : ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

  return (
    <div className="tf-screen">
      <div style={{ padding: '8px 20px 0' }}>
        <h1 style={{ fontFamily: 'var(--font-display)', fontSize: 28, fontWeight: 500, margin: 0, color: 'var(--ink)', letterSpacing: '-0.02em' }}>
          {t('progress_title')}
        </h1>
      </div>

      {/* Stats row */}
      <div style={{ display: 'flex', gap: 10, padding: '14px 16px 0' }}>
        <Card style={{ flex: 1, padding: '14px' }}>
          <div style={{ fontSize: 10, fontWeight: 600, color: 'var(--ink-3)', letterSpacing: '0.05em', textTransform: 'uppercase' }}>
            {t('avg_daily')}
          </div>
          <div style={{ fontFamily: 'var(--font-display)', fontSize: 28, fontWeight: 500, marginTop: 4, color: 'var(--ink)', letterSpacing: '-0.02em', fontVariantNumeric: 'tabular-nums' }}>
            {fmtNum(avg)}
          </div>
          <div style={{ fontSize: 11, color: 'var(--ink-3)', marginTop: 2 }}>kkal · 7 {t('days')}</div>
        </Card>
        <Card style={{ flex: 1, padding: '14px' }}>
          <div style={{ fontSize: 10, fontWeight: 600, color: 'var(--ink-3)', letterSpacing: '0.05em', textTransform: 'uppercase' }}>
            {t('weight_trend')}
          </div>
          <div style={{ fontFamily: 'var(--font-display)', fontSize: 28, fontWeight: 500, marginTop: 4, color: 'var(--ink)', letterSpacing: '-0.02em', fontVariantNumeric: 'tabular-nums' }}>
            −1,8<span style={{ fontSize: 14, marginLeft: 2 }}>kg</span>
          </div>
          <div style={{ fontSize: 11, color: 'var(--primary-deep)', marginTop: 2, fontWeight: 600 }}>{t('on_track')}</div>
        </Card>
      </div>

      {/* Bar chart */}
      <Card style={{ margin: '12px 16px 0', padding: '16px 14px 12px' }}>
        <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', marginBottom: 14, gap: 8 }}>
          <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--ink)', whiteSpace: 'nowrap' }}>{t('this_week')}</div>
          <div style={{ fontSize: 11, color: 'var(--ink-3)', whiteSpace: 'nowrap' }}>
            {lang === 'id' ? 'Target' : 'Goal'} {fmtNum(goal)} kkal
          </div>
        </div>
        <div style={{ position: 'relative', height: 140, display: 'flex', alignItems: 'flex-end', gap: 8 }}>
          {/* Goal line */}
          <div style={{
            position: 'absolute', left: 0, right: 0,
            top: `${(1 - goal / maxKcal) * 100}%`,
            borderTop: '1px dashed var(--ink-3)',
            opacity: 0.5,
          }}>
            <span style={{
              position: 'absolute', right: 0, top: -16,
              fontSize: 9, color: 'var(--ink-3)', fontWeight: 600,
              fontVariantNumeric: 'tabular-nums',
            }}>{fmtNum(goal)}</span>
          </div>
          {days.map((d, i) => {
            const h = (d.kcal / maxKcal) * 100;
            const over = d.kcal > goal;
            const today = i === days.length - 1;
            return (
              <div key={d.key} style={{ flex: 1, height: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'flex-end' }}>
                <div style={{
                  width: '100%', minHeight: 4,
                  height: `${Math.max(2, h)}%`,
                  background: today ? 'var(--primary)' : (over ? '#e0a838' : 'var(--soft-deep, #b6e0cd)'),
                  borderRadius: 6,
                  transition: 'height .6s cubic-bezier(.2,.8,.2,1)',
                  opacity: d.kcal > 0 ? 1 : 0.25,
                }}/>
              </div>
            );
          })}
        </div>
        <div style={{ display: 'flex', marginTop: 8, gap: 8 }}>
          {days.map((d, i) => (
            <div key={d.key} style={{
              flex: 1, textAlign: 'center', fontSize: 10,
              color: i === days.length - 1 ? 'var(--primary-deep)' : 'var(--ink-3)',
              fontWeight: i === days.length - 1 ? 600 : 500,
            }}>{dayNames[d.date.getDay()]}</div>
          ))}
        </div>
      </Card>

      {/* Weight line chart */}
      <Card style={{ margin: '12px 16px 0', padding: '16px 14px 12px' }}>
        <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', marginBottom: 14, gap: 8 }}>
          <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--ink)', whiteSpace: 'nowrap' }}>{t('weight_trend')}</div>
          <div style={{ fontSize: 11, color: 'var(--ink-3)', fontVariantNumeric: 'tabular-nums', whiteSpace: 'nowrap' }}>
            {profile.weight} → {profile.targetWeight} kg
          </div>
        </div>
        <div style={{ height: 100, position: 'relative' }}>
          <svg viewBox="0 0 280 100" preserveAspectRatio="none" style={{ width: '100%', height: '100%', overflow: 'visible' }}>
            <defs>
              <linearGradient id="wgrad" x1="0" x2="0" y1="0" y2="1">
                <stop offset="0%" stopColor="var(--primary)" stopOpacity="0.22"/>
                <stop offset="100%" stopColor="var(--primary)" stopOpacity="0"/>
              </linearGradient>
            </defs>
            {/* horizontal lines */}
            {[0, 0.5, 1].map((p, i) => (
              <line key={i} x1="0" x2="280" y1={p * 100} y2={p * 100}
                    stroke="var(--hairline)" strokeWidth="1"/>
            ))}
            {(() => {
              const pts = weights.map((w, i) => ({
                x: (i / (weights.length - 1)) * 280,
                y: ((wMax - w) / (wMax - wMin)) * 100,
              }));
              const path = pts.map((p, i) => `${i === 0 ? 'M' : 'L'}${p.x},${p.y}`).join(' ');
              const area = path + ` L280,100 L0,100 Z`;
              return (
                <>
                  <path d={area} fill="url(#wgrad)"/>
                  <path d={path} stroke="var(--primary)" strokeWidth="2.5" fill="none" strokeLinecap="round" strokeLinejoin="round"/>
                  {pts.map((p, i) => (
                    <circle key={i} cx={p.x} cy={p.y} r={i === pts.length - 1 ? 4 : 2.5}
                            fill={i === pts.length - 1 ? 'var(--primary)' : '#fff'}
                            stroke="var(--primary)" strokeWidth="2"/>
                  ))}
                </>
              );
            })()}
          </svg>
        </div>
      </Card>

      <div style={{ height: 20 }}/>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// Profile screen
// ─────────────────────────────────────────────────────────────
function ProfileScreen({ state, dispatch }) {
  const { lang, profile } = state;
  const t = useT(lang);

  return (
    <div className="tf-screen">
      <div style={{ padding: '8px 20px 0' }}>
        <h1 style={{ fontFamily: 'var(--font-display)', fontSize: 28, fontWeight: 500, margin: 0, color: 'var(--ink)', letterSpacing: '-0.02em' }}>
          {t('profile_title')}
        </h1>
      </div>

      {/* User card */}
      <Card style={{ margin: '14px 16px 0', padding: '18px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
          <div className="tf-avatar lg">
            {profile.googleConnected
              ? <span style={{ fontSize: 22, fontWeight: 600 }}>{(profile.name || 'A')[0]}</span>
              : <IconUser size={28}/>}
          </div>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ fontSize: 17, fontWeight: 600, color: 'var(--ink)' }}>{profile.name || 'Tamu'}</div>
            <div style={{ fontSize: 12, color: 'var(--ink-3)', marginTop: 2 }}>
              {profile.googleConnected ? profile.email : (lang === 'id' ? 'Belum login' : 'Not signed in')}
            </div>
          </div>
        </div>

        {/* Stats grid */}
        <div className="tf-stats">
          <div className="tf-stat">
            <div className="tf-stat-num">{profile.weight}<span>kg</span></div>
            <div className="tf-stat-lbl">{t('weight')}</div>
          </div>
          <div className="tf-stat">
            <div className="tf-stat-num">{profile.targetWeight}<span>kg</span></div>
            <div className="tf-stat-lbl">{t('target_weight')}</div>
          </div>
          <div className="tf-stat">
            <div className="tf-stat-num">{profile.height}<span>cm</span></div>
            <div className="tf-stat-lbl">{t('height')}</div>
          </div>
          <div className="tf-stat">
            <div className="tf-stat-num">{profile.age}</div>
            <div className="tf-stat-lbl">{t('age')}</div>
          </div>
        </div>
      </Card>

      {/* Daily goal card */}
      <Card style={{ margin: '12px 16px 0', padding: '16px 18px', background: 'linear-gradient(135deg, var(--primary) 0%, var(--primary-deep) 100%)', color: '#fff', border: 'none', position: 'relative' }}>
        <button className="tf-edit-btn" onClick={() => dispatch({ type: 'showOnboarding' })}
                style={{ position: 'absolute', top: 14, right: 14 }}>
          {t('edit')}
        </button>
        <div style={{ fontSize: 10, fontWeight: 600, opacity: 0.85, letterSpacing: '0.06em', textTransform: 'uppercase' }}>
          {t('daily_calorie_goal')}
        </div>
        <div style={{ fontFamily: 'var(--font-display)', fontSize: 40, fontWeight: 500, marginTop: 6, lineHeight: 1, letterSpacing: '-0.02em', fontVariantNumeric: 'tabular-nums' }}>
          {fmtNum(profile.dailyGoal)} <span style={{ fontSize: 16, opacity: 0.8, fontFamily: 'var(--font-body)', fontWeight: 600 }}>kkal</span>
        </div>
        <div style={{ fontSize: 12, opacity: 0.85, marginTop: 8 }}>
          {t(`activity_${profile.activity}`)} · {t(`goal_${profile.goal}`)}
        </div>
      </Card>

      {/* Connect Google */}
      {!profile.googleConnected && (
        <Card style={{ margin: '12px 16px 0', padding: '16px', display: 'flex', alignItems: 'center', gap: 12 }}>
          <IconGoogle size={28}/>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 14, fontWeight: 600, color: 'var(--ink)' }}>{t('connect_google')}</div>
            <div style={{ fontSize: 12, color: 'var(--ink-3)', marginTop: 2 }}>{t('connect_google_desc')}</div>
          </div>
          <button className="tf-btn-sm" onClick={() => dispatch({ type: 'showGoogle' })}>
            {t('add')}
          </button>
        </Card>
      )}

      {/* Settings list */}
      <div style={{ padding: '14px 16px 0' }}>
        <div style={{ fontSize: 10, fontWeight: 600, color: 'var(--ink-3)', letterSpacing: '0.05em', textTransform: 'uppercase', padding: '0 6px 6px' }}>
          {t('settings')}
        </div>
        <Card style={{ overflow: 'hidden' }}>
          <SettingRow icon={<IconBell size={16}/>} label={t('notifications')} value="08:00" />
          <SettingRow icon={<IconGlobe size={16}/>} label={t('units')} value="metric" />
          <SettingRow icon={<IconUser size={16}/>} label={t('privacy')} />
          <SettingRow icon={<IconSparkles size={16}/>} label={t('about')} isLast />
        </Card>
      </div>

      <div style={{ height: 20 }}/>
    </div>
  );
}

function SettingRow({ icon, label, value, isLast }) {
  return (
    <div className="tf-set-row" style={{ borderBottom: isLast ? 'none' : '1px solid var(--hairline)' }}>
      <div className="tf-icon-tile small">{icon}</div>
      <div style={{ flex: 1, fontSize: 14, fontWeight: 500, color: 'var(--ink)' }}>{label}</div>
      {value && <div style={{ fontSize: 13, color: 'var(--ink-3)', marginRight: 6 }}>{value}</div>}
      <IconChevronRight size={14} weight={2.4}/>
    </div>
  );
}

Object.assign(window, {
  Card, Tag, MacroBar, CalorieRing, FoodRow,
  HomeScreen, SearchScreen, ProgressScreen, ProfileScreen,
});

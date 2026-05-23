// modals.jsx — AddSheet, ScanModal, DescribeModal, OnboardingModal, GoogleModal, FoodDetailModal

// ─────────────────────────────────────────────────────────────
// Sheet wrapper (slides up from bottom inside the iOS frame)
// ─────────────────────────────────────────────────────────────
function Sheet({ open, onClose, children, height = '90%', fullscreen = false }) {
  const [mounted, setMounted] = React.useState(open);
  const [visible, setVisible] = React.useState(open);

  React.useEffect(() => {
    if (open) {
      setMounted(true);
      // double rAF + setTimeout fallback to flip into visible state on next frame
      let id1, id2, id3;
      id1 = setTimeout(() => {
        id2 = setTimeout(() => setVisible(true), 0);
      }, 0);
      // Hard fallback — in throttled iframes timers may be slow but eventually fire
      id3 = setTimeout(() => setVisible(true), 80);
      return () => { clearTimeout(id1); clearTimeout(id2); clearTimeout(id3); };
    } else if (mounted) {
      setVisible(false);
      const id = setTimeout(() => setMounted(false), 280);
      return () => clearTimeout(id);
    }
  }, [open]);

  if (!mounted) return null;

  return (
    <div className="tf-sheet-root">
      <div className="tf-sheet-scrim" data-on={visible} onClick={onClose}/>
      <div className={`tf-sheet ${fullscreen ? 'full' : ''}`}
           data-on={visible}
           style={{
             height: fullscreen ? '100%' : height,
             transform: visible ? 'translateY(0)' : 'translateY(100%)',
           }}>
        {!fullscreen && <div className="tf-sheet-handle"/>}
        {children}
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// Add sheet — choose Scan / Describe / Search
// ─────────────────────────────────────────────────────────────
function AddSheet({ state, dispatch }) {
  const { lang, addSheet } = state;
  const t = useT(lang);

  const opts = [
    { id: 'scan',     icon: <IconCamera size={22}/>,    title: t('add_scan'),     desc: t('add_scan_desc'),     bg: 'var(--soft)',     fg: 'var(--primary-deep)', sparkle: true },
    { id: 'describe', icon: <IconType size={22}/>,      title: t('add_describe'), desc: t('add_describe_desc'), bg: '#fef4e3',          fg: '#9c5a09',             sparkle: true },
    { id: 'search',   icon: <IconSearch size={22}/>,    title: t('add_search'),   desc: t('add_search_desc'),   bg: '#eef2fb',          fg: '#3c5b9c',             sparkle: false },
  ];

  return (
    <Sheet open={!!addSheet} onClose={() => dispatch({ type: 'closeAdd' })} height="62%">
      <div style={{ padding: '4px 24px 24px' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 4 }}>
          <h2 style={{ fontFamily: 'var(--font-display)', fontSize: 24, fontWeight: 500, margin: 0, color: 'var(--ink)', letterSpacing: '-0.02em', whiteSpace: 'nowrap' }}>
            {t('add_title')}
          </h2>
          <button className="tf-icon-btn" onClick={() => dispatch({ type: 'closeAdd' })}><IconX size={16}/></button>
        </div>
        <div style={{ fontSize: 13, color: 'var(--ink-3)', marginBottom: 20 }}>
          {t('add_subtitle')}
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          {opts.map(o => (
            <button key={o.id} className="tf-add-opt"
                    onClick={() => dispatch({ type: 'openAddMethod', method: o.id })}>
              <div className="tf-add-opt-icon" style={{ background: o.bg, color: o.fg }}>
                {o.icon}
                {o.sparkle && <span className="tf-add-opt-sparkle"><IconSparkles size={10} weight={2.5}/></span>}
              </div>
              <div style={{ flex: 1, textAlign: 'left' }}>
                <div style={{ fontSize: 15, fontWeight: 600, color: 'var(--ink)', display: 'flex', alignItems: 'center', gap: 6 }}>
                  {o.title}
                  {o.sparkle && <span className="tf-ai-pill">AI</span>}
                </div>
                <div style={{ fontSize: 12, color: 'var(--ink-3)', marginTop: 2 }}>{o.desc}</div>
              </div>
              <IconChevronRight size={16} weight={2.4}/>
            </button>
          ))}
        </div>
      </div>
    </Sheet>
  );
}

// ─────────────────────────────────────────────────────────────
// Scan modal — fake camera → AI analyzing → results
// ─────────────────────────────────────────────────────────────

// 3 pre-set sample "plates" — composed with CSS, no images needed
const SAMPLE_PLATES = [
  {
    id: 'plate1',
    title_id: 'Nasi padang lengkap',
    title_en: 'Padang rice plate',
    bg: 'linear-gradient(135deg, #f9b27d 0%, #d97744 60%, #8a3f1a 100%)',
    items: ['nasi-putih', 'rendang', 'tempe-orek', 'toge'],
    confidence: 0.94,
  },
  {
    id: 'plate2',
    title_id: 'Ayam goreng + nasi',
    title_en: 'Fried chicken plate',
    bg: 'linear-gradient(140deg, #f4d68d 0%, #b87432 70%, #5c3413 100%)',
    items: ['nasi-putih', 'ayam-goreng', 'tahu-goreng'],
    confidence: 0.91,
  },
  {
    id: 'plate3',
    title_id: 'Bakso + es teh',
    title_en: 'Meatball soup + iced tea',
    bg: 'linear-gradient(135deg, #c69f6a 0%, #6e3c1a 100%)',
    items: ['bakso', 'kerupuk', 'es-teh-manis'],
    confidence: 0.88,
  },
];

function ScanModal({ state, dispatch }) {
  const { lang, scanOpen, lastMeal } = state;
  const t = useT(lang);

  // phase: 'aim' → 'analyze' → 'result'
  const [phase, setPhase] = React.useState('aim');
  const [plateIdx, setPlateIdx] = React.useState(0);
  const [detected, setDetected] = React.useState([]);
  const [meal, setMeal] = React.useState(lastMeal || 'makan_siang');

  React.useEffect(() => {
    if (scanOpen) {
      setPhase('aim');
      setDetected([]);
      setMeal(lastMeal || 'makan_siang');
    }
  }, [scanOpen]);

  const startAnalyze = () => {
    setPhase('analyze');
    const plate = SAMPLE_PLATES[plateIdx];
    // Simulate AI detection with reveal animation
    setTimeout(() => {
      const items = plate.items.map((id, i) => {
        const f = FOOD_BY_ID[id];
        return {
          uid: 'd_' + Math.random().toString(36).slice(2, 8),
          foodId: id,
          name: lang === 'id' ? f.name : f.name_en,
          emoji: f.emoji,
          serving: lang === 'id' ? f.serving : f.serving_en,
          qty: 1,
          kcal: f.kcal, p: f.p, c: f.c, f: f.f,
          confidence: plate.confidence - (i * 0.02),
        };
      });
      setDetected(items);
      setPhase('result');
    }, 2400);
  };

  const adjustQty = (uid, delta) => {
    setDetected(prev => prev.map(it => {
      if (it.uid !== uid) return it;
      const newQty = Math.max(0.25, Math.min(5, Math.round((it.qty + delta) * 4) / 4));
      const ratio = newQty / it.qty;
      return { ...it, qty: newQty, kcal: it.kcal * ratio, p: it.p * ratio, c: it.c * ratio, f: it.f * ratio };
    }));
  };

  const removeDetected = (uid) => {
    setDetected(prev => prev.filter(it => it.uid !== uid));
  };

  const handleConfirm = () => {
    detected.forEach(it => {
      dispatch({
        type: 'addItem',
        item: {
          uid: 'i_' + Math.random().toString(36).slice(2, 8),
          name: it.name, emoji: it.emoji, serving: it.serving, qty: it.qty,
          kcal: it.kcal, p: it.p, c: it.c, f: it.f, meal,
        },
      });
    });
    dispatch({ type: 'closeScan' });
  };

  const plate = SAMPLE_PLATES[plateIdx];
  const totalKcal = detected.reduce((s, it) => s + it.kcal, 0);

  return (
    <Sheet open={scanOpen} onClose={() => dispatch({ type: 'closeScan' })} fullscreen>
      <div className="tf-scan-root">
        {/* Header */}
        <div className="tf-scan-header">
          <button className="tf-icon-btn dark" onClick={() => dispatch({ type: 'closeScan' })}>
            <IconX size={18}/>
          </button>
          <div style={{ flex: 1, textAlign: 'center', color: '#fff', fontSize: 14, fontWeight: 600 }}>
            {phase === 'result' ? t('scan_review') : t('scan_title')}
          </div>
          <div style={{ width: 36 }}/>
        </div>

        {/* AIM phase — camera viewfinder */}
        {phase === 'aim' && (
          <div className="tf-scan-aim">
            <div className="tf-viewfinder" style={{ background: plate.bg }}>
              {/* Stylized "plate" — visual placeholder */}
              <div className="tf-plate">
                {plate.items.slice(0, 4).map((id, i) => (
                  <div key={i} className="tf-plate-blob" style={{
                    background: ['#f9e2b6', '#d9a85d', '#a86d3d', '#7c4423', '#c4855a'][i % 5],
                    transform: `translate(${[-30, 30, -25, 28][i] || 0}px, ${[-25, -20, 30, 25][i] || 0}px) rotate(${i * 30}deg)`,
                  }}/>
                ))}
                <div className="tf-plate-rim"/>
              </div>
              {/* Frame guides */}
              <div className="tf-viewfinder-frame">
                <span/><span/><span/><span/>
              </div>
            </div>

            {/* Plate picker */}
            <div className="tf-plate-picker">
              <div className="tf-plate-picker-label">{t('scan_sample')}</div>
              <div className="tf-plate-picker-row">
                {SAMPLE_PLATES.map((p, i) => (
                  <button key={p.id}
                          className={`tf-plate-pick ${i === plateIdx ? 'on' : ''}`}
                          style={{ background: p.bg }}
                          onClick={() => setPlateIdx(i)}>
                    {i === plateIdx && <IconCheck size={14} color="#fff"/>}
                  </button>
                ))}
              </div>
              <div style={{ fontSize: 12, color: '#fff', opacity: 0.7, textAlign: 'center', marginTop: 6 }}>
                {lang === 'id' ? plate.title_id : plate.title_en}
              </div>
            </div>

            {/* Capture button */}
            <div className="tf-capture-row">
              <div style={{ width: 44 }}/>
              <button className="tf-capture-btn" onClick={startAnalyze}>
                <div className="tf-capture-inner"/>
              </button>
              <div style={{ width: 44 }}/>
            </div>
          </div>
        )}

        {/* ANALYZE phase */}
        {phase === 'analyze' && (
          <div className="tf-scan-analyzing">
            <div className="tf-analyze-img" style={{ background: plate.bg }}>
              <div className="tf-plate small">
                {plate.items.slice(0, 4).map((id, i) => (
                  <div key={i} className="tf-plate-blob" style={{
                    background: ['#f9e2b6', '#d9a85d', '#a86d3d', '#7c4423', '#c4855a'][i % 5],
                    transform: `translate(${[-22, 22, -18, 20][i] || 0}px, ${[-18, -14, 22, 18][i] || 0}px)`,
                  }}/>
                ))}
                <div className="tf-plate-rim"/>
              </div>
              {/* Scanning sweep */}
              <div className="tf-scan-sweep"/>
              {/* Floating labels */}
              {plate.items.map((id, i) => {
                const f = FOOD_BY_ID[id];
                const pos = [
                  { top: '15%', left: '12%' },
                  { top: '20%', right: '10%' },
                  { bottom: '22%', left: '15%' },
                  { bottom: '15%', right: '14%' },
                ][i];
                return (
                  <div key={id} className="tf-scan-tag" style={{ ...pos, animationDelay: `${i * 0.35}s` }}>
                    {f.emoji} {lang === 'id' ? f.name : f.name_en}
                  </div>
                );
              })}
            </div>
            <div className="tf-analyze-text">
              <div className="tf-ai-dots"><span/><span/><span/></div>
              <div style={{ color: '#fff', fontSize: 17, fontWeight: 600, marginTop: 14, letterSpacing: '-0.01em' }}>
                {t('scan_analyzing')}
              </div>
              <div style={{ color: 'rgba(255,255,255,0.6)', fontSize: 12, marginTop: 6 }}>
                {lang === 'id' ? 'Mengenali makanan & menghitung kalori' : 'Identifying items & estimating calories'}
              </div>
            </div>
          </div>
        )}

        {/* RESULT phase */}
        {phase === 'result' && (
          <div className="tf-scan-result">
            <div className="tf-result-img" style={{ background: plate.bg }}>
              <div className="tf-plate small">
                {plate.items.slice(0, 4).map((id, i) => (
                  <div key={i} className="tf-plate-blob" style={{
                    background: ['#f9e2b6', '#d9a85d', '#a86d3d', '#7c4423', '#c4855a'][i % 5],
                    transform: `translate(${[-22, 22, -18, 20][i] || 0}px, ${[-18, -14, 22, 18][i] || 0}px)`,
                  }}/>
                ))}
                <div className="tf-plate-rim"/>
              </div>
              <div className="tf-result-summary">
                <div style={{ fontSize: 11, color: 'rgba(255,255,255,0.75)', fontWeight: 600, letterSpacing: '0.05em', textTransform: 'uppercase' }}>
                  {t('scan_detected')} · {detected.length} item
                </div>
                <div style={{ fontFamily: 'var(--font-display)', fontSize: 36, fontWeight: 500, color: '#fff', lineHeight: 1, marginTop: 6, fontVariantNumeric: 'tabular-nums', letterSpacing: '-0.02em' }}>
                  {fmtNum(totalKcal)} <span style={{ fontSize: 16, opacity: 0.8 }}>kkal</span>
                </div>
              </div>
            </div>

            <div className="tf-result-body">
              {detected.map(it => (
                <DetectedRow key={it.uid} item={it} t={t}
                             onPlus={() => adjustQty(it.uid, 0.25)}
                             onMinus={() => adjustQty(it.uid, -0.25)}
                             onRemove={() => removeDetected(it.uid)}/>
              ))}

              <div style={{ marginTop: 14 }}>
                <div style={{ fontSize: 11, fontWeight: 600, color: 'var(--ink-3)', letterSpacing: '0.05em', textTransform: 'uppercase', marginBottom: 8 }}>
                  {t('scan_add_to')}
                </div>
                <div className="tf-meal-seg">
                  {['sarapan', 'makan_siang', 'makan_malam', 'cemilan'].map(m => (
                    <button key={m} className={`tf-meal-seg-btn ${meal === m ? 'on' : ''}`}
                            onClick={() => setMeal(m)}>
                      {t(`meal_${m}`)}
                    </button>
                  ))}
                </div>
              </div>
            </div>

            <div className="tf-result-cta">
              <button className="tf-btn-primary" onClick={handleConfirm} disabled={detected.length === 0}>
                {t('add')} · {fmtNum(totalKcal)} kkal
              </button>
            </div>
          </div>
        )}
      </div>
    </Sheet>
  );
}

function DetectedRow({ item, t, onPlus, onMinus, onRemove }) {
  return (
    <div className="tf-detected-row">
      <div style={{ fontSize: 22, flexShrink: 0 }}>{item.emoji}</div>
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 6, minWidth: 0, flexWrap: 'wrap' }}>
          <div style={{ fontSize: 14, fontWeight: 600, color: 'var(--ink)' }}>
            {item.name}
          </div>
          <Tag tone="ghost" style={{ fontSize: 9, padding: '2px 6px', flexShrink: 0 }}>
            {Math.round(item.confidence * 100)}%
          </Tag>
        </div>
        <div style={{ fontSize: 11, color: 'var(--ink-3)', marginTop: 2 }}>
          {item.qty !== 1 ? `${item.qty}× ` : ''}{item.serving} · {fmtNum(item.kcal)} kkal
        </div>
      </div>
      <div className="tf-qty">
        <button onClick={onMinus} disabled={item.qty <= 0.25}>−</button>
        <span style={{ fontVariantNumeric: 'tabular-nums', minWidth: 22, textAlign: 'center' }}>{item.qty}</span>
        <button onClick={onPlus}>+</button>
      </div>
      <button className="tf-row-del" onClick={onRemove}><IconX size={12}/></button>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// Describe modal — AI text → parsed items
// ─────────────────────────────────────────────────────────────
function DescribeModal({ state, dispatch }) {
  const { lang, describeOpen, lastMeal } = state;
  const t = useT(lang);

  const [text, setText] = React.useState('');
  const [parsing, setParsing] = React.useState(false);
  const [items, setItems] = React.useState([]);
  const [meal, setMeal] = React.useState(lastMeal || 'makan_siang');
  const [error, setError] = React.useState(null);

  React.useEffect(() => {
    if (describeOpen) {
      setText('');
      setItems([]);
      setParsing(false);
      setError(null);
      setMeal(lastMeal || 'makan_siang');
    }
  }, [describeOpen]);

  const EXAMPLES = lang === 'id'
    ? ['nasi goreng setengah porsi', 'ayam goreng 1 potong dan tempe', '2 tusuk sate + es teh manis']
    : ['half portion fried rice', 'fried chicken with tempeh', '2 satay skewers + iced sweet tea'];

  const handleParse = async () => {
    if (!text.trim()) return;
    setParsing(true);
    setError(null);

    // Build food list for prompt
    const foodList = FOODS.map(f =>
      `${f.id}: ${f.name} (${f.name_en}), ${f.serving}, ${f.kcal}kcal, P${f.p} C${f.c} F${f.f}, ${f.emoji}`
    ).join('\n');

    const prompt = `You are a nutritionist parsing Indonesian food descriptions into structured logs.

User said (in Indonesian or English): "${text}"

Match each item to the food database below. Use qty (0.25, 0.5, 1, 1.5, 2 etc) to represent portion size relative to the listed serving (e.g. "setengah porsi" = 0.5, "1 piring" or "1 porsi" = 1, "2 potong" of a 1-piece serving = 2). For items not in the database, use foodId: "custom" and make a reasonable estimate.

Database:
${foodList}

Respond with ONLY a JSON array, no prose, no markdown. Format:
[{"foodId":"ayam-goreng","qty":1},{"foodId":"nasi-putih","qty":0.5},{"foodId":"custom","name":"Sambal","name_en":"Chili Paste","emoji":"🌶️","serving":"1 sdm","serving_en":"1 tbsp","qty":1,"kcal":15,"p":0,"c":3,"f":0}]`;

    let parsed = null;
    try {
      const resp = await Promise.race([
        window.claude.complete(prompt),
        new Promise((_, rej) => setTimeout(() => rej(new Error('timeout')), 12000)),
      ]);
      // Extract JSON array
      const m = resp.match(/\[[\s\S]*\]/);
      if (!m) throw new Error('no json');
      parsed = JSON.parse(m[0]);
      if (!Array.isArray(parsed) || parsed.length === 0) throw new Error('empty');
    } catch (e) {
      // Scripted fallback — naive keyword match
      parsed = scriptedFallback(text);
    }

    const newItems = parsed.map((p, i) => {
      let item;
      if (p.foodId === 'custom') {
        item = {
          uid: 'd_' + Math.random().toString(36).slice(2, 8),
          name: lang === 'id' ? (p.name || 'Custom') : (p.name_en || p.name || 'Custom'),
          emoji: p.emoji || '🍽️',
          serving: lang === 'id' ? (p.serving || '1 porsi') : (p.serving_en || p.serving || '1 portion'),
          qty: p.qty || 1,
          kcal: (p.kcal || 100) * (p.qty || 1),
          p: (p.p || 0) * (p.qty || 1),
          c: (p.c || 0) * (p.qty || 1),
          f: (p.f || 0) * (p.qty || 1),
        };
      } else {
        const f = FOOD_BY_ID[p.foodId];
        if (!f) return null;
        const qty = p.qty || 1;
        item = {
          uid: 'd_' + Math.random().toString(36).slice(2, 8),
          name: lang === 'id' ? f.name : f.name_en,
          emoji: f.emoji,
          serving: lang === 'id' ? f.serving : f.serving_en,
          qty,
          kcal: f.kcal * qty, p: f.p * qty, c: f.c * qty, f: f.f * qty,
        };
      }
      return item;
    }).filter(Boolean);

    setItems(newItems);
    setParsing(false);
  };

  const scriptedFallback = (s) => {
    const l = s.toLowerCase();
    const matched = [];
    for (const f of FOODS) {
      const tokens = [f.name.toLowerCase(), f.name_en.toLowerCase()];
      // also tokens from name without spaces
      tokens.push(f.name.toLowerCase().replace(/\s+/g, ''));
      if (tokens.some(tok => l.includes(tok))) {
        // qty detection
        let qty = 1;
        if (/setengah|half|1\/2|0[.,]5/.test(l)) qty = 0.5;
        if (new RegExp(`(\\d+)\\s*(potong|tusuk|porsi|gelas|mangkok|buah|piece|portion|skewer|glass|bowl).{0,15}${f.name.toLowerCase().split(/\s/)[0]}`).test(l)) {
          const m = l.match(new RegExp(`(\\d+)\\s*(potong|tusuk|porsi|gelas|mangkok|buah|piece|portion|skewer|glass|bowl)`));
          if (m) qty = parseInt(m[1]);
        }
        matched.push({ foodId: f.id, qty });
      }
    }
    return matched.length > 0 ? matched : [{ foodId: 'nasi-putih', qty: 1 }];
  };

  const adjustQty = (uid, delta) => {
    setItems(prev => prev.map(it => {
      if (it.uid !== uid) return it;
      const newQty = Math.max(0.25, Math.min(5, Math.round((it.qty + delta) * 4) / 4));
      const ratio = newQty / it.qty;
      return { ...it, qty: newQty, kcal: it.kcal * ratio, p: it.p * ratio, c: it.c * ratio, f: it.f * ratio };
    }));
  };

  const remove = (uid) => setItems(prev => prev.filter(it => it.uid !== uid));

  const totalKcal = items.reduce((s, it) => s + it.kcal, 0);

  const handleConfirm = () => {
    items.forEach(it => dispatch({
      type: 'addItem',
      item: { ...it, meal },
    }));
    dispatch({ type: 'closeDescribe' });
  };

  return (
    <Sheet open={describeOpen} onClose={() => dispatch({ type: 'closeDescribe' })} fullscreen>
      <div className="tf-describe-root">
        <div className="tf-describe-header">
          <button className="tf-icon-btn" onClick={() => dispatch({ type: 'closeDescribe' })}>
            <IconX size={16}/>
          </button>
          <div style={{ flex: 1, textAlign: 'center', fontSize: 14, fontWeight: 600, color: 'var(--ink)' }}>
            {t('describe_title')}
          </div>
          <div style={{ width: 32 }}/>
        </div>

        <div className="tf-describe-body">
          {items.length === 0 && (
            <>
              <div className="tf-describe-hero">
                <div className="tf-ai-badge"><IconSparkles size={14}/> {t('describe_powered')}</div>
                <div style={{ fontFamily: 'var(--font-display)', fontSize: 24, fontWeight: 500, lineHeight: 1.2, marginTop: 14, color: 'var(--ink)', letterSpacing: '-0.02em' }}>
                  {lang === 'id' ? 'Tulis apa yang kamu makan, AI hitung kalorinya.' : 'Tell us what you ate, AI counts the calories.'}
                </div>
              </div>

              <div className="tf-describe-input-wrap">
                <textarea
                  value={text}
                  onChange={e => setText(e.target.value)}
                  placeholder={t('describe_placeholder')}
                  className="tf-describe-input"
                  rows={4}
                  disabled={parsing}
                />
              </div>

              <div style={{ marginTop: 14 }}>
                <div style={{ fontSize: 11, fontWeight: 600, color: 'var(--ink-3)', letterSpacing: '0.05em', textTransform: 'uppercase', marginBottom: 8 }}>
                  {t('try_example')}
                </div>
                <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
                  {EXAMPLES.map((ex, i) => (
                    <button key={i} className="tf-example-chip" onClick={() => setText(ex)}>
                      "{ex}"
                    </button>
                  ))}
                </div>
              </div>
            </>
          )}

          {items.length > 0 && (
            <>
              <div className="tf-describe-quote">
                <div style={{ fontSize: 11, fontWeight: 600, color: 'var(--ink-3)', letterSpacing: '0.05em', textTransform: 'uppercase', marginBottom: 6 }}>
                  {lang === 'id' ? 'Kamu menulis' : 'You wrote'}
                </div>
                <div style={{ fontSize: 14, color: 'var(--ink)', lineHeight: 1.4 }}>"{text}"</div>
              </div>

              <div style={{ marginTop: 16 }}>
                <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', marginBottom: 10, gap: 8 }}>
                  <div style={{ fontSize: 11, fontWeight: 600, color: 'var(--ink-3)', letterSpacing: '0.05em', textTransform: 'uppercase', whiteSpace: 'nowrap' }}>
                    {lang === 'id' ? 'AI menemukan' : 'AI found'}
                  </div>
                  <div style={{ fontVariantNumeric: 'tabular-nums', fontSize: 13, fontWeight: 600, color: 'var(--ink)', whiteSpace: 'nowrap' }}>
                    {fmtNum(totalKcal)} kkal
                  </div>
                </div>
                <Card style={{ overflow: 'hidden' }}>
                  {items.map((it, i) => (
                    <div key={it.uid} style={{ borderBottom: i === items.length - 1 ? 'none' : '1px solid var(--hairline)' }}>
                      <DetectedRow item={{ ...it, confidence: 0.95 }} t={t}
                                   onPlus={() => adjustQty(it.uid, 0.25)}
                                   onMinus={() => adjustQty(it.uid, -0.25)}
                                   onRemove={() => remove(it.uid)}/>
                    </div>
                  ))}
                </Card>
              </div>

              <div style={{ marginTop: 16 }}>
                <div style={{ fontSize: 11, fontWeight: 600, color: 'var(--ink-3)', letterSpacing: '0.05em', textTransform: 'uppercase', marginBottom: 8 }}>
                  {t('scan_add_to')}
                </div>
                <div className="tf-meal-seg">
                  {['sarapan', 'makan_siang', 'makan_malam', 'cemilan'].map(m => (
                    <button key={m} className={`tf-meal-seg-btn ${meal === m ? 'on' : ''}`}
                            onClick={() => setMeal(m)}>
                      {t(`meal_${m}`)}
                    </button>
                  ))}
                </div>
              </div>
            </>
          )}
        </div>

        <div className="tf-describe-cta">
          {items.length === 0 ? (
            <button className="tf-btn-primary" onClick={handleParse}
                    disabled={!text.trim() || parsing}>
              {parsing
                ? <><span className="tf-spinner"/>{t('scan_analyzing')}</>
                : <><IconSparkles size={16} weight={2.5}/> {t('describe_analyze')}</>}
            </button>
          ) : (
            <button className="tf-btn-primary" onClick={handleConfirm}>
              {t('add')} · {fmtNum(totalKcal)} kkal
            </button>
          )}
        </div>
      </div>
    </Sheet>
  );
}

// ─────────────────────────────────────────────────────────────
// Food detail modal — pick qty + meal, add
// ─────────────────────────────────────────────────────────────
function FoodDetailModal({ state, dispatch }) {
  const { lang, foodDetailId, lastMeal } = state;
  const t = useT(lang);
  const food = foodDetailId ? FOOD_BY_ID[foodDetailId] : null;
  const [qty, setQty] = React.useState(1);
  const [meal, setMeal] = React.useState(lastMeal || 'makan_siang');

  React.useEffect(() => {
    if (foodDetailId) {
      setQty(1);
      setMeal(lastMeal || 'makan_siang');
    }
  }, [foodDetailId]);

  if (!food) return <Sheet open={false} onClose={()=>{}}>{null}</Sheet>;

  const handleAdd = () => {
    dispatch({
      type: 'addItem',
      item: {
        uid: 'i_' + Math.random().toString(36).slice(2, 8),
        name: lang === 'id' ? food.name : food.name_en,
        emoji: food.emoji,
        serving: lang === 'id' ? food.serving : food.serving_en,
        qty,
        kcal: food.kcal * qty, p: food.p * qty, c: food.c * qty, f: food.f * qty,
        meal,
      },
    });
    dispatch({ type: 'closeFoodDetail' });
  };

  return (
    <Sheet open={!!foodDetailId} onClose={() => dispatch({ type: 'closeFoodDetail' })} height="auto">
      <div style={{ padding: '4px 24px 24px' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 10 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 12, flex: 1, minWidth: 0 }}>
            <div style={{ fontSize: 36, flexShrink: 0 }}>{food.emoji}</div>
            <div style={{ minWidth: 0, flex: 1 }}>
              <div style={{ fontFamily: 'var(--font-display)', fontSize: 22, fontWeight: 500, color: 'var(--ink)', letterSpacing: '-0.02em', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                {lang === 'id' ? food.name : food.name_en}
              </div>
              <div style={{ fontSize: 12, color: 'var(--ink-3)', marginTop: 1, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                {lang === 'id' ? food.serving : food.serving_en}
              </div>
            </div>
          </div>
          <button className="tf-icon-btn" onClick={() => dispatch({ type: 'closeFoodDetail' })} style={{ flexShrink: 0 }}><IconX size={16}/></button>
        </div>

        {/* Macro grid */}
        <div className="tf-macro-grid">
          <div className="tf-macro-cell big">
            <div className="tf-macro-num">{fmtNum(food.kcal * qty)}</div>
            <div className="tf-macro-lbl">kkal</div>
          </div>
          <div className="tf-macro-cell">
            <div className="tf-macro-num">{Math.round(food.p * qty)}<span>g</span></div>
            <div className="tf-macro-lbl">{t('protein')}</div>
          </div>
          <div className="tf-macro-cell">
            <div className="tf-macro-num">{Math.round(food.c * qty)}<span>g</span></div>
            <div className="tf-macro-lbl">{t('carbs')}</div>
          </div>
          <div className="tf-macro-cell">
            <div className="tf-macro-num">{Math.round(food.f * qty)}<span>g</span></div>
            <div className="tf-macro-lbl">{t('fat')}</div>
          </div>
        </div>

        {/* Qty stepper */}
        <div style={{ marginTop: 18 }}>
          <div style={{ fontSize: 11, fontWeight: 600, color: 'var(--ink-3)', letterSpacing: '0.05em', textTransform: 'uppercase', marginBottom: 8 }}>
            {t('portion')}
          </div>
          <div className="tf-qty-row">
            <button className="tf-qty-btn" onClick={() => setQty(Math.max(0.25, qty - 0.25))} disabled={qty <= 0.25}>−</button>
            <div style={{ flex: 1, textAlign: 'center', fontFamily: 'var(--font-display)', fontSize: 28, fontWeight: 500, color: 'var(--ink)', fontVariantNumeric: 'tabular-nums', letterSpacing: '-0.02em' }}>
              {qty}
            </div>
            <button className="tf-qty-btn" onClick={() => setQty(Math.min(10, qty + 0.25))}>+</button>
          </div>
        </div>

        {/* Meal */}
        <div style={{ marginTop: 14 }}>
          <div style={{ fontSize: 11, fontWeight: 600, color: 'var(--ink-3)', letterSpacing: '0.05em', textTransform: 'uppercase', marginBottom: 8 }}>
            {t('scan_add_to')}
          </div>
          <div className="tf-meal-seg">
            {['sarapan', 'makan_siang', 'makan_malam', 'cemilan'].map(m => (
              <button key={m} className={`tf-meal-seg-btn ${meal === m ? 'on' : ''}`}
                      onClick={() => setMeal(m)}>
                {t(`meal_${m}`)}
              </button>
            ))}
          </div>
        </div>

        <button className="tf-btn-primary" style={{ marginTop: 18 }} onClick={handleAdd}>
          {t('add')} · {fmtNum(food.kcal * qty)} kkal
        </button>
      </div>
    </Sheet>
  );
}

// ─────────────────────────────────────────────────────────────
// Onboarding — multi-step
// ─────────────────────────────────────────────────────────────
function OnboardingModal({ state, dispatch }) {
  const { lang, onboardingOpen, profile } = state;
  const t = useT(lang);

  const [step, setStep] = React.useState(0);
  const [draft, setDraft] = React.useState({
    name: profile.name || '',
    weight: profile.weight || 68,
    height: profile.height || 170,
    age: profile.age || 28,
    sex: profile.sex || 'male',
    targetWeight: profile.targetWeight || 64,
    goal: profile.goal || 'lose',
    pace: profile.pace || 'normal',
    activity: profile.activity || 'moderate',
  });

  React.useEffect(() => {
    if (onboardingOpen) {
      setStep(0);
      setDraft({
        name: profile.name || '',
        weight: profile.weight || 68,
        height: profile.height || 170,
        age: profile.age || 28,
        sex: profile.sex || 'male',
        targetWeight: profile.targetWeight || 64,
        goal: profile.goal || 'lose',
        pace: profile.pace || 'normal',
        activity: profile.activity || 'moderate',
      });
    }
  }, [onboardingOpen]);

  const calculatedGoal = calcDailyCalories(draft);

  const finish = () => {
    dispatch({
      type: 'completeOnboarding',
      profile: { ...draft, dailyGoal: calculatedGoal },
    });
  };

  const next = () => setStep(s => Math.min(4, s + 1));
  const back = () => setStep(s => Math.max(0, s - 1));

  const steps = [
    { title: t('onb_welcome_title'), sub: t('onb_welcome_sub') },
    { title: t('onb_step_about'),    sub: '' },
    { title: t('onb_step_goal'),     sub: '' },
    { title: t('onb_step_activity'), sub: '' },
    { title: t('onb_step_done'),     sub: t('onb_calc_sub') },
  ];

  return (
    <Sheet open={onboardingOpen} onClose={()=>{}} fullscreen>
      <div className="tf-onb-root">
        {/* Progress + back */}
        <div className="tf-onb-header">
          {step > 0 && step < 4 && (
            <button className="tf-icon-btn" onClick={back}><IconChevronLeft size={18}/></button>
          )}
          {(step === 0 || step === 4) && <div style={{ width: 32 }}/>}
          <div className="tf-onb-progress">
            {[0, 1, 2, 3, 4].map(i => (
              <div key={i} className={`tf-onb-dot ${i <= step ? 'on' : ''}`}/>
            ))}
          </div>
          <div style={{ width: 32 }}/>
        </div>

        <div className="tf-onb-body">
          {step === 0 && (
            <div className="tf-onb-welcome">
              <div className="tf-onb-logo"><IconLogo size={80} color="var(--primary)"/></div>
              <h1 style={{ fontFamily: 'var(--font-display)', fontSize: 32, fontWeight: 500, margin: '24px 0 8px', textAlign: 'center', color: 'var(--ink)', letterSpacing: '-0.02em' }}>
                {t('onb_welcome_title')}
              </h1>
              <p style={{ fontSize: 15, color: 'var(--ink-2)', textAlign: 'center', lineHeight: 1.45, margin: 0, padding: '0 20px' }}>
                {t('onb_welcome_sub')}
              </p>

              <div className="tf-onb-features">
                <div className="tf-onb-feature">
                  <div className="tf-icon-tile" style={{ background: 'var(--soft)', color: 'var(--primary-deep)' }}><IconCamera size={18}/></div>
                  <div>
                    <div style={{ fontSize: 14, fontWeight: 600 }}>{t('add_scan')}</div>
                    <div style={{ fontSize: 12, color: 'var(--ink-3)', marginTop: 2 }}>{t('add_scan_desc')}</div>
                  </div>
                </div>
                <div className="tf-onb-feature">
                  <div className="tf-icon-tile" style={{ background: '#fef4e3', color: '#9c5a09' }}><IconSparkles size={18}/></div>
                  <div>
                    <div style={{ fontSize: 14, fontWeight: 600 }}>{t('add_describe')}</div>
                    <div style={{ fontSize: 12, color: 'var(--ink-3)', marginTop: 2 }}>{t('add_describe_desc')}</div>
                  </div>
                </div>
                <div className="tf-onb-feature">
                  <div className="tf-icon-tile" style={{ background: '#eef2fb', color: '#3c5b9c' }}><IconBolt size={18}/></div>
                  <div>
                    <div style={{ fontSize: 14, fontWeight: 600 }}>{lang === 'id' ? 'Tanpa login' : 'No login needed'}</div>
                    <div style={{ fontSize: 12, color: 'var(--ink-3)', marginTop: 2 }}>
                      {lang === 'id' ? 'Langsung pakai, sambungkan Google nanti' : 'Just start, connect Google later'}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}

          {step === 1 && (
            <div className="tf-onb-step">
              <h2 className="tf-onb-title">{t('onb_step_about')}</h2>
              <p className="tf-onb-sub">{lang === 'id' ? 'Bantu kami menghitung kebutuhan kalorimu' : 'Helps us calculate your calorie needs'}</p>

              <div className="tf-field-group">
                <label className="tf-field-lbl">{lang === 'id' ? 'Nama panggilan' : 'Nickname'}</label>
                <input className="tf-field-input" placeholder={lang === 'id' ? 'mis. Andi' : 'e.g. Andi'}
                       value={draft.name}
                       onChange={e => setDraft({ ...draft, name: e.target.value })}/>
              </div>

              <div className="tf-field-group">
                <label className="tf-field-lbl">{t('sex')}</label>
                <div className="tf-seg">
                  <button className={`tf-seg-btn ${draft.sex === 'male' ? 'on' : ''}`} onClick={() => setDraft({ ...draft, sex: 'male' })}>{t('male')}</button>
                  <button className={`tf-seg-btn ${draft.sex === 'female' ? 'on' : ''}`} onClick={() => setDraft({ ...draft, sex: 'female' })}>{t('female')}</button>
                </div>
              </div>

              <div style={{ display: 'flex', gap: 10 }}>
                <NumField label={t('age')} value={draft.age} min={14} max={90}
                          onChange={v => setDraft({ ...draft, age: v })}/>
                <NumField label={`${t('height')} (cm)`} value={draft.height} min={140} max={210}
                          onChange={v => setDraft({ ...draft, height: v })}/>
              </div>
              <div style={{ marginTop: 12 }}>
                <NumField label={`${t('weight')} (kg)`} value={draft.weight} min={35} max={200} step={0.5}
                          onChange={v => setDraft({ ...draft, weight: v })}/>
              </div>
            </div>
          )}

          {step === 2 && (
            <div className="tf-onb-step">
              <h2 className="tf-onb-title">{t('onb_step_goal')}</h2>
              <p className="tf-onb-sub">{lang === 'id' ? 'Apa tujuanmu?' : 'What\'s your goal?'}</p>

              <div className="tf-radio-list">
                {['lose', 'maintain', 'gain'].map(g => (
                  <button key={g} className={`tf-radio-card ${draft.goal === g ? 'on' : ''}`}
                          onClick={() => setDraft({ ...draft, goal: g })}>
                    <div style={{ fontSize: 22 }}>{g === 'lose' ? '📉' : g === 'gain' ? '📈' : '⚖️'}</div>
                    <div style={{ flex: 1, textAlign: 'left' }}>
                      <div style={{ fontSize: 15, fontWeight: 600, color: 'var(--ink)' }}>{t(`goal_${g}`)}</div>
                    </div>
                    <div className={`tf-radio-dot ${draft.goal === g ? 'on' : ''}`}/>
                  </button>
                ))}
              </div>

              {draft.goal !== 'maintain' && (
                <div className="tf-field-group" style={{ marginTop: 8 }}>
                  <label className="tf-field-lbl">{lang === 'id' ? 'Kecepatan' : 'Pace'}</label>
                  <div className="tf-seg">
                    <button className={`tf-seg-btn ${draft.pace === 'slow' ? 'on' : ''}`} onClick={() => setDraft({ ...draft, pace: 'slow' })}>{t('pace_slow')}</button>
                    <button className={`tf-seg-btn ${draft.pace === 'normal' ? 'on' : ''}`} onClick={() => setDraft({ ...draft, pace: 'normal' })}>{t('pace_normal')}</button>
                    <button className={`tf-seg-btn ${draft.pace === 'fast' ? 'on' : ''}`} onClick={() => setDraft({ ...draft, pace: 'fast' })}>{t('pace_fast')}</button>
                  </div>
                </div>
              )}

              <NumField label={`${t('target_weight')} (kg)`} value={draft.targetWeight} min={35} max={200} step={0.5}
                        onChange={v => setDraft({ ...draft, targetWeight: v })}/>
            </div>
          )}

          {step === 3 && (
            <div className="tf-onb-step">
              <h2 className="tf-onb-title">{t('onb_step_activity')}</h2>
              <p className="tf-onb-sub">{lang === 'id' ? 'Seberapa aktif kamu sehari-hari?' : 'How active are you day to day?'}</p>

              <div className="tf-radio-list">
                {['sedentary', 'light', 'moderate', 'active', 'very_active'].map(a => (
                  <button key={a} className={`tf-radio-card ${draft.activity === a ? 'on' : ''}`}
                          onClick={() => setDraft({ ...draft, activity: a })}>
                    <div style={{ flex: 1, textAlign: 'left' }}>
                      <div style={{ fontSize: 15, fontWeight: 600, color: 'var(--ink)' }}>{t(`activity_${a}`)}</div>
                      <div style={{ fontSize: 12, color: 'var(--ink-3)', marginTop: 2 }}>{t(`activity_${a}_desc`)}</div>
                    </div>
                    <div className={`tf-radio-dot ${draft.activity === a ? 'on' : ''}`}/>
                  </button>
                ))}
              </div>
            </div>
          )}

          {step === 4 && (
            <div className="tf-onb-step tf-onb-result">
              <div style={{ fontSize: 11, fontWeight: 600, color: 'var(--primary-deep)', letterSpacing: '0.05em', textTransform: 'uppercase', textAlign: 'center' }}>
                {t('onb_calc_title')}
              </div>
              <div style={{ fontFamily: 'var(--font-display)', fontSize: 72, fontWeight: 500, textAlign: 'center', lineHeight: 1, marginTop: 12, color: 'var(--ink)', letterSpacing: '-0.03em', fontVariantNumeric: 'tabular-nums' }}>
                {fmtNum(calculatedGoal)}
              </div>
              <div style={{ textAlign: 'center', fontSize: 13, color: 'var(--ink-3)', marginTop: 4 }}>
                kkal · {lang === 'id' ? 'per hari' : 'per day'}
              </div>
              <p style={{ fontSize: 13, color: 'var(--ink-2)', textAlign: 'center', lineHeight: 1.5, marginTop: 20, padding: '0 16px' }}>
                {t('onb_calc_sub')}
              </p>

              <div className="tf-onb-summary">
                <div className="tf-onb-sum-row"><span>{t('goals')}</span><strong>{t(`goal_${draft.goal}`)}</strong></div>
                <div className="tf-onb-sum-row"><span>{t('activity')}</span><strong>{t(`activity_${draft.activity}`)}</strong></div>
                {draft.goal !== 'maintain' && (
                  <div className="tf-onb-sum-row"><span>{lang === 'id' ? 'Kecepatan' : 'Pace'}</span><strong>{t(`pace_${draft.pace}`)}</strong></div>
                )}
              </div>
            </div>
          )}
        </div>

        <div className="tf-onb-cta">
          {step === 0 && <button className="tf-btn-primary" onClick={next}>{t('onb_start')}</button>}
          {step > 0 && step < 4 && <button className="tf-btn-primary" onClick={next}>{t('next')}</button>}
          {step === 4 && <button className="tf-btn-primary" onClick={finish}>{t('onb_done_cta')}</button>}
        </div>
      </div>
    </Sheet>
  );
}

function NumField({ label, value, onChange, min = 0, max = 999, step = 1 }) {
  const change = (delta) => {
    const v = Math.max(min, Math.min(max, +(value + delta).toFixed(2)));
    onChange(v);
  };
  return (
    <div className="tf-numfield">
      <label className="tf-field-lbl">{label}</label>
      <div className="tf-numfield-row">
        <button onClick={() => change(-step)} disabled={value <= min}>−</button>
        <input type="number" value={value} onChange={e => onChange(+e.target.value || min)} min={min} max={max} step={step}/>
        <button onClick={() => change(step)} disabled={value >= max}>+</button>
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// Google Connect modal
// ─────────────────────────────────────────────────────────────
function GoogleModal({ state, dispatch }) {
  const { lang, googleOpen, profile } = state;
  const t = useT(lang);
  const [phase, setPhase] = React.useState('intro'); // intro | connecting | done

  React.useEffect(() => {
    if (googleOpen) setPhase('intro');
  }, [googleOpen]);

  const connect = () => {
    setPhase('connecting');
    setTimeout(() => {
      dispatch({ type: 'connectGoogle', name: profile.name || 'Andi', email: 'andi.pratama@gmail.com' });
      setPhase('done');
      setTimeout(() => dispatch({ type: 'closeGoogle' }), 1100);
    }, 1300);
  };

  return (
    <Sheet open={googleOpen} onClose={() => phase !== 'connecting' && dispatch({ type: 'closeGoogle' })} height="auto">
      <div style={{ padding: '8px 24px 24px', textAlign: 'center' }}>
        {phase === 'intro' && (
          <>
            <div style={{ display: 'flex', justifyContent: 'center', marginTop: 8 }}>
              <div style={{ width: 64, height: 64, borderRadius: 16, background: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', boxShadow: '0 2px 12px rgba(0,0,0,0.08)' }}>
                <IconGoogle size={36}/>
              </div>
            </div>
            <h2 style={{ fontFamily: 'var(--font-display)', fontSize: 24, fontWeight: 500, margin: '20px 0 6px', color: 'var(--ink)', letterSpacing: '-0.02em' }}>
              {t('gconn_title')}
            </h2>
            <p style={{ fontSize: 14, color: 'var(--ink-2)', lineHeight: 1.5, margin: '0 0 20px' }}>{t('gconn_sub')}</p>

            <div style={{ display: 'flex', flexDirection: 'column', gap: 10, textAlign: 'left', marginBottom: 20 }}>
              {[t('gconn_b1'), t('gconn_b2'), t('gconn_b3')].map((b, i) => (
                <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                  <div className="tf-check"><IconCheck size={12} weight={3}/></div>
                  <div style={{ fontSize: 13, color: 'var(--ink)' }}>{b}</div>
                </div>
              ))}
            </div>

            <button className="tf-btn-google" onClick={connect}>
              <IconGoogle size={18}/>
              {t('gconn_cta')}
            </button>
            <button className="tf-btn-text" onClick={() => dispatch({ type: 'closeGoogle' })}>
              {t('gconn_skip')}
            </button>
          </>
        )}
        {phase === 'connecting' && (
          <div style={{ padding: '40px 0' }}>
            <div className="tf-spinner big"/>
            <div style={{ fontSize: 14, color: 'var(--ink-2)', marginTop: 16 }}>
              {lang === 'id' ? 'Menghubungkan…' : 'Connecting…'}
            </div>
          </div>
        )}
        {phase === 'done' && (
          <div style={{ padding: '32px 0' }}>
            <div style={{ width: 56, height: 56, borderRadius: 999, background: 'var(--primary)', color: '#fff', display: 'inline-flex', alignItems: 'center', justifyContent: 'center' }}>
              <IconCheck size={28} weight={3}/>
            </div>
            <div style={{ fontSize: 16, fontWeight: 600, color: 'var(--ink)', marginTop: 12 }}>
              {lang === 'id' ? 'Berhasil tersambung' : 'Connected'}
            </div>
            <div style={{ fontSize: 12, color: 'var(--ink-3)', marginTop: 4 }}>andi.pratama@gmail.com</div>
          </div>
        )}
      </div>
    </Sheet>
  );
}

Object.assign(window, {
  Sheet, AddSheet, ScanModal, DescribeModal, FoodDetailModal,
  OnboardingModal, GoogleModal, NumField,
});

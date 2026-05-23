// app.jsx — Root component with state, routing, tab bar, tweaks

const DEFAULT_PROFILE = {
  name: 'Andi',
  weight: 68,
  targetWeight: 64,
  height: 170,
  age: 28,
  sex: 'male',
  goal: 'lose',
  pace: 'normal',
  activity: 'moderate',
  dailyGoal: 1980,
  streak: 7,
  googleConnected: false,
  email: '',
};

// Seed today's log with a realistic breakfast + a couple snacks
function seedLog() {
  const today = new Date().toISOString().slice(0, 10);
  const yesterday = new Date(Date.now() - 864e5).toISOString().slice(0, 10);
  const d2 = new Date(Date.now() - 2 * 864e5).toISOString().slice(0, 10);
  const d3 = new Date(Date.now() - 3 * 864e5).toISOString().slice(0, 10);
  const d4 = new Date(Date.now() - 4 * 864e5).toISOString().slice(0, 10);
  const d5 = new Date(Date.now() - 5 * 864e5).toISOString().slice(0, 10);
  const d6 = new Date(Date.now() - 6 * 864e5).toISOString().slice(0, 10);

  const make = (foodId, meal, qty = 1) => {
    const f = FOOD_BY_ID[foodId];
    return {
      uid: 'i_' + Math.random().toString(36).slice(2, 8),
      name: f.name, emoji: f.emoji, serving: f.serving, qty,
      kcal: f.kcal * qty, p: f.p * qty, c: f.c * qty, f: f.f * qty, meal,
    };
  };

  return {
    [today]: {
      items: [
        make('nasi-putih', 'sarapan'),
        make('telur-dadar', 'sarapan'),
        make('kopi-susu', 'sarapan'),
        make('pisang-goreng', 'cemilan'),
      ],
    },
    [yesterday]: {
      items: [
        make('nasi-putih', 'sarapan'),
        make('tempe-goreng', 'sarapan'),
        make('soto-ayam', 'makan_siang'),
        make('es-teh-manis', 'makan_siang'),
        make('nasi-putih', 'makan_malam'),
        make('ayam-bakar', 'makan_malam'),
        make('tumis-kangkung', 'makan_malam'),
      ],
    },
    [d2]: { items: [make('nasi-goreng', 'makan_siang'), make('ayam-goreng', 'makan_malam'), make('nasi-putih', 'makan_malam')] },
    [d3]: { items: [make('mie-ayam', 'makan_siang'), make('martabak-manis', 'cemilan'), make('nasi-putih', 'makan_malam'), make('ikan-tongkol', 'makan_malam')] },
    [d4]: { items: [make('bakso', 'makan_siang'), make('nasi-putih', 'makan_malam'), make('rendang', 'makan_malam')] },
    [d5]: { items: [make('gado-gado', 'makan_siang'), make('nasi-putih', 'sarapan'), make('telur-dadar', 'sarapan')] },
    [d6]: { items: [make('sate-ayam', 'makan_malam'), make('nasi-putih', 'makan_malam'), make('es-jeruk', 'makan_malam')] },
  };
}

function reducer(state, action) {
  switch (action.type) {
    case 'selectDate':       return { ...state, selectedDate: action.date };
    case 'showAdd':          return { ...state, addSheet: true, lastMeal: action.meal || state.lastMeal };
    case 'closeAdd':         return { ...state, addSheet: false };
    case 'openAddMethod': {
      const s = { ...state, addSheet: false };
      if (action.method === 'scan') s.scanOpen = true;
      if (action.method === 'describe') s.describeOpen = true;
      if (action.method === 'search') s.tab = 'search';
      return s;
    }
    case 'closeScan':        return { ...state, scanOpen: false };
    case 'closeDescribe':    return { ...state, describeOpen: false };
    case 'showFoodDetail':   return { ...state, foodDetailId: action.foodId };
    case 'closeFoodDetail':  return { ...state, foodDetailId: null };
    case 'showOnboarding':   return { ...state, onboardingOpen: true };
    case 'showGoogle':       return { ...state, googleOpen: true };
    case 'closeGoogle':      return { ...state, googleOpen: false };
    case 'completeOnboarding': return {
      ...state,
      profile: { ...state.profile, ...action.profile },
      onboardingOpen: false,
    };
    case 'connectGoogle': return {
      ...state,
      profile: { ...state.profile, googleConnected: true, name: action.name, email: action.email },
    };
    case 'setTab': return { ...state, tab: action.tab };
    case 'addItem': {
      const date = state.selectedDate;
      const day = state.log[date] || { items: [] };
      return {
        ...state,
        log: { ...state.log, [date]: { items: [...day.items, action.item] } },
        lastMeal: action.item.meal,
      };
    }
    case 'removeItem': {
      const date = state.selectedDate;
      const day = state.log[date] || { items: [] };
      return {
        ...state,
        log: { ...state.log, [date]: { items: day.items.filter(it => it.uid !== action.uid) } },
      };
    }
    case 'setLang':          return { ...state, lang: action.lang };
    default: return state;
  }
}

// ─────────────────────────────────────────────────────────────
// Tab bar (bottom)
// ─────────────────────────────────────────────────────────────
function TabBar({ tab, dispatch, lang }) {
  const t = useT(lang);
  const tabs = [
    { id: 'home',     label: t('tab_home'),     icon: IconHome },
    { id: 'search',   label: t('tab_search'),   icon: IconSearch },
    { id: 'add',      label: '',                icon: IconPlus, isFab: true },
    { id: 'progress', label: t('tab_progress'), icon: IconChartArea },
    { id: 'profile',  label: t('tab_profile'),  icon: IconUser },
  ];

  return (
    <div className="tf-tabbar">
      {tabs.map(tb => {
        const Icon = tb.icon;
        if (tb.isFab) {
          return (
            <button key={tb.id} className="tf-fab" onClick={() => dispatch({ type: 'showAdd' })}>
              <Icon size={24} stroke={2.5}/>
            </button>
          );
        }
        const active = tab === tb.id;
        return (
          <button key={tb.id} className={`tf-tab ${active ? 'on' : ''}`}
                  onClick={() => dispatch({ type: 'setTab', tab: tb.id })}>
            <Icon size={22} stroke={active ? 2.2 : 1.8}/>
            <span>{tb.label}</span>
          </button>
        );
      })}
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// Tweaks
// ─────────────────────────────────────────────────────────────
const TWEAK_DEFAULTS = /*EDITMODE-BEGIN*/{
  "palette": ["#0f9d6c","#0a6f4d","#e0f1ea"],
  "dark": false,
  "density": "regular",
  "font": "Plus Jakarta Sans",
  "displayFont": "Instrument Serif",
  "lang": "id"
}/*EDITMODE-END*/;

const PALETTES = [
  ['#0f9d6c', '#0a6f4d', '#e0f1ea'], // fresh green (default)
  ['#16a34a', '#0d6e36', '#dcfce7'], // emerald
  ['#0d9488', '#0a6e63', '#ccfbf1'], // teal
  ['#0ea5e9', '#0369a1', '#e0f2fe'], // sky blue
  ['#f5793b', '#b8501e', '#fdebdf'], // warm orange
  ['#22272d', '#0b1014', '#e8efe9'], // editorial mono
];

const FONT_OPTIONS = ['Plus Jakarta Sans', 'DM Sans', 'Geist', 'Manrope', 'Sora'];
const DISPLAY_FONT_OPTIONS = ['Instrument Serif', 'Fraunces', 'Plus Jakarta Sans'];

function applyTweaks(t) {
  const root = document.documentElement;
  const [primary, primaryDeep, soft] = t.palette;
  root.style.setProperty('--primary', primary);
  root.style.setProperty('--primary-deep', primaryDeep);
  root.style.setProperty('--soft', soft);
  root.style.setProperty('--soft-deep', `color-mix(in oklab, ${primary} 35%, white)`);
  root.style.setProperty('--font-body', `"${t.font}", -apple-system, system-ui, sans-serif`);
  root.style.setProperty('--font-display', `"${t.displayFont}", "${t.font}", serif`);

  // Density spacing
  const dens = { compact: 0.85, regular: 1, comfy: 1.15 }[t.density] || 1;
  root.style.setProperty('--dens', dens);

  // Dark mode
  if (t.dark) {
    root.classList.add('tf-dark');
  } else {
    root.classList.remove('tf-dark');
  }
}

// ─────────────────────────────────────────────────────────────
// App
// ─────────────────────────────────────────────────────────────
function App() {
  const [tweaks, setTweak] = useTweaks(TWEAK_DEFAULTS);

  const [state, dispatch] = React.useReducer(reducer, null, () => ({
    tab: 'home',
    lang: tweaks.lang,
    profile: DEFAULT_PROFILE,
    log: seedLog(),
    selectedDate: new Date().toISOString().slice(0, 10),
    addSheet: false,
    scanOpen: false,
    describeOpen: false,
    foodDetailId: null,
    onboardingOpen: false,
    googleOpen: false,
    lastMeal: 'makan_siang',
  }));

  // Apply tweaks
  React.useEffect(() => { applyTweaks(tweaks); }, [tweaks]);
  // Sync lang tweak → app state
  React.useEffect(() => {
    if (tweaks.lang !== state.lang) {
      dispatch({ type: 'setLang', lang: tweaks.lang });
    }
  }, [tweaks.lang]);

  // Auto-scale iOS frame to viewport
  const [scale, setScale] = React.useState(1);
  React.useEffect(() => {
    const FRAME_W = 393, FRAME_H = 852;
    const update = () => {
      const vw = window.innerWidth, vh = window.innerHeight;
      const s = Math.min(vw / (FRAME_W + 40), vh / (FRAME_H + 40), 1);
      setScale(s);
    };
    update();
    window.addEventListener('resize', update);
    return () => window.removeEventListener('resize', update);
  }, []);

  return (
    <>
      {/* Centered iOS frame */}
      <div style={{
        position: 'fixed', inset: 0,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        background: 'var(--page-bg)',
      }}>
        <div style={{ transform: `scale(${scale})`, transformOrigin: 'center center' }}>
          <IOSDevice width={393} height={852} dark={false}>
            <div className="tf-app" data-density={tweaks.density}>
              {/* Screen content */}
              <div className="tf-content">
                {state.tab === 'home'     && <HomeScreen     state={state} dispatch={dispatch}/>}
                {state.tab === 'search'   && <SearchScreen   state={state} dispatch={dispatch}/>}
                {state.tab === 'progress' && <ProgressScreen state={state} dispatch={dispatch}/>}
                {state.tab === 'profile'  && <ProfileScreen  state={state} dispatch={dispatch}/>}
              </div>

              {/* Tab bar */}
              <TabBar tab={state.tab} dispatch={dispatch} lang={state.lang}/>

              {/* Modals — overlays */}
              <AddSheet         state={state} dispatch={dispatch}/>
              <ScanModal        state={state} dispatch={dispatch}/>
              <DescribeModal    state={state} dispatch={dispatch}/>
              <FoodDetailModal  state={state} dispatch={dispatch}/>
              <OnboardingModal  state={state} dispatch={dispatch}/>
              <GoogleModal      state={state} dispatch={dispatch}/>
            </div>
          </IOSDevice>
        </div>
      </div>

      {/* Tweaks panel (outside iOS frame, fixed) */}
      <TweaksPanel>
        <TweakSection label="Brand"/>
        <TweakColor label="Palette" value={tweaks.palette}
                    options={PALETTES}
                    onChange={v => setTweak('palette', v)}/>
        <TweakToggle label="Dark mode" value={tweaks.dark}
                     onChange={v => setTweak('dark', v)}/>

        <TweakSection label="Typography"/>
        <TweakSelect label="Body font" value={tweaks.font}
                     options={FONT_OPTIONS}
                     onChange={v => setTweak('font', v)}/>
        <TweakSelect label="Display font" value={tweaks.displayFont}
                     options={DISPLAY_FONT_OPTIONS}
                     onChange={v => setTweak('displayFont', v)}/>

        <TweakSection label="Layout"/>
        <TweakRadio label="Density" value={tweaks.density}
                    options={['compact', 'regular', 'comfy']}
                    onChange={v => setTweak('density', v)}/>

        <TweakSection label="Language"/>
        <TweakRadio label="Locale" value={tweaks.lang}
                    options={['id', 'en']}
                    onChange={v => setTweak('lang', v)}/>

        <TweakSection label="Flows"/>
        <TweakButton label="Show onboarding"
                     onClick={() => dispatch({ type: 'showOnboarding' })}/>
        <TweakButton label="Show Google connect"
                     onClick={() => dispatch({ type: 'showGoogle' })}/>
      </TweaksPanel>
    </>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App/>);

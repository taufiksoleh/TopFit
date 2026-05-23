// Simple stroke icons — Lucide-style, all use currentColor
// All icons accept { size = 24, stroke = 2, ...props }

const I = (paths, vb = 24) => function Icon({ size = 24, weight = 2, ...rest }) {
  return (
    <svg width={size} height={size} viewBox={`0 0 ${vb} ${vb}`} fill="none"
         strokeWidth={weight} strokeLinecap="round" strokeLinejoin="round"
         {...rest} stroke="currentColor">
      {paths}
    </svg>
  );
};

const IconHome = I(<>
  <path d="M3 11l9-8 9 8v9a2 2 0 01-2 2h-4v-7H9v7H5a2 2 0 01-2-2v-9z"/>
</>);

const IconChartArea = I(<>
  <path d="M3 3v18h18"/>
  <path d="M7 14l4-4 4 3 5-7"/>
</>);

const IconUser = I(<>
  <circle cx="12" cy="8" r="4"/>
  <path d="M4 21c0-4 4-7 8-7s8 3 8 7"/>
</>);

const IconSearch = I(<>
  <circle cx="11" cy="11" r="7"/>
  <path d="M20 20l-3.5-3.5"/>
</>);

const IconPlus = I(<>
  <path d="M12 5v14M5 12h14"/>
</>);

const IconCamera = I(<>
  <path d="M3 7h3l2-3h8l2 3h3a1 1 0 011 1v11a1 1 0 01-1 1H3a1 1 0 01-1-1V8a1 1 0 011-1z"/>
  <circle cx="12" cy="13" r="4"/>
</>);

const IconSparkles = I(<>
  <path d="M12 3l1.8 4.8L18.6 9.6 13.8 11.4 12 16.2 10.2 11.4 5.4 9.6l4.8-1.8L12 3z"/>
  <path d="M19 16l.9 2.4 2.4.9-2.4.9L19 22.6l-.9-2.4-2.4-.9 2.4-.9L19 16z"/>
</>);

const IconType = I(<>
  <path d="M4 7V5h16v2"/>
  <path d="M12 5v14"/>
  <path d="M9 19h6"/>
</>);

const IconChevronRight = I(<>
  <path d="M9 6l6 6-6 6"/>
</>);

const IconChevronLeft = I(<>
  <path d="M15 6l-6 6 6 6"/>
</>);

const IconChevronDown = I(<>
  <path d="M6 9l6 6 6-6"/>
</>);

const IconCheck = I(<>
  <path d="M4 12l5 5L20 6"/>
</>);

const IconX = I(<>
  <path d="M6 6l12 12M18 6L6 18"/>
</>);

const IconTrash = I(<>
  <path d="M4 7h16"/>
  <path d="M10 11v6M14 11v6"/>
  <path d="M5 7l1 13a2 2 0 002 2h8a2 2 0 002-2l1-13"/>
  <path d="M9 7V4a1 1 0 011-1h4a1 1 0 011 1v3"/>
</>);

const IconFlame = I(<>
  <path d="M12 22c4 0 7-3 7-7 0-3-2-5-3-7-1 2-3 2-4 0-1 2-3 2-4 0-1 2-3 4-3 7 0 4 3 7 7 7z"/>
</>);

const IconScale = I(<>
  <path d="M4 4h16v16H4z"/>
  <path d="M8 9c1.5-1 2.5-1 4-1s2.5 0 4 1"/>
  <path d="M12 12v4"/>
</>);

const IconTarget = I(<>
  <circle cx="12" cy="12" r="9"/>
  <circle cx="12" cy="12" r="5"/>
  <circle cx="12" cy="12" r="1.5" fill="currentColor"/>
</>);

const IconBolt = I(<>
  <path d="M13 2L4 14h7l-1 8 9-12h-7l1-8z"/>
</>);

const IconCalendar = I(<>
  <rect x="3" y="5" width="18" height="16" rx="2"/>
  <path d="M3 10h18M8 3v4M16 3v4"/>
</>);

const IconBell = I(<>
  <path d="M6 8a6 6 0 1112 0c0 7 3 7 3 9H3c0-2 3-2 3-9z"/>
  <path d="M10 21a2 2 0 004 0"/>
</>);

const IconGlobe = I(<>
  <circle cx="12" cy="12" r="9"/>
  <path d="M3 12h18M12 3a14 14 0 010 18M12 3a14 14 0 000 18"/>
</>);

const IconLogo = ({ size = 32, color = 'currentColor' }) => (
  <svg width={size} height={size} viewBox="0 0 32 32" fill="none">
    <rect width="32" height="32" rx="9" fill={color}/>
    <path d="M9 17 l4 4 l10 -10" stroke="white" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" fill="none"/>
    <circle cx="9" cy="9" r="2" fill="white" opacity="0.85"/>
  </svg>
);

const IconGoogle = ({ size = 20 }) => (
  <svg width={size} height={size} viewBox="0 0 48 48">
    <path fill="#FFC107" d="M43.6 20.5H42V20H24v8h11.3c-1.6 4.7-6.1 8-11.3 8a12 12 0 110-24c3 0 5.8 1.1 7.9 3l5.7-5.7A20 20 0 1024 44c11 0 20-9 20-20 0-1.2-.1-2.3-.4-3.5z"/>
    <path fill="#FF3D00" d="M6.3 14.7l6.6 4.8C14.6 15.5 19 12 24 12c3 0 5.8 1.1 7.9 3l5.7-5.7A20 20 0 006.3 14.7z"/>
    <path fill="#4CAF50" d="M24 44c5 0 9.6-1.9 13.1-5l-6-5.1c-2 1.4-4.4 2.2-7 2.2-5.2 0-9.6-3.3-11.3-7.9l-6.5 5C9.6 39.5 16.3 44 24 44z"/>
    <path fill="#1976D2" d="M43.6 20.5H42V20H24v8h11.3c-.8 2.3-2.3 4.3-4.2 5.8l6 5.1c3.7-3.4 6.2-8.4 6.2-14.4 0-1.2-.1-2.3-.3-3.5z"/>
  </svg>
);

Object.assign(window, {
  IconHome, IconChartArea, IconUser, IconSearch, IconPlus,
  IconCamera, IconSparkles, IconType, IconChevronRight, IconChevronLeft,
  IconChevronDown, IconCheck, IconX, IconTrash, IconFlame, IconScale,
  IconTarget, IconBolt, IconCalendar, IconBell, IconGlobe, IconLogo, IconGoogle,
});

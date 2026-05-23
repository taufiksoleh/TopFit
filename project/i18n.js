// i18n strings (id / en) and small helpers

const STRINGS = {
  id: {
    // App
    appName: 'TopFit',
    appTagline: 'Lacak kalori, capai target.',

    // Tabs
    tab_home: 'Beranda',
    tab_add: 'Tambah',
    tab_search: 'Cari',
    tab_progress: 'Progres',
    tab_profile: 'Profil',

    // Greetings
    morning: 'Selamat pagi',
    afternoon: 'Selamat siang',
    evening: 'Selamat malam',

    // Home
    today: 'Hari ini',
    yesterday: 'Kemarin',
    cal_remaining: 'kalori tersisa',
    cal_consumed: 'dimakan',
    cal_goal: 'Target',
    protein: 'Protein',
    carbs: 'Karbohidrat',
    fat: 'Lemak',
    streak: 'hari beruntun',
    meal_sarapan: 'Sarapan',
    meal_makan_siang: 'Makan Siang',
    meal_makan_malam: 'Makan Malam',
    meal_cemilan: 'Cemilan',
    add_food: 'Tambah makanan',
    no_meals: 'Belum ada makanan',
    empty_meal_prompt: 'Tambah sesuatu yang kamu makan',

    // Add sheet
    add_title: 'Tambah Makanan',
    add_subtitle: 'Pilih cara mencatat',
    add_scan: 'Pindai dengan AI',
    add_scan_desc: 'Foto makanan, AI hitung kalorinya',
    add_describe: 'Ketik dengan AI',
    add_describe_desc: 'Tulis apa yang kamu makan',
    add_search: 'Cari di Database',
    add_search_desc: 'Telusuri menu Indonesia',

    // Scan
    scan_title: 'Pindai dengan AI',
    scan_hint: 'Arahkan kamera ke makanan',
    scan_capture: 'Ambil Foto',
    scan_sample: 'Coba dengan contoh',
    scan_analyzing: 'Menganalisis dengan AI…',
    scan_detected: 'Terdeteksi',
    scan_review: 'Periksa & sesuaikan',
    scan_add_to: 'Tambah ke',
    confidence: 'keyakinan',

    // Describe
    describe_title: 'Ketik dengan AI',
    describe_hint: 'Contoh: nasi goreng setengah porsi, ayam goreng 1 potong, es teh manis',
    describe_placeholder: 'Tulis apa yang kamu makan…',
    describe_analyze: 'Hitung Kalori',
    describe_powered: 'Didukung AI',
    try_example: 'Contoh',

    // Search
    search_title: 'Cari Makanan',
    search_placeholder: 'Cari menu, mis. ayam goreng…',
    search_categories: 'Kategori',
    search_empty: 'Tidak ditemukan',
    serving: 'Porsi',
    portion: 'porsi',

    // Progress
    progress_title: 'Progres',
    this_week: 'Minggu ini',
    avg_daily: 'Rata-rata harian',
    weight_trend: 'Berat badan',
    days: 'hari',
    kg_lost: 'berkurang',
    on_track: 'Sesuai target',
    over_target: 'Di atas target',
    under_target: 'Di bawah target',

    // Profile
    profile_title: 'Profil',
    goals: 'Target',
    weight: 'Berat',
    target_weight: 'Berat Target',
    height: 'Tinggi',
    age: 'Usia',
    activity: 'Aktivitas',
    daily_calorie_goal: 'Target kalori harian',
    connect_google: 'Sambungkan Google',
    connect_google_desc: 'Simpan data agar tidak hilang',
    connected_as: 'Tersambung sebagai',
    sign_out: 'Keluar',
    settings: 'Pengaturan',
    notifications: 'Notifikasi',
    units: 'Satuan',
    privacy: 'Privasi',
    about: 'Tentang',
    edit_goals: 'Ubah Target',

    // Onboarding
    onb_welcome_title: 'Selamat datang di TopFit',
    onb_welcome_sub: 'Lacak kalori dengan AI. Khusus menu Indonesia.',
    onb_start: 'Mulai',
    onb_step_about: 'Tentang kamu',
    onb_step_goal: 'Target kamu',
    onb_step_activity: 'Tingkat aktivitas',
    onb_step_done: 'Siap!',
    sex: 'Jenis kelamin',
    male: 'Pria',
    female: 'Wanita',
    goal_lose: 'Turunkan berat',
    goal_maintain: 'Pertahankan',
    goal_gain: 'Naikkan berat',
    pace_slow: '0,25 kg / minggu',
    pace_normal: '0,5 kg / minggu',
    pace_fast: '0,75 kg / minggu',
    activity_sedentary: 'Tidak aktif',
    activity_light: 'Ringan',
    activity_moderate: 'Sedang',
    activity_active: 'Aktif',
    activity_very_active: 'Sangat aktif',
    activity_sedentary_desc: 'Duduk hampir sepanjang hari',
    activity_light_desc: 'Olahraga ringan 1–3 hari/minggu',
    activity_moderate_desc: 'Olahraga 3–5 hari/minggu',
    activity_active_desc: 'Olahraga 6–7 hari/minggu',
    activity_very_active_desc: 'Olahraga intensif setiap hari',
    onb_calc_title: 'Target kalori harianmu',
    onb_calc_sub: 'Dihitung berdasarkan profilmu',
    onb_done_cta: 'Mulai Lacak',

    // Google modal
    gconn_title: 'Simpan kemajuanmu',
    gconn_sub: 'Sambungkan Google untuk menyimpan data dan akses dari perangkat lain. Gratis dan opsional.',
    gconn_b1: 'Data tersinkron antar perangkat',
    gconn_b2: 'Tidak akan hilang saat ganti HP',
    gconn_b3: 'Tetap bisa dipakai tanpa login',
    gconn_cta: 'Lanjutkan dengan Google',
    gconn_skip: 'Nanti saja',

    // Generic
    save: 'Simpan',
    cancel: 'Batal',
    done: 'Selesai',
    next: 'Lanjut',
    back: 'Kembali',
    delete: 'Hapus',
    add: 'Tambah',
    edit: 'Ubah',
    close: 'Tutup',
    kcal: 'kkal',
    of: 'dari',
  },
  en: {
    appName: 'TopFit',
    appTagline: 'Track calories, hit your goals.',

    tab_home: 'Home',
    tab_add: 'Add',
    tab_search: 'Search',
    tab_progress: 'Progress',
    tab_profile: 'Profile',

    morning: 'Good morning',
    afternoon: 'Good afternoon',
    evening: 'Good evening',

    today: 'Today',
    yesterday: 'Yesterday',
    cal_remaining: 'calories left',
    cal_consumed: 'eaten',
    cal_goal: 'Goal',
    protein: 'Protein',
    carbs: 'Carbs',
    fat: 'Fat',
    streak: 'day streak',
    meal_sarapan: 'Breakfast',
    meal_makan_siang: 'Lunch',
    meal_makan_malam: 'Dinner',
    meal_cemilan: 'Snacks',
    add_food: 'Add food',
    no_meals: 'No meals yet',
    empty_meal_prompt: 'Add something you ate',

    add_title: 'Add Food',
    add_subtitle: 'Choose how to log it',
    add_scan: 'Scan with AI',
    add_scan_desc: 'Snap a photo, AI counts calories',
    add_describe: 'Describe with AI',
    add_describe_desc: 'Type what you ate',
    add_search: 'Search Database',
    add_search_desc: 'Browse Indonesian menu',

    scan_title: 'Scan with AI',
    scan_hint: 'Point camera at your food',
    scan_capture: 'Capture',
    scan_sample: 'Try with sample',
    scan_analyzing: 'Analyzing with AI…',
    scan_detected: 'Detected',
    scan_review: 'Review & adjust',
    scan_add_to: 'Add to',
    confidence: 'confidence',

    describe_title: 'Describe with AI',
    describe_hint: 'e.g. half portion nasi goreng, 1 piece fried chicken, sweet iced tea',
    describe_placeholder: 'Type what you ate…',
    describe_analyze: 'Calculate Calories',
    describe_powered: 'Powered by AI',
    try_example: 'Example',

    search_title: 'Find Food',
    search_placeholder: 'Search menu, e.g. fried chicken…',
    search_categories: 'Categories',
    search_empty: 'No results',
    serving: 'Serving',
    portion: 'portion',

    progress_title: 'Progress',
    this_week: 'This week',
    avg_daily: 'Daily average',
    weight_trend: 'Weight',
    days: 'days',
    kg_lost: 'lost',
    on_track: 'On track',
    over_target: 'Over target',
    under_target: 'Under target',

    profile_title: 'Profile',
    goals: 'Goals',
    weight: 'Weight',
    target_weight: 'Target Weight',
    height: 'Height',
    age: 'Age',
    activity: 'Activity',
    daily_calorie_goal: 'Daily calorie goal',
    connect_google: 'Connect Google',
    connect_google_desc: 'Save data so you don\'t lose it',
    connected_as: 'Connected as',
    sign_out: 'Sign out',
    settings: 'Settings',
    notifications: 'Notifications',
    units: 'Units',
    privacy: 'Privacy',
    about: 'About',
    edit_goals: 'Edit Goals',

    onb_welcome_title: 'Welcome to TopFit',
    onb_welcome_sub: 'Track calories with AI. Built for Indonesian food.',
    onb_start: 'Get Started',
    onb_step_about: 'About you',
    onb_step_goal: 'Your goal',
    onb_step_activity: 'Activity level',
    onb_step_done: 'You\'re set!',
    sex: 'Sex',
    male: 'Male',
    female: 'Female',
    goal_lose: 'Lose weight',
    goal_maintain: 'Maintain',
    goal_gain: 'Gain weight',
    pace_slow: '0.25 kg / week',
    pace_normal: '0.5 kg / week',
    pace_fast: '0.75 kg / week',
    activity_sedentary: 'Sedentary',
    activity_light: 'Light',
    activity_moderate: 'Moderate',
    activity_active: 'Active',
    activity_very_active: 'Very active',
    activity_sedentary_desc: 'Mostly sitting all day',
    activity_light_desc: 'Light exercise 1–3 days/week',
    activity_moderate_desc: 'Exercise 3–5 days/week',
    activity_active_desc: 'Exercise 6–7 days/week',
    activity_very_active_desc: 'Intense daily exercise',
    onb_calc_title: 'Your daily calorie goal',
    onb_calc_sub: 'Calculated from your profile',
    onb_done_cta: 'Start Tracking',

    gconn_title: 'Save your progress',
    gconn_sub: 'Connect Google to back up your data and sync across devices. Free and optional.',
    gconn_b1: 'Synced across devices',
    gconn_b2: 'Won\'t lose data when switching phones',
    gconn_b3: 'Still works without an account',
    gconn_cta: 'Continue with Google',
    gconn_skip: 'Maybe later',

    save: 'Save',
    cancel: 'Cancel',
    done: 'Done',
    next: 'Next',
    back: 'Back',
    delete: 'Delete',
    add: 'Add',
    edit: 'Edit',
    close: 'Close',
    kcal: 'kcal',
    of: 'of',
  },
};

// useT hook — returns a translator function
function useT(lang) {
  return React.useCallback((key) => {
    return STRINGS[lang]?.[key] ?? STRINGS.en[key] ?? key;
  }, [lang]);
}

// Format numbers — locale-ish
function fmtNum(n, lang = 'id') {
  if (Math.abs(n) >= 1000) {
    return n.toLocaleString(lang === 'id' ? 'id-ID' : 'en-US');
  }
  return Math.round(n).toString();
}

// Greeting based on hour
function getGreeting(lang, t) {
  const h = new Date().getHours();
  if (h < 11) return t('morning');
  if (h < 16) return t('afternoon');
  return t('evening');
}

// Compute BMR + TDEE + target
function calcDailyCalories({ weight = 70, height = 170, age = 28, sex = 'male', activity = 'moderate', goal = 'maintain', pace = 'normal' }) {
  // Mifflin-St Jeor
  const bmr = sex === 'male'
    ? 10 * weight + 6.25 * height - 5 * age + 5
    : 10 * weight + 6.25 * height - 5 * age - 161;
  const activityMult = {
    sedentary: 1.2, light: 1.375, moderate: 1.55, active: 1.725, very_active: 1.9,
  }[activity] || 1.55;
  const tdee = bmr * activityMult;
  const deficit = { slow: 275, normal: 550, fast: 825 }[pace] || 550;
  let target = tdee;
  if (goal === 'lose') target = tdee - deficit;
  if (goal === 'gain') target = tdee + deficit;
  return Math.round(target / 10) * 10;
}

window.STRINGS = STRINGS;
window.useT = useT;
window.fmtNum = fmtNum;
window.getGreeting = getGreeting;
window.calcDailyCalories = calcDailyCalories;

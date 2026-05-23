// Indonesian food database — kcal & macros per default serving
// Each item: { id, name, name_en, category, serving, serving_en, kcal, p, c, f, emoji }

const FOODS = [
  // ───── Lauk (proteins) ─────
  { id: 'ayam-goreng',    name: 'Ayam Goreng',     name_en: 'Fried Chicken',         category: 'lauk',     serving: '1 potong (100g)',    serving_en: '1 piece (100g)',   kcal: 250, p: 24, c: 4,  f: 15, emoji: '🍗' },
  { id: 'ayam-bakar',     name: 'Ayam Bakar',      name_en: 'Grilled Chicken',       category: 'lauk',     serving: '1 potong (100g)',    serving_en: '1 piece (100g)',   kcal: 220, p: 27, c: 3,  f: 11, emoji: '🔥' },
  { id: 'rendang',        name: 'Rendang Sapi',    name_en: 'Beef Rendang',          category: 'lauk',     serving: '1 porsi (100g)',     serving_en: '1 portion (100g)', kcal: 290, p: 22, c: 7,  f: 20, emoji: '🥩' },
  { id: 'ikan-tongkol',   name: 'Ikan Tongkol',    name_en: 'Tuna (Tongkol)',        category: 'lauk',     serving: '1 potong (100g)',    serving_en: '1 piece (100g)',   kcal: 180, p: 28, c: 0,  f: 7,  emoji: '🐟' },
  { id: 'cumi-cumi',      name: 'Cumi-cumi',       name_en: 'Squid',                 category: 'lauk',     serving: '1 porsi (100g)',     serving_en: '1 portion (100g)', kcal: 150, p: 20, c: 5,  f: 5,  emoji: '🦑' },
  { id: 'udang-balado',   name: 'Udang Balado',    name_en: 'Spicy Shrimp',          category: 'lauk',     serving: '1 porsi (100g)',     serving_en: '1 portion (100g)', kcal: 200, p: 22, c: 8,  f: 9,  emoji: '🍤' },
  { id: 'tempe-goreng',   name: 'Tempe Goreng',    name_en: 'Fried Tempeh',          category: 'lauk',     serving: '2 potong',           serving_en: '2 pieces',         kcal: 140, p: 10, c: 8,  f: 8,  emoji: '🟫' },
  { id: 'tahu-goreng',    name: 'Tahu Goreng',     name_en: 'Fried Tofu',            category: 'lauk',     serving: '2 potong',           serving_en: '2 pieces',         kcal: 100, p: 8,  c: 4,  f: 6,  emoji: '⬜' },
  { id: 'tempe-orek',     name: 'Tempe Orek',      name_en: 'Sweet Stir-fried Tempeh', category: 'lauk',   serving: '1 porsi',            serving_en: '1 portion',        kcal: 140, p: 8,  c: 12, f: 7,  emoji: '🍱' },
  { id: 'telur-dadar',    name: 'Telur Dadar',     name_en: 'Omelette',              category: 'lauk',     serving: '1 butir',            serving_en: '1 egg',            kcal: 110, p: 7,  c: 1,  f: 9,  emoji: '🍳' },

  // ───── Nasi & Karbo ─────
  { id: 'nasi-putih',     name: 'Nasi Putih',      name_en: 'White Rice',            category: 'karbo',    serving: '1 porsi (100g)',     serving_en: '1 portion (100g)', kcal: 175, p: 4,  c: 39, f: 0,  emoji: '🍚' },
  { id: 'nasi-merah',     name: 'Nasi Merah',      name_en: 'Brown Rice',            category: 'karbo',    serving: '1 porsi (100g)',     serving_en: '1 portion (100g)', kcal: 165, p: 4,  c: 35, f: 1,  emoji: '🟫' },
  { id: 'nasi-goreng',    name: 'Nasi Goreng',     name_en: 'Fried Rice',            category: 'karbo',    serving: '1 porsi',            serving_en: '1 portion',        kcal: 330, p: 10, c: 50, f: 12, emoji: '🍛' },
  { id: 'mie-ayam',       name: 'Mie Ayam',        name_en: 'Chicken Noodles',       category: 'karbo',    serving: '1 mangkok',          serving_en: '1 bowl',           kcal: 350, p: 14, c: 50, f: 11, emoji: '🍜' },
  { id: 'bakso',          name: 'Bakso',           name_en: 'Meatball Soup',         category: 'karbo',    serving: '1 mangkok',          serving_en: '1 bowl',           kcal: 220, p: 15, c: 22, f: 8,  emoji: '🍲' },
  { id: 'soto-ayam',      name: 'Soto Ayam',       name_en: 'Chicken Soup',          category: 'karbo',    serving: '1 mangkok',          serving_en: '1 bowl',           kcal: 200, p: 18, c: 15, f: 8,  emoji: '🥣' },
  { id: 'baso-cuangki',   name: 'Baso Cuangki',    name_en: 'Cuangki Dumpling Soup', category: 'karbo',    serving: '1 mangkok',          serving_en: '1 bowl',           kcal: 180, p: 11, c: 18, f: 7,  emoji: '🥟' },
  { id: 'sate-ayam',      name: 'Sate Ayam',       name_en: 'Chicken Satay',         category: 'karbo',    serving: '10 tusuk',           serving_en: '10 skewers',       kcal: 250, p: 28, c: 6,  f: 12, emoji: '🍢' },

  // ───── Sayur ─────
  { id: 'toge',           name: 'Toge / Tauge',    name_en: 'Bean Sprouts',          category: 'sayur',    serving: '1 porsi (100g)',     serving_en: '1 portion (100g)', kcal: 30,  p: 3,  c: 4,  f: 0,  emoji: '🌱' },
  { id: 'gado-gado',      name: 'Gado-gado',       name_en: 'Veggie Peanut Salad',   category: 'sayur',    serving: '1 porsi',            serving_en: '1 portion',        kcal: 290, p: 12, c: 25, f: 16, emoji: '🥗' },
  { id: 'sayur-asem',     name: 'Sayur Asem',      name_en: 'Tamarind Vegetable Soup', category: 'sayur',  serving: '1 mangkok',          serving_en: '1 bowl',           kcal: 90,  p: 4,  c: 16, f: 1,  emoji: '🥬' },
  { id: 'tumis-kangkung', name: 'Tumis Kangkung',  name_en: 'Stir-fried Water Spinach', category: 'sayur', serving: '1 porsi',            serving_en: '1 portion',        kcal: 80,  p: 3,  c: 10, f: 4,  emoji: '🥒' },

  // ───── Gorengan / Cemilan ─────
  { id: 'pisang-goreng',  name: 'Pisang Goreng',   name_en: 'Fried Banana',          category: 'cemilan',  serving: '1 buah',             serving_en: '1 piece',          kcal: 130, p: 1,  c: 22, f: 5,  emoji: '🍌' },
  { id: 'gorengan-tempe', name: 'Gorengan Tempe',  name_en: 'Tempeh Fritter',        category: 'cemilan',  serving: '1 buah',             serving_en: '1 piece',          kcal: 75,  p: 5,  c: 5,  f: 4,  emoji: '🟤' },
  { id: 'gorengan-tahu',  name: 'Gorengan Tahu',   name_en: 'Tofu Fritter',          category: 'cemilan',  serving: '1 buah',             serving_en: '1 piece',          kcal: 55,  p: 4,  c: 2,  f: 4,  emoji: '◾' },
  { id: 'kerupuk',        name: 'Kerupuk',         name_en: 'Crackers',              category: 'cemilan',  serving: '5 buah',             serving_en: '5 pieces',         kcal: 70,  p: 1,  c: 9,  f: 3,  emoji: '🍘' },
  { id: 'martabak-manis', name: 'Martabak Manis',  name_en: 'Sweet Pancake',         category: 'cemilan',  serving: '1 potong',           serving_en: '1 slice',          kcal: 380, p: 6,  c: 50, f: 17, emoji: '🥞' },

  // ───── Minuman ─────
  { id: 'es-teh-manis',   name: 'Es Teh Manis',    name_en: 'Sweet Iced Tea',        category: 'minuman',  serving: '1 gelas (250ml)',    serving_en: '1 glass (250ml)',  kcal: 90,  p: 0,  c: 22, f: 0,  emoji: '🧋' },
  { id: 'es-jeruk',       name: 'Es Jeruk',        name_en: 'Iced Orange',           category: 'minuman',  serving: '1 gelas (250ml)',    serving_en: '1 glass (250ml)',  kcal: 110, p: 1,  c: 27, f: 0,  emoji: '🍊' },
  { id: 'kopi-susu',      name: 'Kopi Susu',       name_en: 'Milk Coffee',           category: 'minuman',  serving: '1 gelas (250ml)',    serving_en: '1 glass (250ml)',  kcal: 180, p: 4,  c: 28, f: 6,  emoji: '☕' },
  { id: 'air-putih',      name: 'Air Putih',       name_en: 'Plain Water',           category: 'minuman',  serving: '1 gelas (250ml)',    serving_en: '1 glass (250ml)',  kcal: 0,   p: 0,  c: 0,  f: 0,  emoji: '💧' },
];

const FOOD_BY_ID = Object.fromEntries(FOODS.map(f => [f.id, f]));

const CATEGORIES = [
  { id: 'all',     id_label: 'Semua',    en_label: 'All' },
  { id: 'karbo',   id_label: 'Nasi',     en_label: 'Rice & Grains' },
  { id: 'lauk',    id_label: 'Lauk',     en_label: 'Protein' },
  { id: 'sayur',   id_label: 'Sayur',    en_label: 'Vegetables' },
  { id: 'cemilan', id_label: 'Cemilan',  en_label: 'Snacks' },
  { id: 'minuman', id_label: 'Minuman',  en_label: 'Drinks' },
];

window.FOODS = FOODS;
window.FOOD_BY_ID = FOOD_BY_ID;
window.CATEGORIES = CATEGORIES;

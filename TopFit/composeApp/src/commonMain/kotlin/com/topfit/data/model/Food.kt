package com.topfit.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Food(
    val id: String,
    val name: String,
    val nameEn: String,
    val category: String,
    val serving: String,
    val servingEn: String,
    val kcal: Int,
    val p: Int,
    val c: Int,
    val f: Int,
    val emoji: String,
)

data class FoodCategory(
    val id: String,
    val labelId: String,
    val labelEn: String,
)

val FOODS: List<Food> = listOf(
    // ── Lauk (Protein) ──
    Food("ayam-goreng",    "Ayam Goreng",    "Fried Chicken",            "lauk",    "1 potong (100g)", "1 piece (100g)",    250, 24,  4, 15, "🍗"),
    Food("ayam-bakar",     "Ayam Bakar",     "Grilled Chicken",          "lauk",    "1 potong (100g)", "1 piece (100g)",    220, 27,  3, 11, "🔥"),
    Food("rendang",        "Rendang Sapi",   "Beef Rendang",             "lauk",    "1 porsi (100g)",  "1 portion (100g)",  290, 22,  7, 20, "🥩"),
    Food("ikan-tongkol",   "Ikan Tongkol",   "Tuna (Tongkol)",           "lauk",    "1 potong (100g)", "1 piece (100g)",    180, 28,  0,  7, "🐟"),
    Food("cumi-cumi",      "Cumi-cumi",      "Squid",                    "lauk",    "1 porsi (100g)",  "1 portion (100g)",  150, 20,  5,  5, "🦑"),
    Food("udang-balado",   "Udang Balado",   "Spicy Shrimp",             "lauk",    "1 porsi (100g)",  "1 portion (100g)",  200, 22,  8,  9, "🍤"),
    Food("tempe-goreng",   "Tempe Goreng",   "Fried Tempeh",             "lauk",    "2 potong",        "2 pieces",          140, 10,  8,  8, "🟫"),
    Food("tahu-goreng",    "Tahu Goreng",    "Fried Tofu",               "lauk",    "2 potong",        "2 pieces",          100,  8,  4,  6, "⬜"),
    Food("tempe-orek",     "Tempe Orek",     "Sweet Stir-fried Tempeh",  "lauk",    "1 porsi",         "1 portion",         140,  8, 12,  7, "🍱"),
    Food("telur-dadar",    "Telur Dadar",    "Omelette",                 "lauk",    "1 butir",         "1 egg",             110,  7,  1,  9, "🍳"),
    // ── Nasi & Karbo ──
    Food("nasi-putih",     "Nasi Putih",     "White Rice",               "karbo",   "1 porsi (100g)",  "1 portion (100g)",  175,  4, 39,  0, "🍚"),
    Food("nasi-merah",     "Nasi Merah",     "Brown Rice",               "karbo",   "1 porsi (100g)",  "1 portion (100g)",  165,  4, 35,  1, "🟫"),
    Food("nasi-goreng",    "Nasi Goreng",    "Fried Rice",               "karbo",   "1 porsi",         "1 portion",         330, 10, 50, 12, "🍛"),
    Food("mie-ayam",       "Mie Ayam",       "Chicken Noodles",          "karbo",   "1 mangkok",       "1 bowl",            350, 14, 50, 11, "🍜"),
    Food("bakso",          "Bakso",          "Meatball Soup",            "karbo",   "1 mangkok",       "1 bowl",            220, 15, 22,  8, "🍲"),
    Food("soto-ayam",      "Soto Ayam",      "Chicken Soup",             "karbo",   "1 mangkok",       "1 bowl",            200, 18, 15,  8, "🥣"),
    Food("baso-cuangki",   "Baso Cuangki",   "Cuangki Dumpling Soup",    "karbo",   "1 mangkok",       "1 bowl",            180, 11, 18,  7, "🥟"),
    Food("sate-ayam",      "Sate Ayam",      "Chicken Satay",            "karbo",   "10 tusuk",        "10 skewers",        250, 28,  6, 12, "🍢"),
    // ── Sayur ──
    Food("toge",           "Toge / Tauge",   "Bean Sprouts",             "sayur",   "1 porsi (100g)",  "1 portion (100g)",   30,  3,  4,  0, "🌱"),
    Food("gado-gado",      "Gado-gado",      "Veggie Peanut Salad",      "sayur",   "1 porsi",         "1 portion",         290, 12, 25, 16, "🥗"),
    Food("sayur-asem",     "Sayur Asem",     "Tamarind Vegetable Soup",  "sayur",   "1 mangkok",       "1 bowl",             90,  4, 16,  1, "🥬"),
    Food("tumis-kangkung", "Tumis Kangkung", "Stir-fried Water Spinach", "sayur",   "1 porsi",         "1 portion",          80,  3, 10,  4, "🥒"),
    // ── Gorengan / Cemilan ──
    Food("pisang-goreng",  "Pisang Goreng",  "Fried Banana",             "cemilan", "1 buah",          "1 piece",           130,  1, 22,  5, "🍌"),
    Food("gorengan-tempe", "Gorengan Tempe", "Tempeh Fritter",           "cemilan", "1 buah",          "1 piece",            75,  5,  5,  4, "🟤"),
    Food("gorengan-tahu",  "Gorengan Tahu",  "Tofu Fritter",             "cemilan", "1 buah",          "1 piece",            55,  4,  2,  4, "◾"),
    Food("kerupuk",        "Kerupuk",        "Crackers",                 "cemilan", "5 buah",          "5 pieces",           70,  1,  9,  3, "🍘"),
    Food("martabak-manis", "Martabak Manis", "Sweet Pancake",            "cemilan", "1 potong",        "1 slice",           380,  6, 50, 17, "🥞"),
    // ── Minuman ──
    Food("es-teh-manis",   "Es Teh Manis",   "Sweet Iced Tea",           "minuman", "1 gelas (250ml)", "1 glass (250ml)",    90,  0, 22,  0, "🧋"),
    Food("es-jeruk",       "Es Jeruk",       "Iced Orange",              "minuman", "1 gelas (250ml)", "1 glass (250ml)",   110,  1, 27,  0, "🍊"),
    Food("kopi-susu",      "Kopi Susu",      "Milk Coffee",              "minuman", "1 gelas (250ml)", "1 glass (250ml)",   180,  4, 28,  6, "☕"),
    Food("air-putih",      "Air Putih",      "Plain Water",              "minuman", "1 gelas (250ml)", "1 glass (250ml)",     0,  0,  0,  0, "💧"),
)

val FOOD_BY_ID: Map<String, Food> = FOODS.associateBy { it.id }

val CATEGORIES: List<FoodCategory> = listOf(
    FoodCategory("all",     "Semua",   "All"),
    FoodCategory("karbo",   "Nasi",    "Rice & Grains"),
    FoodCategory("lauk",    "Lauk",    "Protein"),
    FoodCategory("sayur",   "Sayur",   "Vegetables"),
    FoodCategory("cemilan", "Cemilan", "Snacks"),
    FoodCategory("minuman", "Minuman", "Drinks"),
)

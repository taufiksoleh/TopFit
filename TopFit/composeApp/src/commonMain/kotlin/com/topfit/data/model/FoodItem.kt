package com.topfit.data.model

data class FoodItem(
    val uid: String,
    val name: String,
    val emoji: String,
    val serving: String,
    val qty: Double,
    val kcal: Double,
    val p: Double,
    val c: Double,
    val f: Double,
    val meal: String, // sarapan | makan_siang | makan_malam | cemilan
)

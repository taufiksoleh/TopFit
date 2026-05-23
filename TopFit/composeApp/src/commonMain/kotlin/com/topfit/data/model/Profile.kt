package com.topfit.data.model

data class Profile(
    val name: String = "Andi",
    val weight: Double = 68.0,
    val targetWeight: Double = 64.0,
    val height: Int = 170,
    val age: Int = 28,
    val sex: String = "male",       // male | female
    val goal: String = "lose",      // lose | maintain | gain
    val pace: String = "normal",    // slow | normal | fast
    val activity: String = "moderate", // sedentary | light | moderate | active | very_active
    val dailyGoal: Int = 1980,
    val streak: Int = 7,
    val googleConnected: Boolean = false,
    val email: String = "",
)

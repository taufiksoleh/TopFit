package com.topfit.util

import com.topfit.data.model.Profile
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

fun calcDailyCalories(p: Profile): Int {
    val bmr = if (p.sex == "male")
        10 * p.weight + 6.25 * p.height - 5 * p.age + 5
    else
        10 * p.weight + 6.25 * p.height - 5 * p.age - 161

    val activityMult = when (p.activity) {
        "sedentary"   -> 1.2
        "light"       -> 1.375
        "moderate"    -> 1.55
        "active"      -> 1.725
        "very_active" -> 1.9
        else          -> 1.55
    }
    val tdee = bmr * activityMult
    val deficit = when (p.pace) { "slow" -> 275.0; "fast" -> 825.0; else -> 550.0 }
    val target = when (p.goal) {
        "lose" -> tdee - deficit
        "gain" -> tdee + deficit
        else   -> tdee
    }
    return (Math.round(target / 10) * 10).toInt()
}

fun todayString(): String =
    Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()

fun dateString(daysAgo: Int): String {
    val tz = TimeZone.currentSystemDefault()
    val today = Clock.System.todayIn(tz)
    return today.minus(kotlinx.datetime.DatePeriod(days = daysAgo)).toString()
}

fun fmtNum(n: Double): String =
    if (n >= 1000) n.toLong().toString().reversed().chunked(3).joinToString(".").reversed()
    else n.toLong().toString()

fun fmtNum(n: Int): String = fmtNum(n.toDouble())

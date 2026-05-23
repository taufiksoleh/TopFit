package com.topfit.viewmodel

import androidx.lifecycle.ViewModel
import com.topfit.data.model.*
import com.topfit.util.calcDailyCalories
import com.topfit.util.dateString
import com.topfit.util.todayString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class AppViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        AppState(
            selectedDate = todayString(),
            log = seedLog(),
        )
    )
    val state: StateFlow<AppState> = _state.asStateFlow()

    fun dispatch(action: AppAction) {
        _state.update { reduce(it, action) }
    }

    private fun reduce(s: AppState, a: AppAction): AppState = when (a) {
        is AppAction.SelectDate -> s.copy(selectedDate = a.date)

        is AppAction.ShowAdd -> s.copy(
            addSheetOpen = true,
            lastMeal = a.meal ?: s.lastMeal,
        )
        AppAction.CloseAdd -> s.copy(addSheetOpen = false)

        is AppAction.OpenAddMethod -> {
            val base = s.copy(addSheetOpen = false)
            when (a.method) {
                "scan"     -> base.copy(scanOpen = true)
                "describe" -> base.copy(describeOpen = true)
                "search"   -> base.copy(tab = "search")
                else       -> base
            }
        }

        AppAction.CloseScan -> s.copy(scanOpen = false)
        AppAction.CloseDescribe -> s.copy(describeOpen = false)

        is AppAction.ShowFoodDetail -> s.copy(foodDetailId = a.foodId)
        AppAction.CloseFoodDetail -> s.copy(foodDetailId = null)

        AppAction.ShowOnboarding -> s.copy(onboardingOpen = true)
        is AppAction.CompleteOnboarding -> s.copy(
            profile = a.profile.copy(dailyGoal = calcDailyCalories(a.profile)),
            onboardingOpen = false,
        )

        AppAction.ShowGoogle -> s.copy(googleOpen = true)
        AppAction.CloseGoogle -> s.copy(googleOpen = false)
        is AppAction.ConnectGoogle -> s.copy(
            profile = s.profile.copy(
                googleConnected = true,
                name = a.name,
                email = a.email,
            ),
        )

        is AppAction.SetTab -> s.copy(tab = a.tab)

        is AppAction.AddItem -> {
            val day = s.log[s.selectedDate] ?: emptyList()
            s.copy(
                log = s.log + (s.selectedDate to day + a.item),
                lastMeal = a.item.meal,
            )
        }
        is AppAction.RemoveItem -> {
            val day = s.log[s.selectedDate] ?: emptyList()
            s.copy(log = s.log + (s.selectedDate to day.filter { it.uid != a.uid }))
        }

        is AppAction.SetLang -> s.copy(lang = a.lang)
    }

    private fun seedLog(): Map<String, List<FoodItem>> {
        fun make(foodId: String, meal: String, qty: Double = 1.0): FoodItem {
            val f = FOOD_BY_ID[foodId]!!
            return FoodItem(
                uid = "i_${Random.nextInt(100000)}",
                name = f.name, emoji = f.emoji, serving = f.serving, qty = qty,
                kcal = f.kcal * qty, p = f.p * qty, c = f.c * qty, f = f.f * qty,
                meal = meal,
            )
        }
        return mapOf(
            todayString() to listOf(
                make("nasi-putih", "sarapan"),
                make("telur-dadar", "sarapan"),
                make("kopi-susu", "sarapan"),
                make("pisang-goreng", "cemilan"),
            ),
            dateString(1) to listOf(
                make("nasi-putih", "sarapan"),
                make("tempe-goreng", "sarapan"),
                make("soto-ayam", "makan_siang"),
                make("es-teh-manis", "makan_siang"),
                make("nasi-putih", "makan_malam"),
                make("ayam-bakar", "makan_malam"),
                make("tumis-kangkung", "makan_malam"),
            ),
            dateString(2) to listOf(make("nasi-goreng", "makan_siang"), make("ayam-goreng", "makan_malam"), make("nasi-putih", "makan_malam")),
            dateString(3) to listOf(make("mie-ayam", "makan_siang"), make("martabak-manis", "cemilan"), make("nasi-putih", "makan_malam"), make("ikan-tongkol", "makan_malam")),
            dateString(4) to listOf(make("bakso", "makan_siang"), make("nasi-putih", "makan_malam"), make("rendang", "makan_malam")),
            dateString(5) to listOf(make("gado-gado", "makan_siang"), make("nasi-putih", "sarapan"), make("telur-dadar", "sarapan")),
            dateString(6) to listOf(make("sate-ayam", "makan_malam"), make("nasi-putih", "makan_malam"), make("es-jeruk", "makan_malam")),
        )
    }
}

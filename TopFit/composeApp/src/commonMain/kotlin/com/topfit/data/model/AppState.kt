package com.topfit.data.model

data class AppState(
    val tab: String = "home",
    val lang: String = "id",
    val profile: Profile = Profile(),
    val log: Map<String, List<FoodItem>> = emptyMap(),
    val selectedDate: String = "",
    val addSheetOpen: Boolean = false,
    val scanOpen: Boolean = false,
    val describeOpen: Boolean = false,
    val foodDetailId: String? = null,
    val onboardingOpen: Boolean = false,
    val googleOpen: Boolean = false,
    val lastMeal: String = "makan_siang",
)

sealed class AppAction {
    data class SelectDate(val date: String) : AppAction()
    data class ShowAdd(val meal: String? = null) : AppAction()
    object CloseAdd : AppAction()
    data class OpenAddMethod(val method: String) : AppAction() // scan | describe | search
    object CloseScan : AppAction()
    object CloseDescribe : AppAction()
    data class ShowFoodDetail(val foodId: String) : AppAction()
    object CloseFoodDetail : AppAction()
    object ShowOnboarding : AppAction()
    data class CompleteOnboarding(val profile: Profile) : AppAction()
    object ShowGoogle : AppAction()
    object CloseGoogle : AppAction()
    data class ConnectGoogle(val name: String, val email: String) : AppAction()
    data class SetTab(val tab: String) : AppAction()
    data class AddItem(val item: FoodItem) : AppAction()
    data class RemoveItem(val uid: String) : AppAction()
    data class SetLang(val lang: String) : AppAction()
}

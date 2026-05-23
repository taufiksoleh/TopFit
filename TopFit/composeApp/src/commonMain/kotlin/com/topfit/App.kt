package com.topfit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.*
import com.topfit.data.model.AppAction
import com.topfit.data.model.AppState
import com.topfit.ui.modals.*
import com.topfit.ui.screens.*
import com.topfit.ui.theme.LocalTfColors
import com.topfit.ui.theme.Primary
import com.topfit.ui.theme.TopFitTheme
import com.topfit.viewmodel.AppViewModel

@Composable
fun App(viewModel: AppViewModel = AppViewModel()) {
    val state by viewModel.state.collectAsState()
    TopFitTheme {
        AppContent(state = state, dispatch = viewModel::dispatch)
    }
}

@Composable
private fun AppContent(state: AppState, dispatch: (AppAction) -> Unit) {
    val tabs = remember { listOf(TfTab.Home, TfTab.Search, TfTab.Progress, TfTab.Profile) }
    val currentTab = state.tab

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            // Screen content
            Box(Modifier.weight(1f)) {
                when (currentTab) {
                    "home"     -> HomeScreen(state, dispatch)
                    "search"   -> SearchScreen(state, dispatch)
                    "progress" -> ProgressScreen(state, dispatch)
                    "profile"  -> ProfileScreen(state, dispatch)
                }
            }
            // Bottom navigation
            BottomBar(currentTab = currentTab, dispatch = dispatch)
        }

        // Modals
        AddSheet(state = state, dispatch = dispatch)
        ScanModal(state = state, dispatch = dispatch)
        DescribeModal(state = state, dispatch = dispatch)
        FoodDetailModal(state = state, dispatch = dispatch)
        OnboardingModal(state = state, dispatch = dispatch)
        GoogleModal(state = state, dispatch = dispatch)
    }
}

@Composable
private fun BottomBar(currentTab: String, dispatch: (AppAction) -> Unit) {
    val tfColors = LocalTfColors.current
    val colors = MaterialTheme.colorScheme

    Box(
        Modifier.fillMaxWidth()
            .background(colors.surface)
    ) {
        // Top hairline
        Box(Modifier.fillMaxWidth().height(0.5.dp).background(tfColors.hairline))

        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp).height(64.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Home
            TabItem(
                icon = "🏠", label = "Beranda",
                selected = currentTab == "home",
                modifier = Modifier.weight(1f),
                onClick = { dispatch(AppAction.SetTab("home")) }
            )
            // Search
            TabItem(
                icon = "🔍", label = "Cari",
                selected = currentTab == "search",
                modifier = Modifier.weight(1f),
                onClick = { dispatch(AppAction.SetTab("search")) }
            )
            // FAB center
            Box(
                Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier.size(52.dp).clip(RoundedCornerShape(18.dp))
                        .background(Primary)
                        .clickable { dispatch(AppAction.ShowAdd) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("+", fontSize = 28.sp, color = Color.White, fontWeight = FontWeight.W300)
                }
            }
            // Progress
            TabItem(
                icon = "📊", label = "Progres",
                selected = currentTab == "progress",
                modifier = Modifier.weight(1f),
                onClick = { dispatch(AppAction.SetTab("progress")) }
            )
            // Profile
            TabItem(
                icon = "👤", label = "Profil",
                selected = currentTab == "profile",
                modifier = Modifier.weight(1f),
                onClick = { dispatch(AppAction.SetTab("profile")) }
            )
        }
    }
}

@Composable
private fun TabItem(icon: String, label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val tfColors = LocalTfColors.current
    val colors = MaterialTheme.colorScheme

    Column(
        modifier.clickable(onClick = onClick).padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(icon, fontSize = 20.sp)
        Text(
            label,
            fontSize = 9.sp,
            fontWeight = if (selected) FontWeight.W700 else FontWeight.W400,
            color = if (selected) Primary else tfColors.ink3,
        )
        if (selected) {
            Box(Modifier.size(4.dp).clip(CircleShape).background(Primary))
        }
    }
}

// Sealed tab objects for Voyager compatibility (used internally)
private sealed class TfTab(val id: String) {
    object Home : TfTab("home")
    object Search : TfTab("search")
    object Progress : TfTab("progress")
    object Profile : TfTab("profile")
}

package com.topfit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topfit.data.model.AppAction
import com.topfit.data.model.AppState
import com.topfit.data.model.FoodItem
import com.topfit.i18n.t
import com.topfit.ui.components.CalorieRing
import com.topfit.ui.components.FoodRow
import com.topfit.ui.components.MacroBar
import com.topfit.ui.components.TfCard
import com.topfit.ui.theme.Accent
import com.topfit.ui.theme.LocalTfColors
import com.topfit.ui.theme.Primary
import com.topfit.util.fmtNum
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

@Composable
fun HomeScreen(state: AppState, dispatch: (AppAction) -> Unit) {
    val lang = state.lang
    val tfColors = LocalTfColors.current
    val colors = MaterialTheme.colorScheme
    val day = state.log[state.selectedDate] ?: emptyList()
    val consumed = day.sumOf { it.kcal }
    val protein = day.sumOf { it.p }
    val carbs   = day.sumOf { it.c }
    val fat     = day.sumOf { it.f }
    val goal = state.profile.dailyGoal
    val pGoal = (goal * 0.25 / 4).toInt()
    val cGoal = (goal * 0.5 / 4).toInt()
    val fGoal = (goal * 0.25 / 9).toInt()

    val tz = TimeZone.currentSystemDefault()
    val today = Clock.System.todayIn(tz)
    val weekDays = (-3..3).map { today.plus(DatePeriod(days = it)) }

    val dayNamesId = listOf("Min","Sen","Sel","Rab","Kam","Jum","Sab")
    val dayNamesEn = listOf("Sun","Mon","Tue","Wed","Thu","Fri","Sat")

    val meals = listOf(
        Triple("sarapan",     t(lang,"meal_sarapan"),    "🌅"),
        Triple("makan_siang", t(lang,"meal_makan_siang"),"☀️"),
        Triple("makan_malam", t(lang,"meal_makan_malam"),"🌙"),
        Triple("cemilan",     t(lang,"meal_cemilan"),    "🍪"),
    )

    val hour = Clock.System.now().toLocalDateTime(tz).hour
    val greeting = when {
        hour < 11 -> t(lang, "morning")
        hour < 16 -> t(lang, "afternoon")
        else      -> t(lang, "evening")
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 8.dp)
    ) {
        // Header
        Row(
            Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(greeting, fontSize = 13.sp, color = tfColors.ink3, fontWeight = FontWeight.W500)
                Text(
                    state.profile.name.ifBlank { "Halo" },
                    fontSize = 26.sp, fontWeight = FontWeight.W500,
                    color = colors.onBackground, lineHeight = 28.sp,
                )
            }
            Box(
                Modifier.size(38.dp).clip(CircleShape)
                    .background(tfColors.soft)
                    .clickable { dispatch(AppAction.ShowGoogle) },
                contentAlignment = Alignment.Center,
            ) {
                if (state.profile.googleConnected)
                    Text(state.profile.name.firstOrNull()?.toString() ?: "A", fontSize = 14.sp, fontWeight = FontWeight.W600)
                else
                    Text("👤", fontSize = 16.sp)
            }
        }

        // Week strip
        Row(
            Modifier.fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(start = 12.dp, end = 12.dp, top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            weekDays.forEach { date ->
                val dateKey = date.toString()
                val isSelected = dateKey == state.selectedDate
                val isToday = date == today
                val dayName = if (lang == "id") dayNamesId[date.dayOfWeek.ordinal % 7] else dayNamesEn[date.dayOfWeek.ordinal % 7]

                Box(
                    Modifier
                        .width(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) Primary else Color.Transparent)
                        .clickable { dispatch(AppAction.SelectDate(dateKey)) }
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(dayName, fontSize = 10.sp, fontWeight = FontWeight.W600,
                            color = if (isSelected) Color.White.copy(alpha = 0.8f) else tfColors.ink3,
                            letterSpacing = 0.4.sp)
                        Text(date.dayOfMonth.toString(), fontSize = 16.sp, fontWeight = FontWeight.W600,
                            color = if (isSelected) Color.White else colors.onBackground)
                        if (isToday) Box(Modifier.size(4.dp).clip(CircleShape).background(if (isSelected) Color.White else Primary))
                        else Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }

        // Calorie ring card
        TfCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
            Column(Modifier.padding(horizontal = 18.dp, vertical = 20.dp)) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CalorieRing(consumed = consumed, goal = goal)
                }
                Spacer(Modifier.height(18.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    MacroBar(if(lang=="id") "Protein" else "Protein", protein, pGoal, Primary, Modifier.weight(1f))
                    MacroBar(if(lang=="id") "Karbo" else "Carbs",    carbs,   cGoal, Accent,  Modifier.weight(1f))
                    MacroBar(if(lang=="id") "Lemak" else "Fat",      fat,     fGoal, Color(0xFFD77A55), Modifier.weight(1f))
                }
            }
        }

        // Streak + goal
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TfCard(Modifier.weight(1f)) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFFEF3E6)), contentAlignment = Alignment.Center) {
                        Text("🔥", fontSize = 14.sp)
                    }
                    Column {
                        Text("${state.profile.streak}", fontSize = 18.sp, fontWeight = FontWeight.W600, lineHeight = 18.sp)
                        Text(t(lang,"streak"), fontSize = 10.sp, color = tfColors.ink3, maxLines = 1)
                    }
                }
            }
            TfCard(Modifier.weight(1f)) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(tfColors.soft), contentAlignment = Alignment.Center) {
                        Text("🎯", fontSize = 14.sp)
                    }
                    Column {
                        Text(fmtNum(goal), fontSize = 18.sp, fontWeight = FontWeight.W600, lineHeight = 18.sp)
                        Text(t(lang,"goal_label"), fontSize = 10.sp, color = tfColors.ink3, maxLines = 1)
                    }
                }
            }
        }

        // Meal sections
        Spacer(Modifier.height(16.dp))
        meals.forEach { (mealKey, mealLabel, emoji) ->
            val items = day.filter { it.meal == mealKey }
            val mkcal = items.sumOf { it.kcal }
            TfCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 10.dp)) {
                Row(
                    Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(emoji, fontSize = 18.sp)
                        Column {
                            Text(mealLabel, fontSize = 14.sp, fontWeight = FontWeight.W600, color = colors.onSurface)
                            Text(
                                if (items.isEmpty()) t(lang,"empty_meal_prompt") else "${items.size} item · ${fmtNum(mkcal)} kkal",
                                fontSize = 11.sp, color = tfColors.ink3,
                            )
                        }
                    }
                    Box(
                        Modifier.size(30.dp).clip(RoundedCornerShape(10.dp))
                            .background(tfColors.bgSoft)
                            .clickable { dispatch(AppAction.ShowAdd(mealKey)) },
                        contentAlignment = Alignment.Center,
                    ) { Text("+", fontSize = 18.sp, color = tfColors.ink3) }
                }
                if (items.isNotEmpty()) {
                    HorizontalDivider(color = tfColors.hairline, thickness = 0.5.dp)
                    items.forEachIndexed { i, item ->
                        FoodRow(
                            item = item,
                            onDelete = { dispatch(AppAction.RemoveItem(item.uid)) },
                            isLast = i == items.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

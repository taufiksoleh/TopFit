package com.topfit.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.topfit.data.model.AppAction
import com.topfit.data.model.AppState
import com.topfit.data.model.FOOD_BY_ID
import com.topfit.data.model.FoodItem
import com.topfit.i18n.t
import com.topfit.ui.theme.LocalTfColors
import com.topfit.ui.theme.Primary
import com.topfit.util.fmtNum
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailModal(state: AppState, dispatch: (AppAction) -> Unit) {
    val foodId = state.foodDetailId ?: return
    val food = FOOD_BY_ID[foodId] ?: return
    val lang = state.lang
    val tfColors = LocalTfColors.current
    val colors = MaterialTheme.colorScheme

    var qty by remember(foodId) { mutableStateOf(1.0) }
    var meal by remember(foodId) { mutableStateOf(state.lastMeal) }

    ModalBottomSheet(
        onDismissRequest = { dispatch(AppAction.CloseFoodDetail) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.surface,
    ) {
        Column(Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            // Header
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(food.emoji, fontSize = 36.sp)
                Column(Modifier.weight(1f)) {
                    Text(if (lang=="id") food.name else food.nameEn, fontSize = 22.sp, fontWeight = FontWeight.W500, color = colors.onSurface)
                    Text(if (lang=="id") food.serving else food.servingEn, fontSize = 12.sp, color = tfColors.ink3)
                }
                Box(Modifier.size(32.dp).clip(RoundedCornerShape(999.dp)).background(tfColors.bgSoft).clickable { dispatch(AppAction.CloseFoodDetail) }, contentAlignment = Alignment.Center) {
                    Text("✕", fontSize = 14.sp, color = tfColors.ink3)
                }
            }

            // Macro grid
            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).border(0.5.dp, tfColors.hairline, RoundedCornerShape(14.dp))) {
                MacroCell(fmtNum((food.kcal * qty).toInt()), "kkal", isHero = true, modifier = Modifier.weight(1.4f))
                Box(Modifier.width(0.5.dp).fillMaxHeight().background(tfColors.hairline))
                MacroCell(Math.round(food.p * qty).toString(), t(lang,"protein") + " g", modifier = Modifier.weight(1f))
                Box(Modifier.width(0.5.dp).fillMaxHeight().background(tfColors.hairline))
                MacroCell(Math.round(food.c * qty).toString(), t(lang,"carbs") + " g", modifier = Modifier.weight(1f))
                Box(Modifier.width(0.5.dp).fillMaxHeight().background(tfColors.hairline))
                MacroCell(Math.round(food.f * qty).toString(), t(lang,"fat") + " g", modifier = Modifier.weight(1f))
            }

            // Qty stepper
            Spacer(Modifier.height(18.dp))
            Text(t(lang,"portion"), fontSize = 11.sp, fontWeight = FontWeight.W600, color = tfColors.ink3, letterSpacing = 0.5.sp)
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(tfColors.bgSoft).padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                QtyBtn("-", enabled = qty > 0.25) { qty = maxOf(0.25, qty - 0.25) }
                Text(qty.toString(), Modifier.weight(1f), fontSize = 28.sp, fontWeight = FontWeight.W500, color = colors.onSurface, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                QtyBtn("+") { qty = minOf(10.0, qty + 0.25) }
            }

            // Meal selector
            Spacer(Modifier.height(14.dp))
            MealSegment(lang, meal, onSelect = { meal = it })

            // Add button
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = {
                    dispatch(AppAction.AddItem(FoodItem(
                        uid = "i_${Random.nextInt(100000)}",
                        name = if (lang=="id") food.name else food.nameEn,
                        emoji = food.emoji,
                        serving = if (lang=="id") food.serving else food.servingEn,
                        qty = qty,
                        kcal = food.kcal * qty, p = food.p * qty, c = food.c * qty, f = food.f * qty,
                        meal = meal,
                    )))
                    dispatch(AppAction.CloseFoodDetail)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
            ) {
                Text("${t(lang,"add")} · ${fmtNum((food.kcal * qty).toInt())} kkal", fontSize = 15.sp, fontWeight = FontWeight.W600)
            }
        }
    }
}

@Composable
private fun MacroCell(value: String, label: String, isHero: Boolean = false, modifier: Modifier = Modifier) {
    val tfColors = LocalTfColors.current
    Column(
        modifier.background(if (isHero) tfColors.soft else MaterialTheme.colorScheme.surface).padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.W500, color = if (isHero) Color(0xFF0A6F4D) else MaterialTheme.colorScheme.onSurface)
        Text(label, fontSize = 10.sp, color = tfColors.ink3, fontWeight = FontWeight.W600)
    }
}

@Composable
fun MealSegment(lang: String, selected: String, onSelect: (String) -> Unit) {
    val tfColors = LocalTfColors.current
    val meals = listOf("sarapan","makan_siang","makan_malam","cemilan")
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(tfColors.bgSoft).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        meals.forEach { m ->
            val isOn = m == selected
            Box(
                Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                    .background(if (isOn) MaterialTheme.colorScheme.surface else Color.Transparent)
                    .clickable { onSelect(m) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    t(lang, "meal_$m"),
                    fontSize = 11.sp, fontWeight = FontWeight.W600,
                    color = if (isOn) MaterialTheme.colorScheme.onSurface else tfColors.ink3,
                )
            }
        }
    }
}

@Composable
fun QtyBtn(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    Box(
        Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
            .background(if (enabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { Text(label, fontSize = 20.sp, color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(0.3f)) }
}

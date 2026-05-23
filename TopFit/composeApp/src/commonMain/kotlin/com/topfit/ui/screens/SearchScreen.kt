package com.topfit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topfit.data.model.AppAction
import com.topfit.data.model.AppState
import com.topfit.data.model.CATEGORIES
import com.topfit.data.model.FOODS
import com.topfit.i18n.t
import com.topfit.ui.components.TfCard
import com.topfit.ui.theme.LocalTfColors
import com.topfit.util.fmtNum

@Composable
fun SearchScreen(state: AppState, dispatch: (AppAction) -> Unit) {
    val lang = state.lang
    val tfColors = LocalTfColors.current
    val colors = MaterialTheme.colorScheme
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("all") }

    val filtered = FOODS.filter { food ->
        (category == "all" || food.category == category) &&
        (query.isBlank() || food.name.contains(query, true) || food.nameEn.contains(query, true))
    }

    Column(Modifier.fillMaxSize()) {
        // Title
        Text(
            t(lang, "search_title"),
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp),
            fontSize = 28.sp, fontWeight = FontWeight.W500, color = colors.onBackground,
        )

        // Search field
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
                .height(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(colors.surface)
                .border(0.5.dp, tfColors.hairline, RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("🔍", fontSize = 16.sp)
            BasicTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.weight(1f),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = colors.onSurface),
                singleLine = true,
                decorationBox = { inner ->
                    if (query.isEmpty()) Text(t(lang,"search_placeholder"), fontSize = 14.sp, color = tfColors.ink3)
                    inner()
                },
            )
            if (query.isNotEmpty()) {
                Box(Modifier.clickable { query = "" }) { Text("✕", fontSize = 14.sp, color = tfColors.ink3) }
            }
        }

        // Category chips
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            CATEGORIES.forEach { cat ->
                val isOn = category == cat.id
                Box(
                    Modifier.clip(RoundedCornerShape(999.dp))
                        .background(if (isOn) colors.onBackground else colors.surface)
                        .border(0.5.dp, tfColors.hairline, RoundedCornerShape(999.dp))
                        .clickable { category = cat.id }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                ) {
                    Text(
                        if (lang == "id") cat.labelId else cat.labelEn,
                        fontSize = 12.sp, fontWeight = FontWeight.W600,
                        color = if (isOn) colors.surface else tfColors.ink3,
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(t(lang, "search_empty"), fontSize = 14.sp, color = tfColors.ink3)
            }
        } else {
            TfCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                LazyColumn {
                    itemsIndexed(filtered) { i, food ->
                        Row(
                            Modifier.fillMaxWidth()
                                .clickable { dispatch(AppAction.ShowFoodDetail(food.id)) }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(food.emoji, fontSize = 22.sp, modifier = Modifier.width(32.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    if (lang == "id") food.name else food.nameEn,
                                    fontSize = 14.sp, fontWeight = FontWeight.W500, color = colors.onSurface,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    if (lang == "id") food.serving else food.servingEn,
                                    fontSize = 11.sp, color = tfColors.ink3,
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${food.kcal}", fontSize = 14.sp, fontWeight = FontWeight.W600, color = colors.onSurface)
                                Text("kkal", fontSize = 10.sp, color = tfColors.ink3)
                            }
                        }
                        if (i < filtered.lastIndex) HorizontalDivider(color = tfColors.hairline, thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

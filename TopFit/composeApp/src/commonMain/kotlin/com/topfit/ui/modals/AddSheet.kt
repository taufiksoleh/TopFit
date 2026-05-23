package com.topfit.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.topfit.i18n.t
import com.topfit.ui.theme.LocalTfColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSheet(state: AppState, dispatch: (AppAction) -> Unit) {
    if (!state.addSheetOpen) return
    val lang = state.lang
    val tfColors = LocalTfColors.current
    val colors = MaterialTheme.colorScheme

    ModalBottomSheet(
        onDismissRequest = { dispatch(AppAction.CloseAdd) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.surface,
    ) {
        Column(Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(t(lang,"add_title"), fontSize = 24.sp, fontWeight = FontWeight.W500, color = colors.onSurface)
                Box(
                    Modifier.size(32.dp).clip(RoundedCornerShape(999.dp))
                        .background(tfColors.bgSoft).clickable { dispatch(AppAction.CloseAdd) },
                    contentAlignment = Alignment.Center,
                ) { Text("✕", fontSize = 14.sp, color = tfColors.ink3) }
            }
            Text(t(lang,"add_subtitle"), fontSize = 13.sp, color = tfColors.ink3, modifier = Modifier.padding(bottom = 20.dp))

            val options = listOf(
                AddOption("scan",     "📷", t(lang,"add_scan"),     t(lang,"add_scan_desc"),     Color(0xFFE0F1EA), Color(0xFF0A6F4D), true),
                AddOption("describe", "✨", t(lang,"add_describe"),  t(lang,"add_describe_desc"), Color(0xFFFEF4E3), Color(0xFF9C5A09), true),
                AddOption("search",   "🔍", t(lang,"add_search"),    t(lang,"add_search_desc"),   Color(0xFFEEF2FB), Color(0xFF3C5B9C), false),
            )
            options.forEach { opt ->
                Row(
                    Modifier.fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.surface)
                        .border(0.5.dp, tfColors.hairline, RoundedCornerShape(16.dp))
                        .clickable { dispatch(AppAction.OpenAddMethod(opt.id)) }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Box(
                        Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(opt.iconBg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(opt.icon, fontSize = 20.sp)
                        if (opt.hasAi) {
                            Box(Modifier.align(Alignment.TopEnd).offset(x = 3.dp, y = (-3).dp)
                                .size(16.dp).clip(RoundedCornerShape(999.dp))
                                .background(colors.onBackground), contentAlignment = Alignment.Center) {
                                Text("✦", fontSize = 8.sp, color = colors.surface)
                            }
                        }
                    }
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(opt.title, fontSize = 15.sp, fontWeight = FontWeight.W600, color = colors.onSurface)
                            if (opt.hasAi) {
                                Box(Modifier.clip(RoundedCornerShape(999.dp)).background(Color(0xFF0F9D6C)).padding(horizontal = 5.dp, vertical = 1.5.dp)) {
                                    Text("AI", fontSize = 9.sp, fontWeight = FontWeight.W700, color = Color.White, letterSpacing = 0.5.sp)
                                }
                            }
                        }
                        Text(opt.desc, fontSize = 12.sp, color = tfColors.ink3)
                    }
                    Text("›", fontSize = 18.sp, color = tfColors.ink3)
                }
            }
        }
    }
}

private data class AddOption(val id: String, val icon: String, val title: String, val desc: String, val iconBg: Color, val iconFg: Color, val hasAi: Boolean)

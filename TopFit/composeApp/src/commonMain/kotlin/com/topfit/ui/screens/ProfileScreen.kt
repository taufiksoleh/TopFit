package com.topfit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topfit.data.model.AppAction
import com.topfit.data.model.AppState
import com.topfit.i18n.t
import com.topfit.ui.components.TfCard
import com.topfit.ui.theme.LocalTfColors
import com.topfit.ui.theme.Primary
import com.topfit.ui.theme.PrimaryDeep
import com.topfit.util.fmtNum

@Composable
fun ProfileScreen(state: AppState, dispatch: (AppAction) -> Unit) {
    val lang = state.lang
    val profile = state.profile
    val tfColors = LocalTfColors.current
    val colors = MaterialTheme.colorScheme

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 20.dp)
    ) {
        Text(
            t(lang, "profile_title"),
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp),
            fontSize = 28.sp, fontWeight = FontWeight.W500, color = colors.onBackground,
        )

        // User card
        TfCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) {
            Column(Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(Modifier.size(56.dp).clip(CircleShape).background(tfColors.soft), contentAlignment = Alignment.Center) {
                        if (profile.googleConnected)
                            Text(profile.name.firstOrNull()?.toString() ?: "A", fontSize = 22.sp, fontWeight = FontWeight.W600)
                        else Text("👤", fontSize = 26.sp)
                    }
                    Column(Modifier.weight(1f)) {
                        Text(profile.name.ifBlank { "Tamu" }, fontSize = 17.sp, fontWeight = FontWeight.W600, color = colors.onSurface)
                        Text(
                            if (profile.googleConnected) profile.email else t(lang,"not_signed_in"),
                            fontSize = 12.sp, color = tfColors.ink3,
                        )
                    }
                }
                // Stats grid
                Spacer(Modifier.height(18.dp))
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).border(0.5.dp, tfColors.hairline, RoundedCornerShape(14.dp))) {
                    listOf(
                        Triple(t(lang,"weight"), "${profile.weight}", "kg"),
                        Triple(t(lang,"target_weight"), "${profile.targetWeight}", "kg"),
                        Triple(t(lang,"height"), "${profile.height}", "cm"),
                        Triple(t(lang,"age"), "${profile.age}", ""),
                    ).forEachIndexed { i, (label, value, unit) ->
                        Column(
                            Modifier.weight(1f).background(colors.surface).padding(vertical = 10.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Row(verticalAlignment = Alignment.Baseline) {
                                Text(value, fontSize = 22.sp, fontWeight = FontWeight.W500, color = colors.onSurface)
                                if (unit.isNotEmpty()) Text(unit, fontSize = 11.sp, color = tfColors.ink3, modifier = Modifier.padding(start = 1.dp))
                            }
                            Text(label, fontSize = 10.sp, color = tfColors.ink3, fontWeight = FontWeight.W500)
                        }
                        if (i < 3) Box(Modifier.width(0.5.dp).height(52.dp).background(tfColors.hairline))
                    }
                }
            }
        }

        // Daily goal card
        TfCard(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            color = Color.Transparent,
        ) {
            Box(
                Modifier.fillMaxWidth().background(
                    Brush.linearGradient(listOf(Primary, PrimaryDeep))
                ).padding(horizontal = 18.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = { dispatch(AppAction.ShowOnboarding) },
                    modifier = Modifier.align(Alignment.TopEnd),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.15f),
                        contentColor = Color.White,
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                ) {
                    Text(t(lang,"edit"), fontSize = 12.sp, fontWeight = FontWeight.W600)
                }
                Column {
                    Text(t(lang,"daily_calorie_goal"), fontSize = 10.sp, color = Color.White.copy(0.85f), fontWeight = FontWeight.W600, letterSpacing = 0.6.sp)
                    Row(verticalAlignment = Alignment.Baseline, modifier = Modifier.padding(top = 6.dp)) {
                        Text(fmtNum(profile.dailyGoal), fontSize = 40.sp, fontWeight = FontWeight.W500, color = Color.White, lineHeight = 40.sp)
                        Text(" kkal", fontSize = 16.sp, color = Color.White.copy(0.8f), fontWeight = FontWeight.W600)
                    }
                    Text("${t(lang,"activity_${profile.activity}")} · ${t(lang,"goal_${profile.goal}")}", fontSize = 12.sp, color = Color.White.copy(0.85f), modifier = Modifier.padding(top = 8.dp))
                }
            }
        }

        // Google connect
        if (!profile.googleConnected) {
            Spacer(Modifier.height(12.dp))
            TfCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp).clickable { dispatch(AppAction.ShowGoogle) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text("G", fontSize = 20.sp, fontWeight = FontWeight.W700, color = Primary)
                    Column(Modifier.weight(1f)) {
                        Text(t(lang,"connect_google"), fontSize = 14.sp, fontWeight = FontWeight.W600, color = colors.onSurface)
                        Text(t(lang,"connect_google_desc"), fontSize = 12.sp, color = tfColors.ink3)
                    }
                    Box(
                        Modifier.clip(CircleShape).background(colors.onBackground).padding(horizontal = 14.dp, vertical = 8.dp)
                    ) { Text(t(lang,"add"), fontSize = 12.sp, fontWeight = FontWeight.W600, color = colors.surface) }
                }
            }
        }

        // Settings
        Spacer(Modifier.height(14.dp))
        Text(t(lang,"settings"), modifier = Modifier.padding(horizontal = 22.dp, vertical = 6.dp),
            fontSize = 10.sp, fontWeight = FontWeight.W600, color = tfColors.ink3, letterSpacing = 0.5.sp)
        TfCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            listOf(
                Triple("🔔", t(lang,"notifications"), "08:00"),
                Triple("🌐", t(lang,"units"), "metric"),
                Triple("👤", t(lang,"privacy"), null),
                Triple("✨", t(lang,"about"), null),
            ).forEachIndexed { i, (icon, label, value) ->
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(tfColors.bgSoft), contentAlignment = Alignment.Center) {
                        Text(icon, fontSize = 14.sp)
                    }
                    Text(label, Modifier.weight(1f), fontSize = 14.sp, fontWeight = FontWeight.W500, color = colors.onSurface)
                    if (value != null) Text(value, fontSize = 13.sp, color = tfColors.ink3)
                    Text("›", fontSize = 18.sp, color = tfColors.ink3)
                }
                if (i < 3) HorizontalDivider(color = tfColors.hairline, thickness = 0.5.dp)
            }
        }
    }
}

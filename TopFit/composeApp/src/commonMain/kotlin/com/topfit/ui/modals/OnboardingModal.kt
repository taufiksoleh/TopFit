package com.topfit.ui.modals

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.topfit.data.model.AppAction
import com.topfit.data.model.AppState
import com.topfit.data.model.Profile
import com.topfit.i18n.t
import com.topfit.ui.theme.LocalTfColors
import com.topfit.ui.theme.Primary
import com.topfit.util.calcDailyCalories
import com.topfit.util.fmtNum

@Composable
fun OnboardingModal(state: AppState, dispatch: (AppAction) -> Unit) {
    if (!state.onboardingOpen) return
    val lang = state.lang
    val tfColors = LocalTfColors.current
    val colors = MaterialTheme.colorScheme
    val p = state.profile

    var step by remember { mutableStateOf(0) }
    var draft by remember { mutableStateOf(p) }
    val calcGoal = calcDailyCalories(draft)

    Dialog(onDismissRequest = {}, properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = false)) {
        Column(Modifier.fillMaxSize().background(colors.background)) {
            // Header with progress dots
            Row(
                Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 50.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (step in 1..3) {
                    Box(Modifier.size(32.dp).clip(RoundedCornerShape(999.dp)).background(tfColors.bgSoft).clickable { step-- }, contentAlignment = Alignment.Center) {
                        Text("‹", fontSize = 20.sp, color = tfColors.ink3)
                    }
                } else Spacer(Modifier.width(32.dp))
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    (0..4).forEach { i ->
                        Box(
                            Modifier.padding(horizontal = 3.dp)
                                .height(7.dp)
                                .width(if (i <= step) 18.dp else 7.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (i <= step) Primary else tfColors.hairline)
                        )
                    }
                }
                Spacer(Modifier.width(32.dp))
            }

            AnimatedContent(
                targetState = step,
                transitionSpec = { slideInHorizontally { it } togetherWith slideOutHorizontally { -it } },
                modifier = Modifier.weight(1f),
                label = "onbStep",
            ) { s ->
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 20.dp)) {
                    when (s) {
                        0 -> WelcomeStep(lang, tfColors)
                        1 -> AboutStep(lang, draft) { draft = it }
                        2 -> GoalStep(lang, draft) { draft = it }
                        3 -> ActivityStep(lang, draft) { draft = it }
                        4 -> ResultStep(lang, calcGoal, draft, tfColors)
                    }
                    Spacer(Modifier.height(100.dp))
                }
            }

            Box(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
                Button(
                    onClick = {
                        if (step < 4) step++ else {
                            dispatch(AppAction.CompleteOnboarding(draft.copy(dailyGoal = calcGoal)))
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                ) {
                    Text(when(step) { 0 -> t(lang,"onb_start"); 4 -> t(lang,"onb_done_cta"); else -> t(lang,"next") }, fontSize = 15.sp, fontWeight = FontWeight.W600)
                }
            }
        }
    }
}

@Composable private fun WelcomeStep(lang: String, tfColors: com.topfit.ui.theme.TfColors) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(30.dp))
        Box(Modifier.size(80.dp).clip(RoundedCornerShape(22.dp)).background(Primary), contentAlignment = Alignment.Center) {
            Text("✓", fontSize = 36.sp, color = Color.White, fontWeight = FontWeight.W700)
        }
        Spacer(Modifier.height(24.dp))
        Text(t(lang,"onb_welcome_title"), fontSize = 32.sp, fontWeight = FontWeight.W500, textAlign = TextAlign.Center, lineHeight = 36.sp)
        Spacer(Modifier.height(8.dp))
        Text(t(lang,"onb_welcome_sub"), fontSize = 15.sp, color = tfColors.ink3, textAlign = TextAlign.Center, lineHeight = 22.sp, modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(Modifier.height(40.dp))
        listOf(
            Triple("📷", t(lang,"add_scan"), t(lang,"add_scan_desc")),
            Triple("✨", t(lang,"add_describe"), t(lang,"add_describe_desc")),
            Triple("🔓", t(lang,"no_login_title"), t(lang,"no_login_desc")),
        ).forEach { (icon, title, desc) ->
            Row(
                Modifier.fillMaxWidth().padding(bottom = 12.dp).clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(0.5.dp, tfColors.hairline, RoundedCornerShape(16.dp)).padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(tfColors.soft), contentAlignment = Alignment.Center) { Text(icon, fontSize = 16.sp) }
                Column {
                    Text(title, fontSize = 14.sp, fontWeight = FontWeight.W600)
                    Text(desc, fontSize = 12.sp, color = tfColors.ink3)
                }
            }
        }
    }
}

@Composable private fun AboutStep(lang: String, draft: Profile, onUpdate: (Profile) -> Unit) {
    val tfColors = LocalTfColors.current
    Text(t(lang,"onb_step_about"), fontSize = 28.sp, fontWeight = FontWeight.W500)
    Text(t(lang,"help_calc"), fontSize = 14.sp, color = tfColors.ink3, modifier = Modifier.padding(top = 4.dp, bottom = 24.dp))
    FieldGroup(t(lang,"nickname")) {
        OutlinedTextField(value = draft.name, onValueChange = { onUpdate(draft.copy(name=it)) },
            placeholder = { Text(t(lang,"nickname_hint"), color = tfColors.ink3) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true)
    }
    FieldGroup(t(lang,"sex")) {
        SegmentedControl(listOf(t(lang,"male") to "male", t(lang,"female") to "female"), draft.sex) { onUpdate(draft.copy(sex=it)) }
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(Modifier.weight(1f)) { NumField(t(lang,"age"), draft.age, 14, 90) { onUpdate(draft.copy(age=it)) } }
        Column(Modifier.weight(1f)) { NumField("${t(lang,"height")} (cm)", draft.height, 140, 210) { onUpdate(draft.copy(height=it)) } }
    }
    Spacer(Modifier.height(12.dp))
    NumField("${t(lang,"weight")} (kg)", draft.weight.toInt(), 35, 200) { onUpdate(draft.copy(weight=it.toDouble())) }
}

@Composable private fun GoalStep(lang: String, draft: Profile, onUpdate: (Profile) -> Unit) {
    val tfColors = LocalTfColors.current
    Text(t(lang,"onb_step_goal"), fontSize = 28.sp, fontWeight = FontWeight.W500)
    Text(t(lang,"what_goal"), fontSize = 14.sp, color = tfColors.ink3, modifier = Modifier.padding(top = 4.dp, bottom = 24.dp))
    listOf("lose" to "📉", "maintain" to "⚖️", "gain" to "📈").forEach { (g, emoji) ->
        RadioCard(emoji, t(lang,"goal_$g"), draft.goal==g, Modifier.padding(bottom = 8.dp)) { onUpdate(draft.copy(goal=g)) }
    }
    if (draft.goal != "maintain") {
        Spacer(Modifier.height(8.dp))
        FieldGroup(t(lang,"pace_label")) {
            SegmentedControl(listOf(t(lang,"pace_slow") to "slow", t(lang,"pace_normal") to "normal", t(lang,"pace_fast") to "fast"), draft.pace) { onUpdate(draft.copy(pace=it)) }
        }
    }
    Spacer(Modifier.height(8.dp))
    NumField("${t(lang,"target_weight")} (kg)", draft.targetWeight.toInt(), 35, 200) { onUpdate(draft.copy(targetWeight=it.toDouble())) }
}

@Composable private fun ActivityStep(lang: String, draft: Profile, onUpdate: (Profile) -> Unit) {
    val tfColors = LocalTfColors.current
    Text(t(lang,"onb_step_activity"), fontSize = 28.sp, fontWeight = FontWeight.W500)
    Text(t(lang,"how_active"), fontSize = 14.sp, color = tfColors.ink3, modifier = Modifier.padding(top = 4.dp, bottom = 24.dp))
    listOf("sedentary","light","moderate","active","very_active").forEach { a ->
        Row(
            Modifier.fillMaxWidth().padding(bottom = 8.dp).clip(RoundedCornerShape(14.dp))
                .background(if (draft.activity==a) tfColors.soft else MaterialTheme.colorScheme.surface)
                .border(1.5.dp, if (draft.activity==a) Primary else tfColors.hairline, RoundedCornerShape(14.dp))
                .clickable { onUpdate(draft.copy(activity=a)) }.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(Modifier.weight(1f)) {
                Text(t(lang,"activity_$a"), fontSize = 15.sp, fontWeight = FontWeight.W600)
                Text(t(lang,"activity_${a}_desc"), fontSize = 12.sp, color = tfColors.ink3)
            }
            RadioDot(draft.activity == a)
        }
    }
}

@Composable private fun ResultStep(lang: String, goal: Int, draft: Profile, tfColors: com.topfit.ui.theme.TfColors) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(30.dp))
        Text(t(lang,"onb_calc_title"), fontSize = 11.sp, fontWeight = FontWeight.W600, color = Primary, letterSpacing = 0.5.sp)
        Text(fmtNum(goal), fontSize = 72.sp, fontWeight = FontWeight.W500, color = MaterialTheme.colorScheme.onBackground, lineHeight = 72.sp, modifier = Modifier.padding(top = 12.dp))
        Text("kkal · ${t(lang,"per_day")}", fontSize = 13.sp, color = tfColors.ink3, modifier = Modifier.padding(top = 4.dp))
        Text(t(lang,"onb_calc_sub"), fontSize = 13.sp, color = tfColors.ink3, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 20.dp, bottom = 30.dp, start = 16.dp, end = 16.dp))
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.surface).border(0.5.dp, tfColors.hairline, RoundedCornerShape(14.dp))) {
            SumRow(t(lang,"goals"), t(lang,"goal_${draft.goal}"), tfColors, false)
            SumRow(t(lang,"activity"), t(lang,"activity_${draft.activity}"), tfColors, false)
            if (draft.goal != "maintain") SumRow(t(lang,"pace_label"), t(lang,"pace_${draft.pace}"), tfColors, true)
        }
    }
}

@Composable private fun SumRow(label: String, value: String, tfColors: com.topfit.ui.theme.TfColors, isLast: Boolean) {
    Column {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 14.sp, color = tfColors.ink3)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.W600)
        }
        if (!isLast) HorizontalDivider(color = tfColors.hairline, thickness = 0.5.dp)
    }
}

@Composable private fun FieldGroup(label: String, content: @Composable () -> Unit) {
    val tfColors = LocalTfColors.current
    Text(label, fontSize = 11.sp, fontWeight = FontWeight.W600, color = tfColors.ink3, letterSpacing = 0.5.sp, modifier = Modifier.padding(bottom = 6.dp))
    content()
    Spacer(Modifier.height(16.dp))
}

@Composable private fun SegmentedControl(options: List<Pair<String,String>>, selected: String, onSelect: (String) -> Unit) {
    val tfColors = LocalTfColors.current
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(tfColors.bgSoft).padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        options.forEach { (label, value) ->
            Box(
                Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                    .background(if (selected==value) MaterialTheme.colorScheme.surface else Color.Transparent)
                    .clickable { onSelect(value) }.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) { Text(label, fontSize = 13.sp, fontWeight = FontWeight.W600, color = if (selected==value) MaterialTheme.colorScheme.onSurface else tfColors.ink3) }
        }
    }
}

@Composable private fun NumField(label: String, value: Int, min: Int, max: Int, onUpdate: (Int) -> Unit) {
    val tfColors = LocalTfColors.current
    Text(label, fontSize = 11.sp, fontWeight = FontWeight.W600, color = tfColors.ink3, letterSpacing = 0.5.sp, modifier = Modifier.padding(bottom = 6.dp))
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surface).border(0.5.dp, tfColors.hairline, RoundedCornerShape(12.dp)).height(42.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.width(32.dp).fillMaxHeight().clickable(enabled=value>min) { onUpdate(value-1) }, contentAlignment = Alignment.Center) {
            Text("−", fontSize = 18.sp, color = if (value>min) MaterialTheme.colorScheme.onSurface else tfColors.ink3)
        }
        Text(value.toString(), Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 16.sp, fontWeight = FontWeight.W600)
        Box(Modifier.width(32.dp).fillMaxHeight().clickable(enabled=value<max) { onUpdate(value+1) }, contentAlignment = Alignment.Center) {
            Text("+", fontSize = 18.sp, color = if (value<max) MaterialTheme.colorScheme.onSurface else tfColors.ink3)
        }
    }
}

@Composable private fun RadioCard(emoji: String, label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val tfColors = LocalTfColors.current
    Row(
        modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(if (selected) tfColors.soft else MaterialTheme.colorScheme.surface)
            .border(1.5.dp, if (selected) Primary else tfColors.hairline, RoundedCornerShape(14.dp))
            .clickable(onClick=onClick).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(emoji, fontSize = 22.sp)
        Text(label, Modifier.weight(1f), fontSize = 15.sp, fontWeight = FontWeight.W600, textAlign = TextAlign.Start)
        RadioDot(selected)
    }
}

@Composable private fun RadioDot(selected: Boolean) {
    val tfColors = LocalTfColors.current
    Box(Modifier.size(20.dp).clip(CircleShape).background(if (selected) Primary else Color.Transparent).border(1.5.dp, if (selected) Primary else tfColors.ink3, CircleShape), contentAlignment = Alignment.Center) {
        if (selected) Box(Modifier.size(8.dp).clip(CircleShape).background(Color.White))
    }
}

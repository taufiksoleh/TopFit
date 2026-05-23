package com.topfit.ui.modals

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import kotlinx.coroutines.launch
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.topfit.data.model.*
import com.topfit.i18n.t
import com.topfit.ui.theme.LocalTfColors
import com.topfit.ui.theme.Primary
import com.topfit.util.fmtNum
import kotlin.math.roundToInt
import kotlin.random.Random

private data class SamplePlate(
    val id: String, val titleId: String, val titleEn: String,
    val bgColors: List<Color>, val items: List<String>, val confidence: Float,
)

private val SAMPLE_PLATES = listOf(
    SamplePlate("plate1","Nasi padang lengkap","Padang rice plate",
        listOf(Color(0xFFF9B27D),Color(0xFFD97744),Color(0xFF8A3F1A)),
        listOf("nasi-putih","rendang","tempe-orek","toge"), 0.94f),
    SamplePlate("plate2","Ayam goreng + nasi","Fried chicken plate",
        listOf(Color(0xFFF4D68D),Color(0xFFB87432),Color(0xFF5C3413)),
        listOf("nasi-putih","ayam-goreng","tahu-goreng"), 0.91f),
    SamplePlate("plate3","Bakso + es teh","Meatball soup + iced tea",
        listOf(Color(0xFFC69F6A),Color(0xFF6E3C1A)),
        listOf("bakso","kerupuk","es-teh-manis"), 0.88f),
)

private enum class ScanPhase { AIM, ANALYZE, RESULT }

@Composable
fun ScanModal(state: AppState, dispatch: (AppAction) -> Unit) {
    if (!state.scanOpen) return
    val lang = state.lang
    val tfColors = LocalTfColors.current

    val scope = rememberCoroutineScope()
    var phase by remember { mutableStateOf(ScanPhase.AIM) }
    var plateIdx by remember { mutableStateOf(0) }
    var detected by remember { mutableStateOf<List<DetectedItem>>(emptyList()) }
    var meal by remember { mutableStateOf(state.lastMeal) }

    val plate = SAMPLE_PLATES[plateIdx]

    Dialog(
        onDismissRequest = { dispatch(AppAction.CloseScan) },
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = true),
    ) {
        Box(Modifier.fillMaxSize().background(Color(0xFF0A0F0C))) {
            Column(Modifier.fillMaxSize()) {
                // Header
                Row(
                    Modifier.fillMaxWidth().padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(32.dp).clip(CircleShape).background(Color.White.copy(0.15f)).clickable { dispatch(AppAction.CloseScan) }, contentAlignment = Alignment.Center) {
                        Text("✕", fontSize = 16.sp, color = Color.White)
                    }
                    Text(
                        if (phase == ScanPhase.RESULT) t(lang,"scan_review") else t(lang,"scan_title"),
                        Modifier.weight(1f), textAlign = TextAlign.Center,
                        fontSize = 14.sp, fontWeight = FontWeight.W600, color = Color.White,
                    )
                    Spacer(Modifier.width(32.dp))
                }

                when (phase) {
                    ScanPhase.AIM -> AimPhase(
                        plate = plate, plateIdx = plateIdx, lang = lang,
                        onSelectPlate = { plateIdx = it },
                        onCapture = {
                            phase = ScanPhase.ANALYZE
                            scope.launch {
                                kotlinx.coroutines.delay(2400)
                                detected = plate.items.mapIndexed { i, id ->
                                    val f = FOOD_BY_ID[id]!!
                                    DetectedItem(
                                        uid = "d_${Random.nextInt(100000)}",
                                        foodId = id,
                                        name = if (lang=="id") f.name else f.nameEn,
                                        emoji = f.emoji,
                                        serving = if (lang=="id") f.serving else f.servingEn,
                                        qty = 1.0,
                                        kcal = f.kcal.toDouble(), p = f.p.toDouble(), c = f.c.toDouble(), f = f.f.toDouble(),
                                        confidence = plate.confidence - i * 0.02f,
                                    )
                                }
                                phase = ScanPhase.RESULT
                            }
                        },
                    )
                    ScanPhase.ANALYZE -> AnalyzePhase(plate = plate, lang = lang)
                    ScanPhase.RESULT -> ResultPhase(
                        plate = plate, detected = detected, meal = meal, lang = lang,
                        onQtyChange = { uid, delta ->
                            detected = detected.map { it.uid != uid ? it : run {
                                val newQty = maxOf(0.25, minOf(5.0, ((it.qty + delta) * 4).roundToInt() / 4.0))
                                val ratio = newQty / it.qty
                                it.copy(qty=newQty, kcal=it.kcal*ratio, p=it.p*ratio, c=it.c*ratio, f=it.f*ratio)
                            }}
                        },
                        onRemove = { uid -> detected = detected.filter { it.uid != uid } },
                        onMealChange = { meal = it },
                        onConfirm = {
                            detected.forEach { it2 ->
                                dispatch(AppAction.AddItem(FoodItem("i_${Random.nextInt(100000)}", it2.name, it2.emoji, it2.serving, it2.qty, it2.kcal, it2.p, it2.c, it2.f, meal)))
                            }
                            dispatch(AppAction.CloseScan)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.AimPhase(plate: SamplePlate, plateIdx: Int, lang: String, onSelectPlate: (Int) -> Unit, onCapture: () -> Unit) {
    val tfColors = LocalTfColors.current
    Box(
        Modifier.weight(1f).fillMaxWidth().padding(20.dp).clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(plate.bgColors)),
        contentAlignment = Alignment.Center,
    ) {
        // Plate visualization
        Box(Modifier.size(180.dp).clip(CircleShape).background(Color(0xFFF5EDE0))) {
            plate.items.take(4).forEachIndexed { i, _ ->
                Box(Modifier.size(60.dp).offset(
                    x = listOf((-24).dp, 24.dp, (-20).dp, 22.dp)[i],
                    y = listOf((-20).dp, (-16).dp, 24.dp, 20.dp)[i],
                ).clip(RoundedCornerShape(60))
                    .background(listOf(Color(0xFFF9E2B6),Color(0xFFD9A85D),Color(0xFFA86D3D),Color(0xFF7C4423))[i]))
            }
        }
        // Viewfinder corners
        val cornerSize = 28.dp
        val borderWidth = 2.5.dp
        val cornerColor = Color.White.copy(0.85f)
        listOf(Alignment.TopStart, Alignment.TopEnd, Alignment.BottomStart, Alignment.BottomEnd).forEach { a ->
            Box(Modifier.fillMaxSize().padding(18.dp)) {
                Box(Modifier.size(cornerSize).align(a).border(
                    width = borderWidth,
                    color = cornerColor,
                    shape = when(a) {
                        Alignment.TopStart -> RoundedCornerShape(topStart = 10.dp)
                        Alignment.TopEnd   -> RoundedCornerShape(topEnd = 10.dp)
                        Alignment.BottomStart -> RoundedCornerShape(bottomStart = 10.dp)
                        else -> RoundedCornerShape(bottomEnd = 10.dp)
                    }
                ))
            }
        }
    }

    // Plate picker
    Column(Modifier.padding(horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(t(lang,"scan_sample"), fontSize = 10.sp, fontWeight = FontWeight.W600, color = Color.White.copy(0.6f), letterSpacing = 0.5.sp)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SAMPLE_PLATES.forEachIndexed { i, p ->
                val isOn = i == plateIdx
                Box(
                    Modifier.size(48.dp).clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(p.bgColors))
                        .border(if (isOn) 2.dp else 0.dp, Color.White, RoundedCornerShape(14.dp))
                        .clickable { onSelectPlate(i) },
                    contentAlignment = Alignment.Center,
                ) { if (isOn) Text("✓", color = Color.White, fontWeight = FontWeight.W700) }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(if (lang=="id") plate.titleId else plate.titleEn, fontSize = 12.sp, color = Color.White.copy(0.7f))
    }

    // Capture button
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), horizontalArrangement = Arrangement.Center) {
        Box(
            Modifier.size(72.dp).clip(CircleShape)
                .border(3.dp, Color.White, CircleShape).padding(4.dp)
                .clickable(onClick = onCapture),
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.fillMaxSize().clip(CircleShape).background(Color.White))
        }
    }
}

@Composable
private fun ColumnScope.AnalyzePhase(plate: SamplePlate, lang: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val dotAlpha by infiniteTransition.animateFloat(0.5f, 1f, infiniteRepeatable(tween(600), RepeatMode.Reverse), label = "dot")

    Column(
        Modifier.weight(1f).fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            Modifier.size(240.dp).clip(RoundedCornerShape(24.dp)).background(Brush.linearGradient(plate.bgColors)),
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.size(140.dp).clip(CircleShape).background(Color(0xFFF5EDE0)))
            // Sweep animation
            val sweep by infiniteTransition.animateFloat(0f, 1f, infiniteRepeatable(tween(1600, easing = LinearEasing)), label = "sweep")
            Box(Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Primary.copy(0.4f), Primary.copy(0f)),
                    startY = sweep * 240f, endY = sweep * 240f + 100f)))
        }
        Spacer(Modifier.height(30.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(3) { i ->
                Box(Modifier.size(8.dp).clip(CircleShape).background(Primary.copy(alpha = dotAlpha)))
            }
        }
        Spacer(Modifier.height(14.dp))
        Text(t(lang,"scan_analyzing"), fontSize = 17.sp, fontWeight = FontWeight.W600, color = Color.White)
        Spacer(Modifier.height(6.dp))
        Text(t(lang,"ai_identify"), fontSize = 12.sp, color = Color.White.copy(0.6f))
    }
}

@Composable
private fun ColumnScope.ResultPhase(
    plate: SamplePlate, detected: List<DetectedItem>, meal: String, lang: String,
    onQtyChange: (String, Double) -> Unit, onRemove: (String) -> Unit,
    onMealChange: (String) -> Unit, onConfirm: () -> Unit,
) {
    val tfColors = LocalTfColors.current
    val totalKcal = detected.sumOf { it.kcal }

    Column(
        Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Image header
        Row(
            Modifier.fillMaxWidth().height(160.dp).background(Brush.linearGradient(plate.bgColors)).padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Box(Modifier.size(110.dp).clip(CircleShape).background(Color(0xFFF5EDE0)))
            Column {
                Text("${t(lang,"scan_detected")} · ${detected.size} item", fontSize = 11.sp, color = Color.White.copy(0.75f), fontWeight = FontWeight.W600)
                Text("${fmtNum(totalKcal.toInt())} kkal", fontSize = 36.sp, fontWeight = FontWeight.W500, color = Color.White, lineHeight = 36.sp)
            }
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(14.dp)) {
            detected.forEach { item ->
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 6.dp)
                        .clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.surface)
                        .border(0.5.dp, tfColors.hairline, RoundedCornerShape(14.dp)).padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(item.emoji, fontSize = 22.sp)
                    Column(Modifier.weight(1f)) {
                        Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.W600, color = MaterialTheme.colorScheme.onSurface)
                        Text("${if(item.qty!=1.0) "${item.qty}× " else ""}${item.serving} · ${fmtNum(item.kcal.toInt())} kkal", fontSize = 11.sp, color = tfColors.ink3)
                    }
                    Row(Modifier.clip(RoundedCornerShape(10.dp)).background(tfColors.bgSoft).padding(3.dp), verticalAlignment = Alignment.CenterVertically) {
                        QtyBtn("−", enabled = item.qty > 0.25) { onQtyChange(item.uid, -0.25) }
                        Text(item.qty.toString(), Modifier.width(28.dp), textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.W600)
                        QtyBtn("+") { onQtyChange(item.uid, 0.25) }
                    }
                    Box(Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(tfColors.bgSoft).clickable { onRemove(item.uid) }, contentAlignment = Alignment.Center) {
                        Text("✕", fontSize = 12.sp, color = tfColors.ink3)
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            Text(t(lang,"scan_add_to"), fontSize = 11.sp, fontWeight = FontWeight.W600, color = tfColors.ink3, letterSpacing = 0.5.sp)
            Spacer(Modifier.height(8.dp))
            MealSegment(lang, meal, onSelect = onMealChange)
            Spacer(Modifier.height(80.dp))
        }
    }

    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(horizontal = 20.dp, vertical = 16.dp)) {
        Button(
            onClick = onConfirm, enabled = detected.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
        ) {
            Text("${t(lang,"add")} · ${fmtNum(totalKcal.toInt())} kkal", fontSize = 15.sp, fontWeight = FontWeight.W600)
        }
    }
}

data class DetectedItem(val uid: String, val foodId: String, val name: String, val emoji: String,
    val serving: String, val qty: Double, val kcal: Double, val p: Double, val c: Double, val f: Double, val confidence: Float)

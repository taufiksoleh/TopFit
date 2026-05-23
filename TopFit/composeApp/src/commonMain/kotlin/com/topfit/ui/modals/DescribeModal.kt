package com.topfit.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.topfit.data.model.*
import com.topfit.i18n.t
import com.topfit.ui.components.TfCard
import com.topfit.ui.theme.LocalTfColors
import com.topfit.ui.theme.Primary
import com.topfit.util.fmtNum
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun DescribeModal(state: AppState, dispatch: (AppAction) -> Unit) {
    if (!state.describeOpen) return
    val lang = state.lang
    val tfColors = LocalTfColors.current
    val colors = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()

    var text by remember { mutableStateOf("") }
    var parsing by remember { mutableStateOf(false) }
    var items by remember { mutableStateOf<List<DetectedItem>>(emptyList()) }
    var meal by remember { mutableStateOf(state.lastMeal) }

    val examples = if (lang == "id") listOf(
        "nasi goreng setengah porsi",
        "ayam goreng 1 potong dan tempe",
        "2 tusuk sate + es teh manis",
    ) else listOf(
        "half portion fried rice",
        "fried chicken with tempeh",
        "2 satay skewers + iced sweet tea",
    )

    fun scriptedFallback(s: String): List<DetectedItem> {
        val l = s.lowercase()
        val matched = mutableListOf<DetectedItem>()
        for (f in FOODS) {
            val tokens = listOf(f.name.lowercase(), f.nameEn.lowercase(), f.name.lowercase().replace(" ",""))
            if (tokens.any { l.contains(it) }) {
                val qty = when {
                    Regex("setengah|half|1/2").containsMatchIn(l) -> 0.5
                    else -> 1.0
                }
                matched.add(DetectedItem("d_${Random.nextInt(100000)}", f.id, f.name, f.emoji, f.serving, qty, f.kcal*qty, f.p*qty, f.c*qty, f.f*qty, 0.85f))
            }
        }
        return matched.ifEmpty { listOf(DetectedItem("d_${Random.nextInt(100000)}", "nasi-putih", "Nasi Putih", "🍚", "1 porsi", 1.0, 175.0, 4.0, 39.0, 0.0, 0.8f)) }
    }

    fun parseText() {
        scope.launch {
            parsing = true
            val result = withTimeoutOrNull(12_000) {
                try {
                    val client = HttpClient { install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) } }
                    val foodList = FOODS.joinToString("\n") { f ->
                        "${f.id}: ${f.name} (${f.nameEn}), ${f.serving}, ${f.kcal}kcal, P${f.p} C${f.c} F${f.f}"
                    }
                    val prompt = """You are a nutritionist parsing Indonesian food descriptions.
User said: "$text"
Match each item to the database. Use qty (0.25..5) to represent portion size.
Database:
$foodList
Respond ONLY with a JSON array:
[{"foodId":"ayam-goreng","qty":1},{"foodId":"nasi-putih","qty":0.5}]"""

                    val resp = client.post("https://api.anthropic.com/v1/messages") {
                        header("x-api-key", ANTHROPIC_API_KEY)
                        header("anthropic-version", "2023-06-01")
                        contentType(ContentType.Application.Json)
                        setBody(mapOf("model" to "claude-haiku-4-5-20251001", "max_tokens" to 512,
                            "messages" to listOf(mapOf("role" to "user", "content" to prompt))))
                    }
                    val body = resp.body<AnthropicResponse>()
                    val jsonStr = body.content.firstOrNull()?.text ?: ""
                    val m = Regex("""\[[\s\S]*]""").find(jsonStr)?.value
                    if (m != null) {
                        val parsed = Json { ignoreUnknownKeys = true }.decodeFromString<List<FoodParse>>(m)
                        parsed.mapNotNull { p ->
                            val f = FOOD_BY_ID[p.foodId] ?: return@mapNotNull null
                            val qty = p.qty
                            DetectedItem("d_${Random.nextInt(100000)}", f.id, if(lang=="id") f.name else f.nameEn, f.emoji,
                                if(lang=="id") f.serving else f.servingEn, qty, f.kcal*qty, f.p*qty, f.c*qty, f.f*qty, 0.95f)
                        }
                    } else null
                } catch (_: Exception) { null }
            } ?: scriptedFallback(text)
            items = result
            parsing = false
        }
    }

    Dialog(onDismissRequest = { dispatch(AppAction.CloseDescribe) }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(Modifier.fillMaxSize().background(colors.background)) {
            // Header
            Row(
                Modifier.fillMaxWidth().background(colors.surface)
                    .border(width = 0.dp, color = tfColors.hairline)
                    .padding(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(32.dp).clip(RoundedCornerShape(999.dp)).background(tfColors.bgSoft).clickable { dispatch(AppAction.CloseDescribe) }, contentAlignment = Alignment.Center) {
                    Text("✕", fontSize = 14.sp, color = tfColors.ink3)
                }
                Text(t(lang,"describe_title"), Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 14.sp, fontWeight = FontWeight.W600, color = colors.onSurface)
                Spacer(Modifier.width(32.dp))
            }

            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp)) {
                if (items.isEmpty()) {
                    // AI badge
                    Box(Modifier.clip(RoundedCornerShape(999.dp)).background(tfColors.soft).padding(horizontal = 10.dp, vertical = 5.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("✦", fontSize = 12.sp, color = Color(0xFF0A6F4D))
                            Text(t(lang,"describe_powered"), fontSize = 11.sp, fontWeight = FontWeight.W600, color = Color(0xFF0A6F4D))
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(
                        if (lang=="id") "Tulis apa yang kamu makan, AI hitung kalorinya." else "Tell us what you ate, AI counts the calories.",
                        fontSize = 24.sp, fontWeight = FontWeight.W500, color = colors.onBackground, lineHeight = 28.sp,
                    )
                    Spacer(Modifier.height(18.dp))
                    // Textarea
                    Box(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(colors.surface)
                            .border(1.5.dp, if (text.isNotEmpty()) Primary else tfColors.hairline, RoundedCornerShape(18.dp))
                    ) {
                        BasicTextField(
                            value = text, onValueChange = { text = it },
                            enabled = !parsing,
                            modifier = Modifier.fillMaxWidth().padding(16.dp).defaultMinSize(minHeight = 96.dp),
                            textStyle = LocalTextStyle.current.copy(fontSize = 15.sp, color = colors.onSurface, lineHeight = 22.sp),
                            decorationBox = { inner ->
                                if (text.isEmpty()) Text(t(lang,"describe_placeholder"), fontSize = 15.sp, color = tfColors.ink3)
                                inner()
                            },
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(t(lang,"try_example"), fontSize = 11.sp, fontWeight = FontWeight.W600, color = tfColors.ink3, letterSpacing = 0.5.sp)
                    Spacer(Modifier.height(8.dp))
                    examples.forEach { ex ->
                        Box(
                            Modifier.fillMaxWidth().padding(bottom = 6.dp)
                                .clip(RoundedCornerShape(12.dp)).background(colors.surface)
                                .border(0.5.dp, tfColors.hairline, RoundedCornerShape(12.dp))
                                .clickable { text = ex }.padding(10.dp)
                        ) { Text('"' + ex + '"', fontSize = 12.sp, color = tfColors.ink3) }
                    }
                } else {
                    // Results
                    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(tfColors.soft).padding(12.dp)) {
                        Column {
                            Text(t(lang,"you_wrote"), fontSize = 11.sp, fontWeight = FontWeight.W600, color = tfColors.ink3, letterSpacing = 0.5.sp)
                            Spacer(Modifier.height(6.dp))
                            Text('"' + text + '"', fontSize = 14.sp, color = colors.onBackground, lineHeight = 20.sp)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(t(lang,"ai_found"), fontSize = 11.sp, fontWeight = FontWeight.W600, color = tfColors.ink3, letterSpacing = 0.5.sp)
                        Text("${fmtNum(items.sumOf { it.kcal }.toInt())} kkal", fontSize = 13.sp, fontWeight = FontWeight.W600, color = colors.onSurface)
                    }
                    Spacer(Modifier.height(10.dp))
                    TfCard(Modifier.fillMaxWidth()) {
                        items.forEachIndexed { i, item ->
                            Row(
                                Modifier.fillMaxWidth().padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Text(item.emoji, fontSize = 22.sp)
                                Column(Modifier.weight(1f)) {
                                    Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.W600, color = colors.onSurface)
                                    Text("${if(item.qty!=1.0) "${item.qty}× " else ""}${item.serving} · ${fmtNum(item.kcal.toInt())} kkal", fontSize = 11.sp, color = tfColors.ink3)
                                }
                                Row(Modifier.clip(RoundedCornerShape(10.dp)).background(tfColors.bgSoft).padding(3.dp), verticalAlignment = Alignment.CenterVertically) {
                                    QtyBtn("−", item.qty > 0.25) {
                                        val newQty = maxOf(0.25, minOf(5.0, ((item.qty - 0.25) * 4).roundToInt() / 4.0))
                                        val ratio = newQty / item.qty
                                        items = items.map { if (it.uid==item.uid) it.copy(qty=newQty, kcal=it.kcal*ratio, p=it.p*ratio, c=it.c*ratio, f=it.f*ratio) else it }
                                    }
                                    Text(item.qty.toString(), Modifier.width(28.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.W600)
                                    QtyBtn("+") {
                                        val newQty = maxOf(0.25, minOf(5.0, ((item.qty + 0.25) * 4).roundToInt() / 4.0))
                                        val ratio = newQty / item.qty
                                        items = items.map { if (it.uid==item.uid) it.copy(qty=newQty, kcal=it.kcal*ratio, p=it.p*ratio, c=it.c*ratio, f=it.f*ratio) else it }
                                    }
                                }
                                Box(Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(tfColors.bgSoft).clickable { items = items.filter { it.uid != item.uid } }, contentAlignment = Alignment.Center) {
                                    Text("✕", fontSize = 12.sp, color = tfColors.ink3)
                                }
                            }
                            if (i < items.lastIndex) HorizontalDivider(color = tfColors.hairline, thickness = 0.5.dp)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(t(lang,"scan_add_to"), fontSize = 11.sp, fontWeight = FontWeight.W600, color = tfColors.ink3, letterSpacing = 0.5.sp)
                    Spacer(Modifier.height(8.dp))
                    MealSegment(lang, meal, onSelect = { meal = it })
                    Spacer(Modifier.height(80.dp))
                }
            }

            // CTA
            Box(Modifier.fillMaxWidth().background(colors.background).padding(horizontal = 20.dp, vertical = 16.dp)) {
                if (items.isEmpty()) {
                    Button(
                        onClick = { parseText() },
                        enabled = text.isNotBlank() && !parsing,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    ) {
                        if (parsing) CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        else Text("✦ ${t(lang,"describe_analyze")}", fontSize = 15.sp, fontWeight = FontWeight.W600)
                    }
                } else {
                    Button(
                        onClick = {
                            items.forEach { it2 ->
                                dispatch(AppAction.AddItem(FoodItem("i_${Random.nextInt(100000)}", it2.name, it2.emoji, it2.serving, it2.qty, it2.kcal, it2.p, it2.c, it2.f, meal)))
                            }
                            dispatch(AppAction.CloseDescribe)
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    ) { Text("${t(lang,"add")} · ${fmtNum(items.sumOf { it.kcal }.toInt())} kkal", fontSize = 15.sp, fontWeight = FontWeight.W600) }
                }
            }
        }
    }
}

private const val ANTHROPIC_API_KEY = "" // Set via BuildConfig or environment

@Serializable private data class AnthropicResponse(val content: List<ContentBlock>)
@Serializable private data class ContentBlock(val type: String, val text: String = "")
@Serializable private data class FoodParse(val foodId: String, val qty: Double = 1.0)

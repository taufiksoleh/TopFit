package com.topfit.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topfit.data.model.AppState
import com.topfit.i18n.t
import com.topfit.ui.components.TfCard
import com.topfit.ui.theme.LocalTfColors
import com.topfit.ui.theme.Primary
import com.topfit.util.dateString
import com.topfit.util.fmtNum
import com.topfit.util.todayString
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

@Composable
fun ProgressScreen(state: AppState) {
    val lang = state.lang
    val tfColors = LocalTfColors.current
    val colors = MaterialTheme.colorScheme
    val goal = state.profile.dailyGoal

    val tz = TimeZone.currentSystemDefault()
    val today = Clock.System.todayIn(tz)
    val dayNamesId = listOf("Min","Sen","Sel","Rab","Kam","Jum","Sab")
    val dayNamesEn = listOf("Sun","Mon","Tue","Wed","Thu","Fri","Sat")

    val days = (6 downTo 0).map { i ->
        val key = dateString(i)
        val kcal = state.log[key]?.sumOf { it.kcal } ?: 0.0
        val date = today.minus(kotlinx.datetime.DatePeriod(days = i))
        Triple(key, kcal, date)
    }

    val maxKcal = maxOf(goal * 1.1, days.maxOf { it.second }, 1.0)
    val filled = days.filter { it.second > 0 }
    val avg = if (filled.isEmpty()) 0 else (filled.sumOf { it.second } / filled.size).toInt()

    val startWeight = state.profile.weight + 1.8
    val weights = listOf(startWeight, startWeight-0.3, startWeight-0.7, startWeight-0.9, startWeight-1.2, startWeight-1.5, state.profile.weight)
    val wMin = weights.min() - 0.5
    val wMax = weights.max() + 0.5

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 20.dp)
    ) {
        Text(
            t(lang, "progress_title"),
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp),
            fontSize = 28.sp, fontWeight = FontWeight.W500, color = colors.onBackground,
        )

        // Stats row
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TfCard(Modifier.weight(1f)) {
                Column(Modifier.padding(14.dp)) {
                    Text(t(lang,"avg_daily"), fontSize = 10.sp, fontWeight = FontWeight.W600, color = tfColors.ink3, letterSpacing = 0.5.sp)
                    Text(fmtNum(avg), fontSize = 28.sp, fontWeight = FontWeight.W500, color = colors.onSurface)
                    Text("kkal · 7 ${t(lang,"days")}", fontSize = 11.sp, color = tfColors.ink3)
                }
            }
            TfCard(Modifier.weight(1f)) {
                Column(Modifier.padding(14.dp)) {
                    Text(t(lang,"weight_trend"), fontSize = 10.sp, fontWeight = FontWeight.W600, color = tfColors.ink3, letterSpacing = 0.5.sp)
                    Text("−1.8 kg", fontSize = 28.sp, fontWeight = FontWeight.W500, color = colors.onSurface)
                    Text(t(lang,"on_track"), fontSize = 11.sp, color = Primary, fontWeight = FontWeight.W600)
                }
            }
        }

        // Bar chart
        TfCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Column(Modifier.padding(horizontal = 14.dp, vertical = 16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(t(lang,"this_week"), fontSize = 13.sp, fontWeight = FontWeight.W600, color = colors.onSurface)
                    Text("${if(lang=="id") "Target" else "Goal"} ${fmtNum(goal)} kkal", fontSize = 11.sp, color = tfColors.ink3)
                }
                Spacer(Modifier.height(14.dp))
                Canvas(Modifier.fillMaxWidth().height(140.dp)) {
                    val barW = (size.width - 7 * 8.dp.toPx()) / 7
                    val goalY = (1f - goal / maxKcal).toFloat() * size.height

                    // Goal dashed line
                    drawLine(color = Color(0xFF92A39B).copy(alpha = 0.5f), start = Offset(0f, goalY),
                        end = Offset(size.width, goalY), strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f)))

                    days.forEachIndexed { i, (_, kcal, _) ->
                        val x = i * (barW + 8.dp.toPx())
                        val h = (kcal / maxKcal * size.height).toFloat().coerceAtLeast(4.dp.toPx())
                        val barColor = when {
                            i == days.lastIndex -> Primary
                            kcal > goal -> Color(0xFFE0A838)
                            else -> Color(0xFFB6E0CD)
                        }
                        drawRoundRect(
                            color = barColor.copy(alpha = if (kcal > 0) 1f else 0.25f),
                            topLeft = Offset(x, size.height - h),
                            size = Size(barW, h),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx()),
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    days.forEachIndexed { i, (_, _, date) ->
                        val name = if (lang=="id") dayNamesId[date.dayOfWeek.ordinal % 7] else dayNamesEn[date.dayOfWeek.ordinal % 7]
                        Text(name, modifier = Modifier.weight(1f),
                            fontSize = 10.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = if (i == days.lastIndex) Primary else tfColors.ink3,
                            fontWeight = if (i == days.lastIndex) FontWeight.W600 else FontWeight.W500)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Weight trend line chart
        TfCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Column(Modifier.padding(horizontal = 14.dp, vertical = 16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(t(lang,"weight_trend"), fontSize = 13.sp, fontWeight = FontWeight.W600, color = colors.onSurface)
                    Text("${state.profile.weight} → ${state.profile.targetWeight} kg", fontSize = 11.sp, color = tfColors.ink3)
                }
                Spacer(Modifier.height(14.dp))
                Canvas(Modifier.fillMaxWidth().height(100.dp)) {
                    val pts = weights.mapIndexed { i, w ->
                        Offset(
                            x = i.toFloat() / (weights.size - 1) * size.width,
                            y = ((wMax - w) / (wMax - wMin) * size.height).toFloat(),
                        )
                    }
                    val linePath = Path().apply {
                        pts.forEachIndexed { i, pt -> if (i == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y) }
                    }
                    val areaPath = Path().apply {
                        addPath(linePath)
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                        close()
                    }
                    drawPath(areaPath, brush = Brush.verticalGradient(
                        listOf(Primary.copy(alpha = 0.22f), Primary.copy(alpha = 0f))))
                    drawPath(linePath, color = Primary, style = Stroke(2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                    pts.forEachIndexed { i, pt ->
                        val isLast = i == pts.lastIndex
                        drawCircle(color = if (isLast) Primary else Color.White, radius = if (isLast) 4.dp.toPx() else 2.5.dp.toPx(), center = pt)
                        drawCircle(color = Primary, radius = if (isLast) 4.dp.toPx() else 2.5.dp.toPx(), center = pt, style = Stroke(2.dp.toPx()))
                    }
                }
            }
        }
    }
}

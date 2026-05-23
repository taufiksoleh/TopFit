package com.topfit.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topfit.ui.theme.LocalTfColors
import com.topfit.ui.theme.Primary
import com.topfit.util.fmtNum

@Composable
fun CalorieRing(
    consumed: Double,
    goal: Int,
    modifier: Modifier = Modifier,
    size: Dp = 196.dp,
) {
    val tfColors = LocalTfColors.current
    val pct by animateFloatAsState(
        targetValue = (consumed / goal).coerceIn(0.0, 1.0).toFloat(),
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "calorieRing",
    )
    val remaining = (goal - consumed).coerceAtLeast(0.0)

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 9.dp.toPx()
            val inset = strokeWidth / 2
            val arcSize = Size(this.size.width - strokeWidth, this.size.height - strokeWidth)
            val topLeft = Offset(inset, inset)

            // Track
            drawArc(
                color = tfColors.hairline,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(strokeWidth, cap = StrokeCap.Round),
            )
            // Progress
            if (pct > 0f) {
                drawArc(
                    color = Primary,
                    startAngle = -90f,
                    sweepAngle = 360f * pct,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(strokeWidth, cap = StrokeCap.Round),
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = fmtNum(remaining),
                fontSize = 52.sp,
                fontWeight = FontWeight.W400,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 48.sp,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "kkal tersisa",
                fontSize = 10.sp,
                fontWeight = FontWeight.W600,
                color = tfColors.ink3,
                letterSpacing = 0.8.sp,
            )
        }
    }
}

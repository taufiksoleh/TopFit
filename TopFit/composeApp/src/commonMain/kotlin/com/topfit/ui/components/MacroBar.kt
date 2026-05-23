package com.topfit.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topfit.ui.theme.LocalTfColors

@Composable
fun MacroBar(
    label: String,
    value: Double,
    goal: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val tfColors = LocalTfColors.current
    val pct by animateFloatAsState(
        targetValue = (value / goal).coerceIn(0.0, 1.0).toFloat(),
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "macroBar_$label",
    )

    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.W600,
            color = tfColors.ink3,
            letterSpacing = 0.4.sp,
        )
        Spacer(Modifier.height(4.dp))
        // Track
        Box(
            Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(tfColors.hairline)
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(pct)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.W600, color = Color.Unspecified)) {
                    append("${value.toInt()}")
                }
                withStyle(SpanStyle(color = tfColors.ink3)) {
                    append(" / ${goal}g")
                }
            },
            fontSize = 11.sp,
        )
    }
}

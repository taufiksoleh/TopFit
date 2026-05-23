package com.topfit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topfit.data.model.FoodItem
import com.topfit.ui.theme.LocalTfColors
import com.topfit.util.fmtNum

@Composable
fun FoodRow(
    item: FoodItem,
    onDelete: () -> Unit,
    isLast: Boolean,
    modifier: Modifier = Modifier,
) {
    val tfColors = LocalTfColors.current
    val colors = MaterialTheme.colorScheme

    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(item.emoji, fontSize = 20.sp, modifier = Modifier.width(28.dp))
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    item.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500,
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    buildString {
                        if (item.qty != 1.0) append("${item.qty}× ")
                        append(item.serving)
                    },
                    fontSize = 11.sp,
                    color = tfColors.ink3,
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End, modifier = Modifier.width(56.dp)) {
                Text(fmtNum(item.kcal), fontSize = 14.sp, fontWeight = FontWeight.W600, color = colors.onSurface)
                Text("kkal", fontSize = 10.sp, color = tfColors.ink3)
            }
            Spacer(Modifier.width(4.dp))
            Box(
                Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(tfColors.bgSoft)
                    .clickable(onClick = onDelete),
                contentAlignment = Alignment.Center,
            ) {
                Text("✕", fontSize = 12.sp, color = tfColors.ink3)
            }
        }
        if (!isLast) {
            HorizontalDivider(color = tfColors.hairline, thickness = 0.5.dp)
        }
    }
}

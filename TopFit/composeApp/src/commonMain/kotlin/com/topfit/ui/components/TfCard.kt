package com.topfit.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.topfit.ui.theme.LocalTfColors

@Composable
fun TfCard(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit,
) {
    val tfColors = LocalTfColors.current
    Surface(
        modifier = modifier
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(18.dp), spotColor = Color(0x050F1E16))
            .border(0.5.dp, tfColors.hairline, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp)),
        color = color,
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(content = content)
    }
}

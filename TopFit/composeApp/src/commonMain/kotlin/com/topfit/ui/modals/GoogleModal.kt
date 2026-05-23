package com.topfit.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.topfit.data.model.AppAction
import com.topfit.data.model.AppState
import com.topfit.i18n.t
import com.topfit.ui.theme.LocalTfColors
import com.topfit.ui.theme.Primary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class GooglePhase { INTRO, CONNECTING, DONE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleModal(state: AppState, dispatch: (AppAction) -> Unit) {
    if (!state.googleOpen) return
    val lang = state.lang
    val tfColors = LocalTfColors.current
    val colors = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    var phase by remember { mutableStateOf(GooglePhase.INTRO) }

    ModalBottomSheet(
        onDismissRequest = { if (phase != GooglePhase.CONNECTING) dispatch(AppAction.CloseGoogle) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.surface,
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp).padding(bottom = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            when (phase) {
                GooglePhase.INTRO -> {
                    Box(Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)).background(Color.White).border(0.5.dp, tfColors.hairline, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                        Text("G", fontSize = 28.sp, fontWeight = FontWeight.W700, color = Color(0xFF4285F4))
                    }
                    Spacer(Modifier.height(20.dp))
                    Text(t(lang,"gconn_title"), fontSize = 24.sp, fontWeight = FontWeight.W500, color = colors.onSurface, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(6.dp))
                    Text(t(lang,"gconn_sub"), fontSize = 14.sp, color = tfColors.ink3, textAlign = TextAlign.Center, lineHeight = 20.sp, modifier = Modifier.padding(horizontal = 8.dp))
                    Spacer(Modifier.height(20.dp))
                    listOf(t(lang,"gconn_b1"), t(lang,"gconn_b2"), t(lang,"gconn_b3")).forEach { benefit ->
                        Row(Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(22.dp).clip(CircleShape).background(Primary), contentAlignment = Alignment.Center) {
                                Text("✓", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.W700)
                            }
                            Text(benefit, fontSize = 13.sp, color = colors.onSurface)
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = {
                            phase = GooglePhase.CONNECTING
                            scope.launch {
                                delay(1300)
                                dispatch(AppAction.ConnectGoogle(state.profile.name.ifBlank { "Andi" }, "andi.pratama@gmail.com"))
                                phase = GooglePhase.DONE
                                delay(1100)
                                dispatch(AppAction.CloseGoogle)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF1F1F1F)),
                        border = ButtonDefaults.outlinedButtonBorder,
                    ) {
                        Text("G  ", fontSize = 16.sp, fontWeight = FontWeight.W700, color = Color(0xFF4285F4))
                        Text(t(lang,"gconn_cta"), fontSize = 14.sp, fontWeight = FontWeight.W600)
                    }
                    Spacer(Modifier.height(4.dp))
                    TextButton(onClick = { dispatch(AppAction.CloseGoogle) }, modifier = Modifier.fillMaxWidth()) {
                        Text(t(lang,"gconn_skip"), fontSize = 13.sp, fontWeight = FontWeight.W600, color = tfColors.ink3)
                    }
                }
                GooglePhase.CONNECTING -> {
                    Spacer(Modifier.height(40.dp))
                    CircularProgressIndicator(modifier = Modifier.size(36.dp), color = Primary, strokeWidth = 3.dp)
                    Spacer(Modifier.height(16.dp))
                    Text(t(lang,"connecting"), fontSize = 14.sp, color = tfColors.ink3)
                    Spacer(Modifier.height(40.dp))
                }
                GooglePhase.DONE -> {
                    Spacer(Modifier.height(32.dp))
                    Box(Modifier.size(56.dp).clip(CircleShape).background(Primary), contentAlignment = Alignment.Center) {
                        Text("✓", fontSize = 26.sp, color = Color.White, fontWeight = FontWeight.W700)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(t(lang,"connected"), fontSize = 16.sp, fontWeight = FontWeight.W600, color = colors.onSurface)
                    Text("andi.pratama@gmail.com", fontSize = 12.sp, color = tfColors.ink3, modifier = Modifier.padding(top = 4.dp))
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

package com.topfit

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.topfit.viewmodel.AppViewModel

fun main() {
    val viewModel = AppViewModel()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "TopFit",
            state = rememberWindowState(width = 400.dp, height = 820.dp),
        ) {
            App(viewModel)
        }
    }
}

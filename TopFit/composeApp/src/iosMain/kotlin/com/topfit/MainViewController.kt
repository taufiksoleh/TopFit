package com.topfit

import androidx.compose.ui.window.ComposeUIViewController
import com.topfit.viewmodel.AppViewModel

fun MainViewController() = ComposeUIViewController {
    App(AppViewModel())
}

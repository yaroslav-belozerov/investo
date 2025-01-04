package org.yaabelozerov.investo

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.context.startKoin
import org.yaabelozerov.investo.domain.MainViewModel

fun main() = application {
    startKoin { modules(appModule) }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Investo",
        state = rememberWindowState(size = DpSize(1200.dp, 600.dp))
    ) {
        App()
    }
}
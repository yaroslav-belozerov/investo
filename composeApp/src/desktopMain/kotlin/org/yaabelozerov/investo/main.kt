package org.yaabelozerov.investo

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.context.startKoin
import org.yaabelozerov.investo.domain.MainViewModel
import investo.composeapp.generated.resources.Res
import org.yaabelozerov.investo.config.BuildConfig

fun main() = application {
    startKoin { modules(appModule) }
    if (BuildConfig.DEBUG_BUILD) Napier.base(DebugAntilog())
    Window(
        onCloseRequest = ::exitApplication,
        title = "Investo",
        state = rememberWindowState(size = DpSize(1200.dp, 600.dp))
    ) {
        App()
    }
}
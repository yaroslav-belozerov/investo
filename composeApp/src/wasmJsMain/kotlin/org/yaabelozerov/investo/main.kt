package org.yaabelozerov.investo

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.browser.document
import org.koin.core.context.startKoin
import org.yaabelozerov.investo.config.BuildConfig

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin { modules(appModule) }
    if (BuildConfig.DEBUG_BUILD) Napier.base(DebugAntilog())
    ComposeViewport(document.body!!) {
        App()
    }
}
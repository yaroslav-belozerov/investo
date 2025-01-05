package org.yaabelozerov.investo

import androidx.compose.ui.window.ComposeUIViewController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.yaabelozerov.investo.config.BuildConfig

fun MainViewController() = ComposeUIViewController {
    if (BuildConfig.DEBUG_BUILD) Napier.base(DebugAntilog())
    startKoin { modules(appModule) }
    val isDarkTheme =
        UIScreen.mainScreen.traitCollection.userInterfaceStyle ==
                UIUserInterfaceStyle.UIUserInterfaceStyleDark
    App()
}
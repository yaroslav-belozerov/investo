package org.yaabelozerov.investo

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cafe.adriel.lyricist.ProvideStrings
import cafe.adriel.lyricist.rememberStrings
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.log
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.yaabelozerov.investo.domain.MainViewModel
import org.yaabelozerov.investo.ui.Nav
import org.yaabelozerov.investo.ui.main.MainPage
import org.yaabelozerov.investo.ui.settings.SettingsPage
import org.yaabelozerov.investo.ui.theme.Typography
import org.yaabelozerov.investo.ui.theme.darkScheme
import org.yaabelozerov.investo.ui.theme.lightScheme

@Composable
@Preview
fun App() {
    val mvm = koinViewModel<MainViewModel>()
    val fr = remember { FocusRequester() }
    val lazyListState = rememberLazyListState()
    val navCtrl = rememberNavController()
    var currentDestination by rememberSaveable { mutableStateOf(Nav.MAIN) }
    navCtrl.addOnDestinationChangedListener { _, dest, _ ->
        currentDestination =
            Nav.entries.find { dest.route == it.route } ?: return@addOnDestinationChangedListener
    }
    val scope = rememberCoroutineScope()
    val lyricist = rememberStrings(currentLanguageTag = mvm.languageTag)
    ProvideStrings(lyricist = lyricist) {
        MaterialTheme(
            colorScheme = if (isSystemInDarkTheme()) darkScheme else lightScheme,
            typography = Typography
        ) {
            val addPadding = isLayoutWide()
            NavigationSuiteScaffold(layoutType = if (isLayoutWide()) NavigationSuiteType.NavigationRail else NavigationSuiteType.NavigationBar,
                navigationSuiteItems = {
                    Nav.entries.forEach {
                        item(modifier = Modifier.then(
                            if (addPadding) Modifier.padding(
                                start = 8.dp, top = 16.dp
                            ) else Modifier
                        ), selected = it == currentDestination, icon = {
                            Icon(
                                if (it == currentDestination) it.iconFilled else it.iconOutline,
                                contentDescription = null
                            )
                        }, onClick = {
                            if (it == currentDestination && it == Nav.MAIN) {
                                scope.launch {
                                    lazyListState.animateScrollToItem(0)
                                    try {
                                        fr.requestFocus()
                                    } catch (t: Throwable) {
                                        log(
                                            priority = LogLevel.INFO,
                                            throwable = t,
                                        ) { "Focus request failed on main screen" }
                                    }
                                }
                            } else {
                                navCtrl.navigate(it.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(
                                        navCtrl.graph.startDestinationRoute ?: return@navigate
                                    ) {
                                        saveState = true
                                    }
                                }
                            }
                        })
                    }
                },
                content = {
                    NavHost(navCtrl, startDestination = Nav.MAIN.route) {
                        composable(Nav.MAIN.route) {
                            MainPage(
                                mvm, fr, lazyListState, modifier = Modifier.safeDrawingPadding()
                            )
                        }
                        composable(Nav.SETTINGS.route) {
                            SettingsPage(onSetLanguage = { lyricist.languageTag = it }, modifier = Modifier.safeDrawingPadding())
                        }
                    }
                })
        }
    }
}
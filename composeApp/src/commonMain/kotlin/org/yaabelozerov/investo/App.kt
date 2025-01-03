package org.yaabelozerov.investo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowWidthSizeClass
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.yaabelozerov.investo.domain.MainViewModel
import org.yaabelozerov.investo.ui.Nav
import org.yaabelozerov.investo.ui.main.MainPage
import org.yaabelozerov.investo.ui.settings.SettingsPage

@Composable
@Preview
fun App(
    darkTheme: Boolean, dynamicColor: Boolean
) {
    val mvm = koinViewModel<MainViewModel>()
    val fr = remember { FocusRequester() }
    val lazyListState = rememberLazyListState()
    val nc = rememberNavController()
    val scope = rememberCoroutineScope()
    AppTheme(darkTheme, dynamicColor) {
        var currentDestination by rememberSaveable { mutableStateOf(Nav.MAIN) }
        val extendedLayout = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavigationSuiteScaffold(modifier = Modifier.padding(if (extendedLayout) 8.dp else 0.dp), navigationSuiteItems = {
                Nav.entries.forEach {
                    item(modifier = Modifier.then(
                        if (extendedLayout) Modifier.padding(start = 8.dp, top = 16.dp) else Modifier
                    ), selected = it == currentDestination, icon = {
                        Icon(
                            if (it == currentDestination) it.iconFilled else it.iconOutline,
                            contentDescription = null
                        )
                    }, onClick = {
                        if (it == currentDestination && it == Nav.MAIN) {
                            try {
                                fr.requestFocus()
                            } catch (_: Exception) {
                            }
                            scope.launch {
                                lazyListState.animateScrollToItem(0)
                                try {
                                    fr.requestFocus()
                                } catch (_: Exception) {
                                }
                            }
                        } else {
                            nc.popBackStack(
                                it.route, inclusive = true, saveState = true
                            )
                            nc.navigate(it.route)
                            currentDestination = it
                        }
                    })
                }
            }, content = {
                NavHost(nc, startDestination = Nav.MAIN.route) {
                    composable(Nav.MAIN.route) {
                        MainPage(
                            mvm, fr, lazyListState, extendedLayout, modifier = Modifier.padding(innerPadding)
                        )
                    }
                    composable(Nav.SETTINGS.route) {
                        SettingsPage(
                            mvm, modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            })
        }
    }
}
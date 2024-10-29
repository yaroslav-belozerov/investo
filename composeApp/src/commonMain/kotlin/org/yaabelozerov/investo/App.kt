package org.yaabelozerov.investo

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.yaabelozerov.investo.domain.MainViewModel
import org.yaabelozerov.investo.network.ApiBaseUrl
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
        LaunchedEffect(true) {
            mvm.fetchCurrencies()
        }
        Scaffold(bottomBar = {
            BottomAppBar {
                Nav.entries.forEach {
                    val isSelected =
                        nc.currentBackStackEntryAsState().value?.destination?.route == it.route
                    NavigationBarItem(selected = isSelected, icon = {
                        Icon(
                            if (isSelected) it.iconFilled else it.iconOutline,
                            contentDescription = null
                        )
                    }, onClick = {
                        if (isSelected && it == Nav.MAIN) {
                            try {
                                fr.requestFocus()
                            } catch (_: Exception) {}
                            scope.launch {
                                lazyListState.animateScrollToItem(0)
                                try {fr.requestFocus()} catch (_: Exception) {}
                            }
                        } else {
                            nc.popBackStack(
                                it.route, inclusive = true, saveState = true
                            )
                            nc.navigate(it.route)
                        }
                    })
                }
            }
        }) { innerPadding ->
            NavHost(nc, startDestination = Nav.MAIN.route) {
                composable(Nav.MAIN.route) {
                    MainPage(mvm, fr, lazyListState, modifier = Modifier.padding(innerPadding))
                }
                composable(Nav.SETTINGS.route) {
                    SettingsPage(mvm, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
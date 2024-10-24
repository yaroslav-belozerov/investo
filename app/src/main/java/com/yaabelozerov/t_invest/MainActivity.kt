package com.yaabelozerov.t_invest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yaabelozerov.t_invest.di.appModule
import com.yaabelozerov.t_invest.domain.MainViewModel
import com.yaabelozerov.t_invest.ui.CurrencyRow
import com.yaabelozerov.t_invest.ui.MainPage
import com.yaabelozerov.t_invest.ui.Nav
import com.yaabelozerov.t_invest.ui.SettingsPage
import com.yaabelozerov.t_invest.ui.ShareCard
import com.yaabelozerov.t_invest.ui.ShareSearchBar
import com.yaabelozerov.t_invest.ui.theme.TInvestTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        startKoin {
            androidContext(applicationContext)
            modules(appModule)
        }
        val mvm = getViewModel<MainViewModel>()

        setContent {
            val navController = rememberNavController()
            val fr = remember { FocusRequester() }
            var sharesQuery by remember { mutableStateOf("") }
            val mainScreenLazyListState = rememberLazyListState()
            val scope = rememberCoroutineScope()

            TInvestTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
                    NavigationBar {
                        Nav.entries.forEach {
                            val isSelected =
                                navController.currentBackStackEntryAsState().value?.destination?.route == it.route
                            NavigationBarItem(isSelected, icon = {
                                Icon(
                                    modifier = Modifier.size(28.dp),
                                    imageVector = if (isSelected) it.iconFilled else it.iconOutline,
                                    contentDescription = it.route
                                )
                            }, onClick = {
                                if (isSelected && it == Nav.MAIN) {
                                    try {
                                        fr.requestFocus()
                                    } catch (_: Exception) {
                                        scope.launch {
                                            mainScreenLazyListState.animateScrollToItem(0)
                                        }
                                    }
                                } else {
                                    navController.popBackStack(
                                        it.route, inclusive = true, saveState = true
                                    )
                                    navController.navigate(it.route)
                                }
                            })
                        }
                    }
                }) { innerPadding ->
                    NavHost(navController, startDestination = Nav.MAIN.route) {
                        composable(Nav.MAIN.route) {
                            MainPage(
                                mvm = mvm,
                                fr = fr,
                                sharesQuery = Pair(sharesQuery) { sharesQuery = it },
                                lazyListState = mainScreenLazyListState,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable(Nav.SETTINGS.route) {
                            SettingsPage(mvm = mvm, modifier = Modifier.padding(innerPadding))
                        }
                    }
                }
            }
        }
    }
}
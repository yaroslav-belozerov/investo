package org.yaabelozerov.investo.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.yaabelozerov.investo.domain.MainViewModel

@Composable
fun MainPage(
    mvm: MainViewModel,
    fr: FocusRequester,
    lazyListState: LazyListState,
    extendedLayout: Boolean = false,
    modifier: Modifier = Modifier
) {
    var sharesQuery by remember { mutableStateOf("") }
    val state by mvm.state.collectAsStateWithLifecycle()
    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AnimatedContent(state.loadingCurrencies) {
                if (it) {
                    LinearProgressIndicator(Modifier.fillMaxWidth().padding(horizontal = 8.dp))
                } else if (state.currencies.isNotEmpty()) {
                    CurrencyRow(state.currencies, extendedLayout)
                }
            }
        }
        item {
            ShareSearchBar(
                query = sharesQuery,
                onUpdateQuery = { sharesQuery = it },
                isLoading = state.loadingShares,
                onSearch = {
                    mvm.searchShares(
                        sharesQuery.trim()
                    )
                },
                focusRequester = fr
            )
        }
        items(state.shares, key = { it.figi }) { share -> ShareCard(share) }
    }
}
package org.yaabelozerov.investo.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.unit.dp
import investo.composeapp.generated.resources.Res
import investo.composeapp.generated.resources.allStringResources
import org.jetbrains.compose.resources.stringResource
import org.yaabelozerov.investo.domain.MainViewModel
import org.yaabelozerov.investo.network.ApiBaseUrl

@Composable
fun MainPage(
    mvm: MainViewModel,
    fr: FocusRequester,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    var sharesQuery by remember { mutableStateOf("") }
    val shares = mvm.shares.collectAsState().value
    val sharesLoading = mvm.loading.collectAsState().value
    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = 0) {
            CurrencyRow(mvm.currencies.collectAsState().value)
        }
        item(key = 1) {
            ShareSearchBar(
                query = sharesQuery,
                onUpdateQuery = { sharesQuery = it },
                isLoading = sharesLoading,
                onSearch = {
                    mvm.searchShares(
                        sharesQuery.trim()
                    )
                },
                focusRequester = fr
            )
        }
        items(shares, key = { it.figi }) { share ->
            ShareCard(share)
        }
    }
}
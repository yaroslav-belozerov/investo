package org.yaabelozerov.investo.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.lyricist.LocalStrings
import kotlinx.coroutines.launch
import org.yaabelozerov.investo.domain.MainViewModel

@Composable
fun MainPage(
    mvm: MainViewModel,
    fr: FocusRequester,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    val state by mvm.state.collectAsStateWithLifecycle()
    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            state = lazyListState,
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AnimatedContent(state.loadingCurrencies) {
                    if (it) {
                        LinearProgressIndicator(
                            Modifier.fillMaxWidth().padding(horizontal = 8.dp).padding(top = 8.dp)
                        )
                    } else if (state.currencies.isNotEmpty() && state.currencyError == null) {
                        CurrencyRow(state.currencies)
                    } else {
                        state.currencyError?.let {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Text(LocalStrings.current.networkError(it), color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
            item {
                ShareSearchBar(
                    query = state.searchQuery,
                    onUpdateQuery = mvm::setQuery,
                    error = state.shareError,
                    isLoading = state.loadingShares,
                    onSearch = mvm::searchShares,
                    focusRequester = fr
                )
            }
            items(state.shares, key = { it.figi }) { share ->
                ShareCard(
                    share, modifier = Modifier.animateItem()
                )
            }
        }
        val scope = rememberCoroutineScope()
        val showButton by derivedStateOf { lazyListState.firstVisibleItemIndex > 3 }
        AnimatedVisibility(
            showButton,
            modifier = Modifier.align(Alignment.BottomEnd).offset((-16).dp, (-16).dp),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Card(onClick = {
                scope.launch {
                    lazyListState.animateScrollToItem(0)
                }
            }) {
                Icon(
                    Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(8.dp).size(32.dp)
                )
            }
        }
    }
}
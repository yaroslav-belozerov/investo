package com.yaabelozerov.t_invest.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yaabelozerov.t_invest.domain.MainViewModel
import com.yaabelozerov.t_invest.domain.model.CurrencyModel
import com.yaabelozerov.t_invest.domain.model.ShareModel
import com.yaabelozerov.t_invest.util.horizontalFadingEdge
import com.yaabelozerov.t_invest.util.shimmerBackground
import com.yaabelozerov.t_invest.util.toCountryEmoji
import org.koin.androidx.compose.koinViewModel
import kotlin.math.max

@Composable
fun MainPage(
    mvm: MainViewModel,
    fr: FocusRequester,
    sharesQuery: Pair<String, (String) -> Unit>,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
) {
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
                query = sharesQuery.first,
                onUpdateQuery = { sharesQuery.second(it) },
                isLoading = sharesLoading,
                onSearch = { mvm.searchShares(sharesQuery.first.trim()) },
                focusRequester = fr
            )
        }
        items(shares, key = { it.figi }) { share ->
            ShareCard(share)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShareCard(share: ShareModel) {
    AnimatedVisibility(share.isLoaded, enter = slideInVertically() + fadeIn()) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .clip(MaterialTheme.shapes.medium)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        modifier = Modifier.animateContentSize(),
                        text = share.name,
                        fontSize = 20.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                    FlowRow(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        if (share.country.isNotBlank()) {
                            OutlinedCard(shape = MaterialTheme.shapes.small) {
                                Text(
                                    modifier = Modifier.padding(8.dp, 4.dp),
                                    text = share.country.toCountryEmoji()
                                )
                            }
                        }
                        if (share.canShort) OutlinedCard(shape = MaterialTheme.shapes.small) {
                            Text(modifier = Modifier.padding(8.dp, 4.dp), text = "Short")
                        }
                        if (share.apiTrade) OutlinedCard(shape = MaterialTheme.shapes.small) {
                            Text(modifier = Modifier.padding(8.dp, 4.dp), text = "API")
                        }
                        if (share.canBuy) OutlinedCard(
                            shape = MaterialTheme.shapes.small,
                            colors = CardDefaults.outlinedCardColors()
                                .copy(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(
                                modifier = Modifier.padding(8.dp, 4.dp),
                                text = "Buy",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        if (share.canSell) OutlinedCard(
                            shape = MaterialTheme.shapes.small,
                            colors = CardDefaults.outlinedCardColors()
                                .copy(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Text(
                                modifier = Modifier.padding(8.dp, 4.dp),
                                text = "Sell",
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    text = share.price,
                    softWrap = false,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun CurrencyRow(items: List<CurrencyModel>) {
    val scrollState = rememberScrollState()
    var current by remember {
        mutableStateOf(
            CurrencyModel(
                "???", "", "???????", false, "", ""
            )
        )
    }
    var isOpen by remember { mutableStateOf(false) }
    Column(modifier = Modifier.animateContentSize()) {
        Row(
            modifier = Modifier
                .horizontalFadingEdge(scrollState, 32.dp)
                .horizontalScroll(scrollState), horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            (items + (1..max(0, 5 - items.size)).map {
                CurrencyModel(
                    "???", "", "???????", false, "", ""
                )
            }).forEach {
                CurrencyChip(model = it, isChosen = current == it) {
                    if (current == it) {
                        isOpen = false
                        current = CurrencyModel(
                            "???", "", "???????", false, "", ""
                        )
                    } else {
                        current = it
                        isOpen = true
                    }
                }
            }
        }
        AnimatedVisibility(
            isOpen, enter = fadeIn() + slideInVertically() + expandVertically(
                animationSpec = spring(stiffness = Spring.StiffnessHigh)
            ), exit = fadeOut() + slideOutVertically() + shrinkVertically(
                animationSpec = spring(stiffness = Spring.StiffnessHigh)
            )
        ) {
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 16.dp, 0.dp, 0.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary)
                    )
                )
            ) {
                Text(
                    modifier = Modifier
                        .padding(0.dp, 24.dp, 0.dp, 0.dp)
                        .fillMaxWidth(),
                    text = current.name,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
                Row(
                    modifier = Modifier
                        .padding(16.dp, 16.dp, 16.dp, 24.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(current.maxPrice, color = MaterialTheme.colorScheme.error)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Text(current.minPrice, color = MaterialTheme.colorScheme.tertiary)
                    }
                }
            }
        }
    }
}

@Composable
fun CurrencyChip(model: CurrencyModel, isChosen: Boolean, onOpen: () -> Unit) {
    var isVisible by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(null) { isVisible = true }
    AnimatedVisibility(
        visible = isVisible, enter = fadeIn() + slideInVertically() + expandVertically()
    ) {
        Crossfade(model.isLoaded) { loaded ->
            Box(modifier = Modifier
                .then(if (!loaded) Modifier.shimmerBackground(
                    MaterialTheme.shapes.medium
                )
                else Modifier
                    .background(CardDefaults.outlinedCardColors().containerColor)
                    .clickable { onOpen() })
                .border(
                    1.dp, if (loaded) {
                        if (!isChosen) OutlinedTextFieldDefaults.colors().unfocusedIndicatorColor
                        else MaterialTheme.colorScheme.primary
                    } else Color.Transparent, MaterialTheme.shapes.medium
                )
                .clip(MaterialTheme.shapes.medium)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        model.isoCode,
                        color = if (loaded) LocalTextStyle.current.color else Color.Transparent
                    )
                    Text(
                        model.price,
                        fontFamily = FontFamily.Monospace,
                        color = if (loaded) MaterialTheme.colorScheme.primary else Color.Transparent
                    )
                }
            }
        }
    }
}

@Composable
fun ShareSearchBar(
    query: String,
    onUpdateQuery: (String) -> Unit,
    isLoading: Boolean,
    onSearch: () -> Unit,
    focusRequester: FocusRequester
) {
    val fm = LocalFocusManager.current
    OutlinedTextField(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = query,
        onValueChange = {
            onUpdateQuery(it)
        },
        singleLine = true,
        trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!isLoading) IconButton(onClick = {
                    if (query.trim().length > 2) {
                        onSearch()
                        fm.clearFocus()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Search, contentDescription = null
                    )
                } else CircularProgressIndicator(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        },
        keyboardActions = KeyboardActions(onSearch = {
            if (query.trim().length > 2) {
                onSearch()
                fm.clearFocus()
            }
        }),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        placeholder = {
            Text(
                "Shares", fontSize = 20.sp
            )
        },
        textStyle = LocalTextStyle.current.copy(fontSize = 20.sp)
    )
}
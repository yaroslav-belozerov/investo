package org.yaabelozerov.investo.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.yaabelozerov.investo.ui.main.model.ShareModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalLayoutApi::class)
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (share.country.isNotBlank()) {
                            OutlinedCard(shape = MaterialTheme.shapes.small) {
                                Text(
                                    modifier = Modifier.padding(8.dp, 4.dp),
                                    text = share.country
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

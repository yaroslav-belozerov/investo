package org.yaabelozerov.investo.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.yaabelozerov.investo.horizontalFadingEdge
import org.yaabelozerov.investo.ui.main.model.CurrencyModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CurrencyRow(items: List<CurrencyModel>, extendedLayout: Boolean) {
    val scrollState = rememberScrollState()
    var current by remember {
        mutableStateOf(items.firstOrNull())
    }
    var isOpen by remember { mutableStateOf(false) }
    Column {
        val content: @Composable () -> Unit = {
            items.map {
                CurrencyChip(
                    model = it, isChosen = current == it && isOpen
                ) {
                    if (current == it && isOpen) {
                        isOpen = false
                    } else {
                        current = it
                        isOpen = true
                    }
                }
            }
        }
        if (extendedLayout) {
            FlowRow(modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = { content() })
        } else {
            Row(modifier = Modifier.horizontalFadingEdge(
                scrollState, 16.dp
            ).horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                content = { content() })
        }

        AnimatedVisibility(
            isOpen,
            enter = fadeIn() + slideInVertically() + expandVertically(),
            exit = fadeOut() + slideOutVertically() + shrinkVertically()
        ) {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth().padding(0.dp, 16.dp, 0.dp, 0.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary
                        )
                    )
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    modifier = Modifier.padding(0.dp, 24.dp, 0.dp, 0.dp).fillMaxWidth(),
                    text = current?.name ?: "",
                    textAlign = TextAlign.Center,
                )
                Row(
                    modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 24.dp).fillMaxWidth(),
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
                        Text(current?.maxPrice ?: "", color = MaterialTheme.colorScheme.error)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(current?.minPrice ?: "", color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}

@Composable
fun CurrencyChip(
    model: CurrencyModel, isChosen: Boolean, onOpen: () -> Unit
) {
    val corners by animateDpAsState(if (isChosen) 20.dp else 12.dp)
    val color by animateColorAsState(if (isChosen) CardDefaults.cardColors().containerColor else CardDefaults.elevatedCardColors().containerColor)

    Card(
        onClick = onOpen,
        shape = RoundedCornerShape(corners),
        colors = CardDefaults.cardColors().copy(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(16.dp, 8.dp)) {
            Text(
                model.isoCode,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                model.price,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

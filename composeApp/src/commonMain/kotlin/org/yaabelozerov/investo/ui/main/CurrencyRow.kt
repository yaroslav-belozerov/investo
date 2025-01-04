package org.yaabelozerov.investo.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseInElastic
import androidx.compose.animation.core.EaseInExpo
import androidx.compose.animation.core.EaseInQuint
import androidx.compose.animation.core.EaseOutQuint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.yaabelozerov.investo.CrossfadeText
import org.yaabelozerov.investo.horizontalFadingEdge
import org.yaabelozerov.investo.isLayoutWide
import org.yaabelozerov.investo.ui.main.model.CurrencyModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CurrencyRow(items: List<CurrencyModel>) {
    val scrollState = rememberScrollState()
    var current by remember {
        mutableStateOf(items.firstOrNull() ?: CurrencyModel("", "", "", "", ""))
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
        if (isLayoutWide()) {
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

        CurrencyCard(current, isOpen)
    }
}

@Composable
fun CurrencyChip(
    model: CurrencyModel, isChosen: Boolean, onOpen: () -> Unit
) {
    val corners by animateDpAsState(if (isChosen) 32.dp else 12.dp)
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

@Composable
fun CurrencyCard(model: CurrencyModel, isOpen: Boolean = false) {
    AnimatedVisibility(
        isOpen, enter = fadeIn(
            animationSpec = tween(
                300, easing = EaseInQuint
            )
        ) + slideInVertically(initialOffsetY = { -it / 3 }) + expandVertically(), exit = fadeOut(
            animationSpec = tween(
                300, easing = EaseOutQuint
            )
        ) + slideOutVertically(targetOffsetY = { -it / 3 }) + shrinkVertically()
    ) {
        OutlinedCard(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp), border = BorderStroke(
                OutlinedTextFieldDefaults.FocusedBorderThickness, MaterialTheme.colorScheme.primary
            ), shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CrossfadeText(model.name)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CrossfadeText(
                            "MAX",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                        CrossfadeText(model.maxPrice)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CrossfadeText(
                            "MIN",
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                        CrossfadeText(model.minPrice)
                    }
                }
            }
        }
    }
}
package org.yaabelozerov.investo.ui.main

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.yaabelozerov.investo.horizontalFadingEdge
import org.yaabelozerov.investo.shimmerBackground
import org.yaabelozerov.investo.ui.main.model.CurrencyModel
import kotlin.math.max

@Composable
fun CurrencyRow(items: Map<String, CurrencyModel>) {
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
            modifier = Modifier.horizontalFadingEdge(scrollState, 16.dp)
                .horizontalScroll(scrollState), horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            (items.values + (1..max(0, 5 - items.size)).map {
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
                modifier = Modifier.fillMaxWidth().padding(0.dp, 16.dp, 0.dp, 0.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(
                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary)
                )),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    modifier = Modifier.padding(0.dp, 24.dp, 0.dp, 0.dp).fillMaxWidth(),
                    text = current.name,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
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
                        Text(current.maxPrice, color = MaterialTheme.colorScheme.error)
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
                        Text(current.minPrice, color = MaterialTheme.colorScheme.secondary)
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
            Box(
                modifier = Modifier.clip(MaterialTheme.shapes.medium).then(if (!loaded) Modifier.shimmerBackground(
                        MaterialTheme.shapes.medium
                    )
                    else Modifier.background(CardDefaults.outlinedCardColors().containerColor, MaterialTheme.shapes.medium)
                        .clickable { onOpen() }).border(
                        1.dp, if (loaded) {
                            if (!isChosen) OutlinedTextFieldDefaults.colors().unfocusedIndicatorColor
                            else MaterialTheme.colorScheme.primary
                        } else Color.Transparent, MaterialTheme.shapes.medium
                    )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        model.isoCode,
                        color = if (loaded) MaterialTheme.colorScheme.onBackground else Color.Transparent
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

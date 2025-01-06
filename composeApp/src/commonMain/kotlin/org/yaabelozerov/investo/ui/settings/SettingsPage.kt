package org.yaabelozerov.investo.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalStrings
import cafe.adriel.lyricist.Lyricist
import org.koin.compose.viewmodel.koinViewModel
import org.yaabelozerov.investo.domain.MainViewModel
import org.yaabelozerov.investo.isLayoutWide
import org.yaabelozerov.investo.ui.Localization

@Composable
fun SettingsPage(
    onSetLanguage: (String) -> Unit, modifier: Modifier = Modifier, mvm: MainViewModel = koinViewModel()
) {
    val token = mvm.token.collectAsState().value
    var newToken by remember { mutableStateOf(token) }
    val fr = LocalFocusManager.current
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OutlinedTextField(
                modifier = Modifier.fillParentMaxWidth(),
                value = newToken,
                onValueChange = { newToken = it },
                trailingIcon = {
                    if (newToken != token) {
                        IconButton(onClick = {
                            mvm.setToken(newToken)
                            fr.clearFocus()
                        }) {
                            Icon(Icons.Default.Check, contentDescription = null)
                        }
                    }
                },
                visualTransformation = PasswordVisualTransformation(),
                prefix = {
                    Text("${LocalStrings.current.token}: ")
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )
        }
        item {
            var isLanguageDialogOpen by remember { mutableStateOf(false) }
            val textStyle = LocalTextStyle.current
            val def = OutlinedTextFieldDefaults
            Row(modifier = Modifier.fillParentMaxWidth()) {
                OutlinedCard(
                    onClick = { isLanguageDialogOpen = true },
                    modifier = Modifier.fillParentMaxWidth()
                        .height(def.MinHeight),
                    border = if (isLanguageDialogOpen) {
                        BorderStroke(
                            def.FocusedBorderThickness,
                            def.colors().focusedIndicatorColor
                        )
                    } else {
                        BorderStroke(
                            def.UnfocusedBorderThickness,
                            def.colors().unfocusedIndicatorColor
                        )
                    }
                ) {
                    DropdownMenu(
                        expanded = isLanguageDialogOpen,
                        onDismissRequest = { isLanguageDialogOpen = false },
                        offset = DpOffset(0.dp, -def.MinHeight),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Localization.entries.forEach {
                            DropdownMenuItem(text = {
                                Text(
                                    it.strings.localeName,
                                    style = textStyle,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }, onClick = {
                                mvm.languageTag = it.strings.tag
                                onSetLanguage(it.strings.tag)
                                isLanguageDialogOpen = false
                            })
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxSize().padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "${LocalStrings.current.chooseLanguage}:",
                            color = OutlinedTextFieldDefaults.colors().unfocusedPrefixColor
                        )
                        Text(
                            LocalStrings.current.localeName,
                            color = OutlinedTextFieldDefaults.colors().unfocusedPrefixColor
                        )
                    }
                }
            }
        }
    }
}

package org.yaabelozerov.investo.ui.main

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.LocalStrings
import org.yaabelozerov.investo.NetworkError

@Composable
fun ShareSearchBar(
    query: String,
    onUpdateQuery: (String) -> Unit,
    error: NetworkError?,
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
        isError = error != null,
        singleLine = true,
        enabled = !isLoading,
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
            LocalStrings.current.run {
                Text(error?.let(networkError) ?: shares)
            }
        },
    )
}

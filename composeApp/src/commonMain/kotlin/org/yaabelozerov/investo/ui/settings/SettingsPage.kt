package org.yaabelozerov.investo.ui.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.yaabelozerov.investo.domain.MainViewModel

@Composable
fun SettingsPage(mvm: MainViewModel, modifier: Modifier = Modifier) {
    val token = mvm.token.collectAsState().value
    var newToken by remember { mutableStateOf(token) }
    val fr = LocalFocusManager.current
    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        item {
            OutlinedTextField(modifier = Modifier.fillParentMaxWidth(), value = newToken, onValueChange = { newToken = it }, trailingIcon = {
                if (newToken != token) {
                    IconButton(onClick = {
                        mvm.setToken(newToken)
                        fr.clearFocus()
                    }) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                }
            }, visualTransformation = PasswordVisualTransformation(), prefix = {
                Text("Token: ")
            }, singleLine = true, shape = MaterialTheme.shapes.medium)
        }
    }
}

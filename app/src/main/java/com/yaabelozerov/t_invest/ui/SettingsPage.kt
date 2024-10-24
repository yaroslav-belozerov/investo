package com.yaabelozerov.t_invest.ui

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.yaabelozerov.t_invest.domain.MainViewModel

@Composable
fun SettingsPage(mvm: MainViewModel, modifier: Modifier = Modifier) {
    val tok = mvm.tokenState.collectAsState().value
    var currToken by remember { mutableStateOf(tok.first) }
    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        item {
            OutlinedTextField(currToken, onValueChange = { currToken = it }, trailingIcon = {
                if (currToken != tok.first) {
                    IconButton(onClick = {
                        mvm.setToken(currToken)
                    }) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                }
            }, visualTransformation = PasswordVisualTransformation(), prefix = {
                Text("Token: ")
            }, singleLine = true, shape = MaterialTheme.shapes.medium)
        }
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Sandbox Token", modifier = Modifier.weight(1f))
                Checkbox(tok.second, onCheckedChange = { mvm.setSandboxStatus(it) })
            }
        }
    }
}
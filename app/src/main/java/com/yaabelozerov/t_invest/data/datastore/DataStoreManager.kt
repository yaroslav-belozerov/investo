package com.yaabelozerov.t_invest.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class DataStoreManager(app: Context) {
    private val tokenPreferencesKey = stringPreferencesKey("token")
    private val sandboxStatusKey = booleanPreferencesKey("is_sandbox")
    private val settingsDataStore = app.dataStore

    fun getToken(): Flow<String> = settingsDataStore.data.map { it[tokenPreferencesKey] ?: "" }
    suspend fun setToken(token: String) = settingsDataStore.edit { it[tokenPreferencesKey] = token }

    fun getSandboxStatus(): Flow<Boolean> = settingsDataStore.data.map { it[sandboxStatusKey] ?: true }
    suspend fun setSandboxStatus(status: Boolean) = settingsDataStore.edit { it[sandboxStatusKey] = status }
}
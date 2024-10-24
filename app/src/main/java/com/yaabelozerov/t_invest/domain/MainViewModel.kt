package com.yaabelozerov.t_invest.domain

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.t_invest.data.datastore.DataStoreManager
import com.yaabelozerov.t_invest.domain.model.CurrencyModel
import com.yaabelozerov.t_invest.domain.model.ShareModel
import com.yaabelozerov.t_invest.util.ApiBaseUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val tinkoffRepository: TinkoffRepository, private val dataStoreManager: DataStoreManager): ViewModel() {
    private val _currencies = MutableStateFlow(emptyList<CurrencyModel>())
    val currencies = _currencies.asStateFlow()

    private val _shares = MutableStateFlow(emptyList<ShareModel>())
    val shares = _shares.asStateFlow()

    private val _loadingShares = MutableStateFlow(false)
    val loading = _loadingShares.asStateFlow()

    private val _tokenState = MutableStateFlow(Pair("", false))
    val tokenState = _tokenState.asStateFlow()

    init {
        fetchToken {
            try {
                fetchCurrencies(_tokenState.value.first, if (_tokenState.value.second) ApiBaseUrl.SANDBOX_BASE_URL else ApiBaseUrl.PROD_BASE_URL)
            } catch (_: Throwable) { }
        }
    }

    fun fetchToken(callback: () -> Unit = {}) {
        viewModelScope.launch {
            dataStoreManager.getToken().collect { token ->
                dataStoreManager.getSandboxStatus().collect { isSandbox ->
                    Log.i("isSandbox", isSandbox.toString())
                    _tokenState.update { Pair(token, isSandbox) }
                    callback()
                }
            }
        }
    }

    fun fetchCurrencies(token: String, url: ApiBaseUrl) {
        viewModelScope.launch {
            try {
                tinkoffRepository.getCurrencies(token, url).collect { curr ->
                    Log.i("update currencies", curr.toString())
                    _currencies.update { it.plus(curr) }
                }
            } catch (_: Throwable) {}
        }
    }

    fun searchShares(query: String) {
        viewModelScope.launch {
            _shares.update { emptyList() }
            _loadingShares.update { true }
            tinkoffRepository.findShare(query, _tokenState.value.first, if (_tokenState.value.second) ApiBaseUrl.SANDBOX_BASE_URL else ApiBaseUrl.PROD_BASE_URL).collect { share ->
                _shares.update { share.first }
                _loadingShares.update { !share.second }
            }
        }
    }

    fun setToken(token: String) {
        viewModelScope.launch {
            dataStoreManager.setToken(token)
            fetchToken {
                try {
                    fetchCurrencies(_tokenState.value.first, if (_tokenState.value.second) ApiBaseUrl.SANDBOX_BASE_URL else ApiBaseUrl.PROD_BASE_URL)
                } catch (_: Throwable) { }
            }
        }
    }

    fun setSandboxStatus(status: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setSandboxStatus(status)
            fetchToken {
                try {
                    fetchCurrencies(_tokenState.value.first, if (_tokenState.value.second) ApiBaseUrl.SANDBOX_BASE_URL else ApiBaseUrl.PROD_BASE_URL)
                } catch (_: Throwable) { }
            }
        }
    }
}
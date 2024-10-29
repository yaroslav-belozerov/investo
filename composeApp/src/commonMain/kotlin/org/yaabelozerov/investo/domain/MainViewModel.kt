package org.yaabelozerov.investo.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.yaabelozerov.investo.network.ApiBaseUrl
import org.yaabelozerov.investo.ui.main.model.CurrencyModel
import org.yaabelozerov.investo.ui.main.model.ShareModel

class MainViewModel(private val tinkoffRepository: TinkoffRepository): ViewModel() {
    private val _currencies = MutableStateFlow(emptyMap<String, CurrencyModel>())
    val currencies = _currencies.asStateFlow()

    private val _shares = MutableStateFlow(emptyList<ShareModel>())
    val shares = _shares.asStateFlow()

    private val _loadingShares = MutableStateFlow(false)
    val loading = _loadingShares.asStateFlow()

    private val _token = MutableStateFlow("")
    val token = _token.asStateFlow()

    private val settings = Settings()

    init {
        fetchToken {
            try {
                fetchCurrencies()
            } catch (_: Throwable) { }
        }
    }

    private fun fetchToken(callback: () -> Unit = {}) {
        _token.update { settings.getString("token", "") }
        callback()
    }

    fun fetchCurrencies() {
        viewModelScope.launch {
            try {
                tinkoffRepository.getCurrencies(_token.value).collect { curr ->
                    _currencies.update { it.plus(curr.isoCode to curr) }
                }
            } catch (_: Throwable) {}
        }
    }

    fun searchShares(query: String) {
        viewModelScope.launch {
            _shares.update { emptyList() }
            _loadingShares.update { true }
            tinkoffRepository.findShare(query, _token.value).collect { share ->
                _shares.update { share.first }
                _loadingShares.update { !share.second }
            }
        }
    }

    fun setToken(token: String) {
        settings.set("token", token)
        _token.update { token }
        fetchCurrencies()
    }
}

package org.yaabelozerov.investo.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.yaabelozerov.investo.network.ApiBaseUrl
import org.yaabelozerov.investo.ui.main.model.CurrencyModel
import org.yaabelozerov.investo.ui.main.model.ShareModel

data class MainState(
    val currencies: List<CurrencyModel> = emptyList(),
    val loadingCurrencies: Boolean = true,
    val shares: List<ShareModel> = emptyList(),
    val loadingShares: Boolean = false
)

class MainViewModel(private val tinkoffRepository: TinkoffRepository): ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    private val _token = MutableStateFlow("")
    val token = _token.asStateFlow()

    private val settings = Settings()

    init {
        _token.update { settings["token"] ?: "" }
        fetchCurrencies()
    }

    private fun fetchCurrencies() {
        viewModelScope.launch {
            try {
                tinkoffRepository.getCurrencies(_token.value) {
                    _state.update { it.copy(loadingCurrencies = false) }
                }.collect { curr ->
                    _state.update { it.copy(currencies = it.currencies.plus(curr)) }
                }
            } catch (_: Throwable) {}
        }
    }

    fun searchShares(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(shares = emptyList(), loadingShares = true) }
            tinkoffRepository.findShare(query, _token.value).collect { share ->
                _state.update { it.copy(shares = share.first, loadingShares = !share.second) }
            }
        }
    }

    fun setToken(token: String) {
        settings["token"] = token
        _token.update { token }
        fetchCurrencies()
    }
}

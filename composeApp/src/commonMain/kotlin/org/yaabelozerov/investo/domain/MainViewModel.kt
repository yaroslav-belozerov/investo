package org.yaabelozerov.investo.domain

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.lyricist.rememberStrings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import io.github.aakira.napier.Napier
import io.github.aakira.napier.log
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.yaabelozerov.investo.NetworkError
import org.yaabelozerov.investo.NetworkResult
import org.yaabelozerov.investo.network.ApiBaseUrl
import org.yaabelozerov.investo.network.model.ShareDTO
import org.yaabelozerov.investo.ui.main.model.CurrencyModel
import org.yaabelozerov.investo.ui.main.model.ShareModel

data class MainState(
    val currencies: List<CurrencyModel> = emptyList(),
    val loadingCurrencies: Boolean = true,
    val currencyError: NetworkError? = null,
    val shares: List<ShareModel> = emptyList(),
    val loadingShares: Boolean = false,
    val shareError: NetworkError? = null,
    val searchQuery: String = ""
)

class MainViewModel(private val tinkoffRepository: TinkoffRepository) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    private val _token = MutableStateFlow("")
    val token = _token.asStateFlow()

    private val settings = Settings()

    var languageTag
        get() = settings.getString("language", Locale.current.toLanguageTag())
        set(value) = settings.set("language", value)

    init {
        _token.update { settings["token"] ?: "" }
        fetchCurrencies()
    }

    fun setQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    private fun fetchCurrencies() {
        viewModelScope.launch {
            try {
                tinkoffRepository.getCurrencies(_token.value).collect { curr ->
                    when (curr) {
                        is NetworkResult.Success -> _state.update { it.copy(currencies = curr.value, currencyError = null) }
                        is NetworkResult.Error -> _state.update { it.copy(loadingCurrencies = false, currencyError = curr.error) }
                        is NetworkResult.Finished -> _state.update { it.copy(loadingCurrencies = false) }
                    }
                }
            } catch (t: Throwable) {
                log(throwable = t) { "Error fetching currencies in MainViewModel" }
            }
        }
    }

    fun searchShares() {
        viewModelScope.launch {
            _state.update { it.copy(shares = emptyList(), loadingShares = true) }
            tinkoffRepository.findShare(_state.value.searchQuery, _token.value).collect { share ->
                when (share) {
                    is NetworkResult.Success -> _state.update { it.copy(shares = it.shares + share.value, shareError = null) }
                    is NetworkResult.Error -> _state.update { it.copy(loadingShares = false, shareError = share.error, searchQuery = "") }
                    is NetworkResult.Finished -> _state.update { it.copy(loadingShares = false) }
                }
            }
        }
    }

    fun setToken(token: String) {
        settings["token"] = token
        _token.update { token }
        fetchCurrencies()
    }
}

package org.yaabelozerov.investo.domain

import kotlinx.coroutines.flow.Flow
import org.yaabelozerov.investo.network.ApiBaseUrl
import org.yaabelozerov.investo.ui.main.model.CurrencyModel
import org.yaabelozerov.investo.ui.main.model.ShareModel

interface TinkoffRepository {
    fun getCurrencies(token: String, onFinish: () -> Unit = {}): Flow<CurrencyModel>
    suspend fun findShare(query: String, token: String): Flow<Pair<List<ShareModel>, Boolean>>
}

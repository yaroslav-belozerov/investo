package org.yaabelozerov.investo.domain

import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.flow.Flow
import kotlinx.io.IOException
import org.yaabelozerov.investo.NetworkResult
import org.yaabelozerov.investo.ui.main.model.CurrencyModel
import org.yaabelozerov.investo.ui.main.model.ShareModel

interface TinkoffRepository {
    fun getCurrencies(token: String): Flow<NetworkResult<List<CurrencyModel>>>
    suspend fun findShare(query: String, token: String): Flow<NetworkResult<ShareModel>>
}

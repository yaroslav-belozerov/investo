package com.yaabelozerov.t_invest.domain

import com.yaabelozerov.t_invest.domain.model.CurrencyModel
import com.yaabelozerov.t_invest.domain.model.ShareModel
import com.yaabelozerov.t_invest.util.ApiBaseUrl
import kotlinx.coroutines.flow.Flow

interface TinkoffRepository {
    fun getCurrencies(token: String, url: ApiBaseUrl): Flow<CurrencyModel>
//    suspend fun getShares(): List<ShareModel>
    suspend fun findShare(query: String, token: String, url: ApiBaseUrl): Flow<Pair<List<ShareModel>, Boolean>>
}
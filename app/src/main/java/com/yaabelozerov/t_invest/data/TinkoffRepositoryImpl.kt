package com.yaabelozerov.t_invest.data

import android.util.Log
import com.yaabelozerov.t_invest.data.remote.TinkoffApi
import com.yaabelozerov.t_invest.data.remote.model.GetShareByRequest
import com.yaabelozerov.t_invest.data.remote.model.InstrumentId
import com.yaabelozerov.t_invest.data.remote.model.OrderBookRequest
import com.yaabelozerov.t_invest.data.remote.model.asDouble
import com.yaabelozerov.t_invest.domain.TinkoffRepository
import com.yaabelozerov.t_invest.domain.model.CurrencyModel
import com.yaabelozerov.t_invest.domain.model.ShareModel
import com.yaabelozerov.t_invest.util.ApiBaseUrl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class TinkoffRepositoryImpl(private val tinkoffApi: TinkoffApi) : TinkoffRepository {
    override fun getCurrencies(token: String, url: ApiBaseUrl): Flow<CurrencyModel> = flow {
        val fmt = DecimalFormat("#,##0.000", DecimalFormatSymbols(Locale.FRANCE))
        tinkoffApi.getCurrencies(token, url).currencyInstruments.map {
            val units = it.nominal?.units?.toDouble()
            val nano = it.nominal?.nano?.toDouble()?.div(1000000000) ?: 0.0
            val nominal = units?.plus(nano)

            val resp = tinkoffApi.getOrderBook(OrderBookRequest(
                it.figi!!,
                depth = 5
            ), token, url)

            val lastPrice = resp.lastPrice.asDouble()
            val minPrice = resp.limitDown.asDouble()
            val maxPrice = resp.limitUp.asDouble()
            val lot = it.lot

            if (nominal != null && lot != null && it.countryOfRisk == "") {
                emit(CurrencyModel(
                    isoCode = it.isoCurrencyName?.uppercase() ?: "",
                    it.name ?: "",
                    isLoaded = true,
                    price = fmt.format(lastPrice / nominal).plus(if (it.currency == "rub") " ₽" else it.currency), minPrice = fmt.format(minPrice / nominal), maxPrice = fmt.format(maxPrice / nominal)))
            }
        }
    }

    override suspend fun findShare(query: String, token: String, url: ApiBaseUrl): Flow<Pair<List<ShareModel>, Boolean>> = flow {
        val fmt = DecimalFormat("#,##0.##", DecimalFormatSymbols(Locale.FRANCE))
        val instrum = tinkoffApi.findShares(query, token, url)
        val indexOfLast = instrum.findInstruments?.size?.minus(1) ?: -1
        val out = mutableMapOf<String, ShareModel>()
        instrum.findInstruments?.forEachIndexed { index, it ->
            val classCode = it.classCode
            val figi = it.figi
            if (classCode != null && figi != null) {
                out[figi] = ShareModel(figi,"????????????????????????", false, "1000", "")
                emit(Pair(out.values.toList(), false))
                val share = tinkoffApi.getShareByFigi(GetShareByRequest(
                    idType = InstrumentId.INSTRUMENT_ID_TYPE_FIGI,
                    classCode = classCode,
                    id = figi
                ), token, url)?.shareInstruments

                out[figi] = ShareModel(figi, share?.name ?: "No name", false, "1000", share?.countryOfRiskName ?: "")
                emit(Pair(out.values.toList(), false))
                val resp = tinkoffApi.getOrderBook(OrderBookRequest(
                    figi = figi,
                    depth = 1
                ), token, url)
                val lastPrice = resp.lastPrice.asDouble()

                out[figi] = ShareModel(
                    figi,
                    share?.name ?: "No name",
                    canShort = share?.shortEnabledFlag == true,
                    fmt.format(lastPrice?.times(share?.lot ?: 1) ?: 0).plus(" ₽"),
                    share?.countryOfRisk ?: "",
                    share?.apiTradeAvailableFlag == true,
                    share?.buyAvailableFlag == true,
                    share?.sellAvailableFlag == true,
                    true
                )
                emit(Pair(out.values.toList(), index == indexOfLast))
            }
        }
    }
}
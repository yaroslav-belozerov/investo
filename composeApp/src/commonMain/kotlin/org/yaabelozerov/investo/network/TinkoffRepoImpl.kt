package org.yaabelozerov.investo.network

import io.github.aakira.napier.Napier
import io.github.aakira.napier.log
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.yaabelozerov.investo.DecimalFormat
import org.yaabelozerov.investo.LocaleMap
import org.yaabelozerov.investo.check
import org.yaabelozerov.investo.NetworkResult
import org.yaabelozerov.investo.domain.TinkoffRepository
import org.yaabelozerov.investo.toError
import org.yaabelozerov.investo.network.model.GetShareByRequest
import org.yaabelozerov.investo.network.model.InstrumentId
import org.yaabelozerov.investo.network.model.OrderBookRequest
import org.yaabelozerov.investo.network.model.asDouble
import org.yaabelozerov.investo.onFailure
import org.yaabelozerov.investo.ui.main.model.CurrencyModel
import org.yaabelozerov.investo.ui.main.model.ShareModel

class TinkoffRepositoryImpl(private val tinkoffApi: TinkoffApi) : TinkoffRepository {
    private val fmt = DecimalFormat()
    private val localeMap = LocaleMap()

    override fun getCurrencies(token: String): Flow<NetworkResult<List<CurrencyModel>>> =
        flow {
            coroutineScope {
                val resCurrencies = check {
                    tinkoffApi.getCurrencies(
                        token, ApiBaseUrl.SANDBOX_BASE_URL
                    )
                }.onFailure {
                    log(throwable = it) { "Error getting currencies" }
                    emit(NetworkResult.Error(it.toError()))
                } ?: return@coroutineScope

                resCurrencies.currencyInstruments.map { instrument ->
                    async {
                        val units = instrument.nominal?.units?.toDouble()
                        val nano = instrument.nominal?.nano?.toDouble()?.div(1000000000) ?: 0.0
                        val nominal = units?.plus(nano)

                        val orderBook =
                            check {
                                tinkoffApi.getOrderBook(
                                    OrderBookRequest(
                                        instrument.figi!!, depth = 5
                                    ), token, ApiBaseUrl.SANDBOX_BASE_URL
                                )
                            }.onFailure {
                                log(throwable = it) { "Error getting order book for ${instrument.figi}" }
                                emit(NetworkResult.Error(it.toError()))
                            } ?: return@async null

                        val lastPrice = orderBook.lastPrice.asDouble()
                        val minPrice = orderBook.limitDown.asDouble()
                        val maxPrice = orderBook.limitUp.asDouble()
                        val lot = instrument.lot

                        if (nominal == null || lot == null || instrument.countryOfRisk != "" || listOf(
                                lastPrice, maxPrice, minPrice
                            ).any { it <= 0.0 }
                        ) return@async null

                        val currencySymbolPostfix =
                            " ${localeMap.currencyToSymbol(instrument.currency ?: "")}"
                        return@async CurrencyModel(
                            isoCode = instrument.isoCurrencyName?.uppercase()
                                ?: error("No iso currency code"),
                            instrument.name ?: "",
                            price = fmt.format(lastPrice / nominal, 3).plus(currencySymbolPostfix),
                            minPrice = fmt.format(minPrice / nominal, 3)
                                .plus(currencySymbolPostfix),
                            maxPrice = fmt.format(maxPrice / nominal, 3).plus(currencySymbolPostfix)
                        )
                    }
                }.awaitAll().filterNotNull().let { emit(NetworkResult.Success(it)) }
                emit(NetworkResult.Finished)
            }
        }

    override suspend fun findShare(
        query: String, token: String
    ): Flow<NetworkResult<ShareModel>> = flow {
        val findInstrument = check {
            tinkoffApi.findShares(query, token, ApiBaseUrl.SANDBOX_BASE_URL)
        }.onFailure {
            log(throwable = it) { "Error getting shares by query: $query" }
            emit(NetworkResult.Error(it.toError()))
        } ?: return@flow

        findInstrument.instruments.forEach { instrument ->
            val classCode = instrument.classCode
            val figi = instrument.figi
            if (classCode != null && figi != null) {
                val share = check {
                    tinkoffApi.getShareByFigi(
                        GetShareByRequest(
                            idType = InstrumentId.INSTRUMENT_ID_TYPE_FIGI,
                            classCode = classCode,
                            id = figi
                        ), token, ApiBaseUrl.SANDBOX_BASE_URL
                    )
                }.onFailure {
                    log(throwable = it) { "Error getting share by figi $figi" }
                    emit(NetworkResult.Error(it.toError()))
                } ?:  return@flow

                val shareInstruments = share.shareInstruments

                val resp = check {
                    tinkoffApi.getOrderBook(
                        OrderBookRequest(
                            figi = figi, depth = 1
                        ), token, ApiBaseUrl.SANDBOX_BASE_URL
                    )
                }.onFailure {
                    log(throwable = it) { "Error getting order book for figi $figi" }
                    emit(NetworkResult.Error(it.toError()))
                } ?: return@flow

                val lastPrice = resp.lastPrice.asDouble()

                val out = ShareModel(figi,
                    shareInstruments.name ?: "No name",
                    canShort = shareInstruments.shortEnabledFlag == true,
                    fmt.format(lastPrice.times(shareInstruments.lot ?: 1), 2)
                        .plus(" ${localeMap.currencyToSymbol(shareInstruments.currency ?: "")}"),
                    shareInstruments.countryOfRisk?.let { localeMap.countryToFlag(it) } ?: "",
                    shareInstruments.apiTradeAvailableFlag == true,
                    shareInstruments.buyAvailableFlag == true,
                    shareInstruments.sellAvailableFlag == true)
                emit(NetworkResult.Success(out))
            }
        }
        emit(NetworkResult.Finished)
    }
}
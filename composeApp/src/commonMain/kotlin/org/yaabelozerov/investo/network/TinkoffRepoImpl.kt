package org.yaabelozerov.investo.network

import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.yaabelozerov.investo.DecimalFormat
import org.yaabelozerov.investo.LocaleMap
import org.yaabelozerov.investo.domain.TinkoffRepository
import org.yaabelozerov.investo.network.model.GetShareByRequest
import org.yaabelozerov.investo.network.model.InstrumentId
import org.yaabelozerov.investo.network.model.OrderBookRequest
import org.yaabelozerov.investo.network.model.asDouble
import org.yaabelozerov.investo.ui.main.model.CurrencyModel
import org.yaabelozerov.investo.ui.main.model.ShareModel
import kotlin.math.min

class TinkoffRepositoryImpl(private val tinkoffApi: TinkoffApi) : TinkoffRepository {
    private val fmt = DecimalFormat()
    private val localeMap = LocaleMap()

    override fun getCurrencies(token: String): Flow<CurrencyModel> = flow {
        coroutineScope {
            tinkoffApi.getCurrencies(
                token, ApiBaseUrl.SANDBOX_BASE_URL
            ).currencyInstruments.map {
                async {
                    val units = it.nominal?.units?.toDouble()
                    val nano = it.nominal?.nano?.toDouble()?.div(1000000000) ?: 0.0
                    val nominal = units?.plus(nano)

                    val resp = tinkoffApi.getOrderBook(
                        OrderBookRequest(
                            it.figi!!, depth = 5
                        ), token, ApiBaseUrl.SANDBOX_BASE_URL
                    )

                    val lastPrice = resp.lastPrice.asDouble()
                    val minPrice = resp.limitDown.asDouble()
                    val maxPrice = resp.limitUp.asDouble()
                    val lot = it.lot

                    if (nominal == null || lot == null || it.countryOfRisk != "" || listOf(
                            lastPrice, maxPrice, minPrice
                        ).any { it <= 0.0 }
                    ) return@async null

                    return@async CurrencyModel(
                        isoCode = it.isoCurrencyName?.uppercase() ?: error("No iso currency code"),
                        it.name ?: "",
                        isLoaded = true,
                        price = fmt.format(lastPrice / nominal, 3)
                            .plus(" ${localeMap.currencyToSymbol(it.currency ?: "")}"),
                        minPrice = fmt.format(minPrice / nominal, 3),
                        maxPrice = fmt.format(maxPrice / nominal, 3)
                    )
                }
            }.onEach { it.await()?.let { emit(it) } }
        }
    }

    override suspend fun findShare(
        query: String, token: String
    ): Flow<Pair<List<ShareModel>, Boolean>> = flow {
        val instrum = tinkoffApi.findShares(query, token, ApiBaseUrl.SANDBOX_BASE_URL)
        val indexOfLast = instrum.findInstruments?.size?.minus(1) ?: -1
        val out = mutableMapOf<String, ShareModel>()
        instrum.findInstruments?.forEachIndexed { index, it ->
            val classCode = it.classCode
            val figi = it.figi
            if (classCode != null && figi != null) {
                out[figi] = ShareModel(figi, "????????????????????????", false, "1000", "")
                emit(Pair(out.values.toList(), false))
                val share = tinkoffApi.getShareByFigi(
                    GetShareByRequest(
                        idType = InstrumentId.INSTRUMENT_ID_TYPE_FIGI,
                        classCode = classCode,
                        id = figi
                    ), token, ApiBaseUrl.SANDBOX_BASE_URL
                )?.shareInstruments

                out[figi] = ShareModel(
                    figi, share?.name ?: "No name", false, "1000", share?.countryOfRiskName ?: ""
                )
                emit(Pair(out.values.toList(), false))
                val resp = tinkoffApi.getOrderBook(
                    OrderBookRequest(
                        figi = figi, depth = 1
                    ), token, ApiBaseUrl.SANDBOX_BASE_URL
                )
                val lastPrice = resp.lastPrice.asDouble()

                out[figi] = ShareModel(figi,
                    share?.name ?: "No name",
                    canShort = share?.shortEnabledFlag == true,
                    fmt.format(lastPrice.times(share?.lot ?: 1), 2)
                        .plus(localeMap.currencyToSymbol(share?.currency ?: "")),
                    share?.countryOfRisk?.let { localeMap.countryToFlag(it) } ?: "",
                    share?.apiTradeAvailableFlag == true,
                    share?.buyAvailableFlag == true,
                    share?.sellAvailableFlag == true,
                    true)
                emit(Pair(out.values.toList(), index == indexOfLast))
            }
        }
    }
}
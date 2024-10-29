@file:OptIn(ExperimentalSerializationApi::class)

package org.yaabelozerov.investo.network.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrencyDTO(
    @SerialName("instruments")
    val currencyInstruments: List<CurrencyInstrument>,
)

@Serializable
data class CurrencyInstrument(
    val isoCurrencyName: String? = null,
    val figi: String? = null,
    val dshortMin: CurrencyDshortMin? = null,
    val countryOfRisk: String? = null,
    val lot: Long? = null,
    val uid: String? = null,
    val blockedTcaFlag: Boolean? = null,
    val dlong: CurrencyDlong? = null,
    val nominal: CurrencyNominal? = null,
    val sellAvailableFlag: Boolean? = null,
    val currency: String? = null,
//    val first1dayCandleDate: Map<String, Any? = null>,
    val buyAvailableFlag: Boolean? = null,
    val weekendFlag: Boolean? = null,
    val classCode: String? = null,
    val ticker: String? = null,
    val forQualInvestorFlag: Boolean? = null,
    val forIisFlag: Boolean? = null,
    val positionUid: String? = null,
    val apiTradeAvailableFlag: Boolean? = null,
    val dlongMin: CurrencyDlongMin? = null,
    val shortEnabledFlag: Boolean? = null,
    val kshort: CurrencyKshort? = null,
//    val first1minCandleDate: Map<String, Any? = null>,
    val minPriceIncrement: CurrencyMinPriceIncrement? = null,
    val otcFlag: Boolean? = null,
    val klong: CurrencyKlong? = null,
    val dshort: CurrencyDshort? = null,
    val name: String? = null,
    val exchange: String? = null,
    val countryOfRiskName: String? = null,
    val isin: String? = null,
)

@Serializable
data class CurrencyDshortMin(
    val nano: Long? = null,
    val units: String? = null,
)

@Serializable
data class CurrencyDlong(
    val nano: Long? = null,
    val units: String? = null,
)

@Serializable
data class CurrencyNominal(
    val nano: Long? = null,
    val currency: String? = null,
    val units: String? = null,
)

@Serializable
data class CurrencyDlongMin(
    val nano: Long? = null,
    val units: String? = null,
)

@Serializable
data class CurrencyKshort(
    val nano: Long? = null,
    val units: String? = null,
)

@Serializable
data class CurrencyMinPriceIncrement(
    val nano: Long? = null,
    val units: String? = null,
)

@Serializable
data class CurrencyKlong(
    val nano: Long? = null,
    val units: String? = null,
)

@Serializable
data class CurrencyDshort(
    val nano: Long? = null,
    val units: String? = null,
)
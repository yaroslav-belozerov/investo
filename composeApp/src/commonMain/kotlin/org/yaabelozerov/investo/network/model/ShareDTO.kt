package org.yaabelozerov.investo.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShareDTO(
    @SerialName("instrument")
    val shareInstruments: ShareInstrument,
)

@Serializable
data class ShareInstrument(
    val figi: String? = null,
    val dshortMin: SharesDshortMin? = null,
    val countryOfRisk: String? = null,
    val lot: Long? = null,
    val uid: String? = null,
    val blockedTcaFlag: Boolean? = null,
    val dlong: SharesDlong? = null,
    val nominal: SharesNominal? = null,
    val sellAvailableFlag: Boolean? = null,
    val currency: String? = null,
//    val first1dayCandleDate: Map<String, Any>? = null,
    val sector: String? = null,
    val buyAvailableFlag: Boolean? = null,
    val weekendFlag: Boolean? = null,
    val classCode: String? = null,
    val ticker: String? = null,
    val forQualInvestorFlag: Boolean? = null,
    val liquidityFlag: Boolean? = null,
    val forIisFlag: Boolean? = null,
    val positionUid: String? = null,
    val apiTradeAvailableFlag: Boolean? = null,
    val dlongMin: SharesDlongMin? = null,
    val shortEnabledFlag: Boolean? = null,
    val kshort: SharesKshort? = null,
//    val first1minCandleDate: Map<String, Any>? = null,
    val issueSizePlan: String? = null,
    val minPriceIncrement: SharesMinPriceIncrement? = null,
    val otcFlag: Boolean? = null,
    val klong: SharesKlong? = null,
    val dshort: SharesDshort? = null,
    val name: String? = null,
    val issueSize: String? = null,
    val exchange: String? = null,
    val countryOfRiskName: String? = null,
    val divYieldFlag: Boolean? = null,
    val isin: String? = null,
//    val ipoDate: Map<String, Any>? = null,
)

@Serializable
data class SharesDshortMin(
    val nano: Long? = null,
    val units: String? = null,
)

@Serializable
data class SharesDlong(
    val nano: Long? = null,
    val units: String? = null,
)

@Serializable
data class SharesNominal(
    val nano: Long? = null,
    val currency: String? = null,
    val units: String? = null,
)

@Serializable
data class SharesDlongMin(
    val nano: Long? = null,
    val units: String? = null,
)

@Serializable
data class SharesKshort(
    val nano: Long? = null,
    val units: String? = null,
)

@Serializable
data class SharesMinPriceIncrement(
    val nano: Long? = null,
    val units: String? = null,
)

@Serializable
data class SharesKlong(
    val nano: Long? = null,
    val units: String? = null,
)

@Serializable
data class SharesDshort(
    val nano: Long? = null,
    val units: String? = null,
)

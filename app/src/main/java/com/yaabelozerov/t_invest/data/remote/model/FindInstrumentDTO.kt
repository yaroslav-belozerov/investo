package com.yaabelozerov.t_invest.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class FindInstrumentDTO(
    @JsonNames("instruments")
    val findInstruments: List<FindInstrument>? = null,
)

@Serializable
data class FindInstrument(
    val isin: String? = null,
    val figi: String? = null,
    val ticker: String? = null,
    val classCode: String? = null,
    val instrumentType: String? = null,
    val name: String? = null,
    val uid: String? = null,
    val positionUid: String? = null,
    val instrumentKind: String? = null,
    val apiTradeAvailableFlag: Boolean? = null,
    val forIisFlag: Boolean? = null,
    val first1minCandleDate: String? = null,
    val first1dayCandleDate: String? = null,
    val forQualInvestorFlag: Boolean? = null,
    val weekendFlag: Boolean? = null,
    val blockedTcaFlag: Boolean? = null,
    val lot: Long? = null,
)
package org.yaabelozerov.investo.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FindInstrumentDTO(
    @SerialName("instruments")
    val instruments: List<FindInstrument>,
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
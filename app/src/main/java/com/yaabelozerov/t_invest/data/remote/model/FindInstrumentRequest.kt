package com.yaabelozerov.t_invest.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class FindInstrumentRequest(
    val query: String,
    val instrumentKind: InstrumentType,
    val apiTradeAvailableFlag: Boolean = true
)

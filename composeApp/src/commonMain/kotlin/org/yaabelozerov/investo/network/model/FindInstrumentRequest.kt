package org.yaabelozerov.investo.network.model

import kotlinx.serialization.Serializable

@Serializable
data class FindInstrumentRequest(
    val query: String,
    val instrumentKind: InstrumentType,
    val apiTradeAvailableFlag: Boolean = true
)

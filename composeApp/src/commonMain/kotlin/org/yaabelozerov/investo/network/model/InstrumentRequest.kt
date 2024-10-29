package org.yaabelozerov.investo.network.model

import kotlinx.serialization.Serializable

enum class InstrumentStatus {
    INSTRUMENT_STATUS_UNSPECIFIED,
    INSTRUMENT_STATUS_BASE,
    INSTRUMENT_STATUS_ALL,
}

enum class InstrumentType {
    INSTRUMENT_TYPE_SHARE
}

enum class InstrumentId {
    INSTRUMENT_ID_TYPE_FIGI
}

@Serializable
data class InstrumentRequestBody(
    val instrumentStatus: InstrumentStatus
)
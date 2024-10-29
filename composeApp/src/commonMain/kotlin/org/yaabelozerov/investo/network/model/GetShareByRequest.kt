package org.yaabelozerov.investo.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetShareByRequest(
    @SerialName("idType")
    val idType: InstrumentId = InstrumentId.INSTRUMENT_ID_TYPE_FIGI,
    val classCode: String,
    val id: String,
)

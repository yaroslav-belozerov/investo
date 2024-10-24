package com.yaabelozerov.t_invest.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class GetShareByRequest(
    @JsonNames("idType")
    val idType: InstrumentId = InstrumentId.INSTRUMENT_ID_TYPE_FIGI,
    val classCode: String,
    val id: String,
)

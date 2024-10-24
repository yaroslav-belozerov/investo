package com.yaabelozerov.t_invest.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderBookRequest(
    val figi: String,
    val depth: Long,
    val instrumentId: String? = null,
)

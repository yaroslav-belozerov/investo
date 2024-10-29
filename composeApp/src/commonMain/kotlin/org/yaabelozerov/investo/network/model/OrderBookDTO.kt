package org.yaabelozerov.investo.network.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderBookDTO(
    val depth: Long? = null,
//    val lastPriceTs: Map<String, Quotation>? = null,
    val asks: List<Ask>? = null,
//    val orderbookTs: Map<String, Any>? = null,
    val instrumentUid: String? = null,
    val bids: List<Bid>? = null,
    val limitUp: Price? = null,
    val figi: String? = null,
    val closePrice: Price? = null,
    val limitDown: Price? = null,
//    val closePriceTs: Map<String, Any>? = null,
    val lastPrice: Price? = null,
)

@Serializable
data class Ask(
    val quantity: String? = null,
    val price: Price? = null,
)

@Serializable
data class Price(
    val nano: Long? = null,
    val units: String? = null,
)

@Serializable
data class Bid(
    val quantity: String? = null,
    val price: Price? = null,
)

fun Price?.asDouble(): Double {
    val lastUnits = this?.units?.toDouble()
    val lastNano = this?.nano?.toDouble()?.div(1000000000) ?: 0.0
    return lastUnits?.plus(lastNano) ?: 0.0
}
package org.yaabelozerov.investo.ui.main.model

data class ShareModel(
    val figi: String,
    val name: String,
    val canShort: Boolean,
    val price: String,
    val country: String,
    val apiTrade: Boolean = false,
    val canBuy: Boolean = false,
    val canSell: Boolean = false
)
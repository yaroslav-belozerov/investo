package com.yaabelozerov.t_invest.domain.model

data class ShareModel(
    val figi: String,
    val name: String,
    val canShort: Boolean,
    val price: String,
    val country: String,
    val apiTrade: Boolean = false,
    val canBuy: Boolean = false,
    val canSell: Boolean = false,
    val isLoaded: Boolean = false
)
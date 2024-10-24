package com.yaabelozerov.t_invest.domain.model

data class CurrencyModel(
    val isoCode: String,
    val name: String,
    val price: String,
    val isLoaded: Boolean = false,
    val minPrice: String,
    val maxPrice: String
)
package com.yaabelozerov.t_invest.util

enum class ApiBaseUrl(val url: String) {
    PROD_BASE_URL("https://invest-public-api.tinkoff.ru/rest/"),
    SANDBOX_BASE_URL("https://sandbox-invest-public-api.tinkoff.ru/rest/")
}
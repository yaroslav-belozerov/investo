package org.yaabelozerov.investo

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowWidthSizeClass
import io.ktor.client.engine.HttpClientEngine
import kotlin.experimental.ExperimentalNativeApi
import kotlin.math.pow
import kotlin.math.round

actual class DecimalFormat actual constructor() {
    actual fun format(num: Double, places: Int): String {
        return round(num.times(10.0.pow(places))).div(10.0.pow(places)).toString()
    }
}

actual class LocaleMap actual constructor() {
    @OptIn(ExperimentalNativeApi::class)
    actual fun countryToFlag(isoCode: String): String? {
        return isoCode
    }

    actual fun currencyToSymbol(isoCode: String): String {
        val isoCode = isoCode.uppercase()
        val map = mapOf(
            "USD" to "$", // US Dollar
            "RUB" to "₽", // Russian Ruble
            "EUR" to "€", // Euro
            "CRC" to "₡", // Costa Rican Colón
            "GBP" to "£", // British Pound Sterling
            "ILS" to "₪", // Israeli New Sheqel
            "INR" to "₹", // Indian Rupee
            "JPY" to "¥", // Japanese Yen
            "KRW" to "₩", // South Korean Won
            "NGN" to "₦", // Nigerian Naira
            "PHP" to "₱", // Philippine Peso
            "PLN" to "ł", // Polish Zloty
            "PYG" to "₲", // Paraguayan Guarani
            "THB" to "฿", // Thai Baht
            "UAH" to "₴", // Ukrainian Hryvnia
            "VND" to "₫", // Vietnamese Dong
        )
        return map[isoCode] ?: isoCode
    }
}

@Composable
actual fun isLayoutWide() = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT
package org.yaabelozerov.investo

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.russhwolf.settings.Settings
import org.yaabelozerov.investo.network.TinkoffApi
import kotlin.math.pow
import kotlin.math.round

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

@Composable
actual fun AppTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if(darkTheme) darkColorScheme() else lightColorScheme(),
        typography = MaterialTheme.typography,
        content = content
    )
}

actual class DecimalFormat actual constructor() {
    actual fun format(num: Double, places: Int): String {
        return round(num.times(10.0.pow(places))).div(10.0.pow(places)).toString()
    }
}

actual class LocaleMap actual constructor() {
    actual fun countryToFlag(isoCode: String): String {
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
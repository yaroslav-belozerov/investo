package org.yaabelozerov.investo

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.russhwolf.settings.Settings
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpEngine
import io.ktor.client.engine.okhttp.OkHttpEngineContainer
import kotlinx.coroutines.Dispatchers
import org.yaabelozerov.investo.network.TinkoffApi
import org.yaabelozerov.investo.ui.theme.Typography
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import kotlin.math.pow
import kotlin.math.round

actual class DecimalFormat actual constructor() {
    actual fun format(num: Double, places: Int): String {
        return round(num.times(10.0.pow(places))).div(10.0.pow(places)).toString()
    }
}

actual class LocaleMap actual constructor() {
    actual fun countryToFlag(isoCode: String): String? {
        val isoCode = isoCode.uppercase()
        var flagOffset = 0x1F1E6
        var asciiOffset = 0x41
        var firstChar: Int
        var secondChar: Int
        try {
            firstChar = Character.codePointAt(isoCode.uppercase(), 0) - asciiOffset + flagOffset
            secondChar = Character.codePointAt(isoCode.uppercase(), 1) - asciiOffset + flagOffset
        } catch (_: Exception) { return null }
        return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
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
actual fun isLayoutWide() = true

actual object Net {
    actual val engine = OkHttp.create {
        config {
            followSslRedirects(true)
            followRedirects(true)
        }
    }
}
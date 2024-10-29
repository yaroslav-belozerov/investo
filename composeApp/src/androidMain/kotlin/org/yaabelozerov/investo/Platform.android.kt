package org.yaabelozerov.investo

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.text.DecimalFormat
import java.util.Currency
import java.util.Locale

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

@Composable
actual fun AppTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if(darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}

actual class DecimalFormat {
    actual fun format(num: Double, places: Int): String {
        val df = DecimalFormat()
        df.isGroupingUsed = false
        df.maximumFractionDigits = places
        df.isDecimalSeparatorAlwaysShown = false
        return df.format(num).replace('.', ',')
    }
}

actual class LocaleMap actual constructor() {
    actual fun countryToFlag(isoCode: String): String {
        val isoCode = isoCode.uppercase()
        var flagOffset = 0x1F1E6
        var asciiOffset = 0x41
        var firstChar = Character.codePointAt(isoCode.uppercase(), 0) - asciiOffset + flagOffset
        var secondChar = Character.codePointAt(isoCode.uppercase(), 1) - asciiOffset + flagOffset
        return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
    }

    actual fun currencyToSymbol(isoCode: String): String {
        val isoCode = isoCode.uppercase()
        if (isoCode == "RUB") return "₽"
        val currency: Currency = Currency.getInstance(isoCode)
        return currency.symbol
    }
}
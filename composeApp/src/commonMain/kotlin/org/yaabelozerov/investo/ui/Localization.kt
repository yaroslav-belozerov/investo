package org.yaabelozerov.investo.ui

import cafe.adriel.lyricist.LyricistStrings
import org.yaabelozerov.investo.NetworkError

data class Strings(
    val tag: String,
    val localeName: String,
    val shares: String,
    val token: String,
    val chooseLanguage: String,
    val currentLanguage: String,
    val short: String,
    val buySell: Pair<String, String>,
    val networkError: (NetworkError) -> String
)

@LyricistStrings(languageTag = "en", default = true)
val EnStrings = Strings(
    tag = "en",
    localeName = "English",
    shares = "Shares",
    token = "Token",
    chooseLanguage = "Language",
    currentLanguage = "Current",
    short = "Short",
    buySell = "Buy" to "Sell",
    networkError = {
        when (it) {
            NetworkError.Server -> "Server Error"
            NetworkError.Credentials -> "Token is Invalid"
            NetworkError.Network -> "Network Error"
            NetworkError.Unknown -> "Something went wrong"
        }
    }
)

@LyricistStrings(languageTag = "ru")
val RuStrings = Strings(
    tag = "ru",
    localeName = "Русский",
    shares = "Акции",
    token = "Токен",
    chooseLanguage = "Язык",
    currentLanguage = "Текущий",
    short = "Шорт",
    buySell = "Купить" to "Продать",
    networkError = {
        when (it) {
            NetworkError.Server -> "Ошибка сервера"
            NetworkError.Credentials -> "Неверный токен"
            NetworkError.Network -> "Ошибка сети"
            NetworkError.Unknown -> "Что-то пошло не так"
        }
    }
)

enum class Localization(val strings: Strings) {
    EN(EnStrings), RU(RuStrings)
}
package org.yaabelozerov.investo

import androidx.compose.runtime.Composable
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.KotlinxSerializationJsonExtensionProvider
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.yaabelozerov.investo.domain.MainViewModel
import org.yaabelozerov.investo.domain.TinkoffRepository
import org.yaabelozerov.investo.network.ApiBaseUrl
import org.yaabelozerov.investo.network.TinkoffApi
import org.yaabelozerov.investo.network.TinkoffRepositoryImpl

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect object Net {
    val engine: HttpClientEngine
}

val appModule = module {
    single { HttpClient(Net.engine) {
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 2)
            exponentialDelay()
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
        defaultRequest {
            url(ApiBaseUrl.SANDBOX_BASE_URL.url)
            io.ktor.http.headers {
                append("Content-Type", "application/json")
                append("accept", "application/json")
            }
        }
        expectSuccess = true
    } }
    single { TinkoffApi(get(HttpClient::class)) }
    single<TinkoffRepository> { TinkoffRepositoryImpl(get()) }
    viewModelOf(::MainViewModel)
}

@Composable
expect fun isLayoutWide(): Boolean

expect class DecimalFormat() {
    fun format(num: Double, places: Int) : String
}

expect class LocaleMap() {
    fun currencyToSymbol(isoCode: String) : String
    fun countryToFlag(isoCode: String): String?
}
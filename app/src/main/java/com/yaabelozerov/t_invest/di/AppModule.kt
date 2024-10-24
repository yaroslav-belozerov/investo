package com.yaabelozerov.t_invest.di

import androidx.compose.ui.graphics.vector.addPathNodes
import com.yaabelozerov.t_invest.data.TinkoffRepositoryImpl
import com.yaabelozerov.t_invest.data.datastore.DataStoreManager
import com.yaabelozerov.t_invest.data.remote.TinkoffApi
import com.yaabelozerov.t_invest.domain.MainViewModel
import com.yaabelozerov.t_invest.domain.TinkoffRepository
import com.yaabelozerov.t_invest.util.ApiBaseUrl
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.headers
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.StringFormat
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.json.Json
import okhttp3.Headers
import okhttp3.internal.addHeaderLenient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

val appModule = module {
    single { HttpClient(OkHttp) {
        engine {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        }
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 5)
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
            headers {
                append("Content-Type", "application/json")
                append("accept", "application/json")
            }
        }
    } }
    single { TinkoffApi(get(HttpClient::class)) }
    single { DataStoreManager(androidContext()) }
    single<TinkoffRepository> { TinkoffRepositoryImpl(get()) }
    viewModelOf(::MainViewModel)
}
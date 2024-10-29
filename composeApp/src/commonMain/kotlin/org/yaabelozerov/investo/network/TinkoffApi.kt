package org.yaabelozerov.investo.network

import com.russhwolf.settings.Settings
import org.yaabelozerov.investo.network.model.CurrencyDTO
import org.yaabelozerov.investo.network.model.FindInstrumentDTO
import org.yaabelozerov.investo.network.model.FindInstrumentRequest
import org.yaabelozerov.investo.network.model.GetShareByRequest
import org.yaabelozerov.investo.network.model.InstrumentRequestBody
import org.yaabelozerov.investo.network.model.InstrumentStatus
import org.yaabelozerov.investo.network.model.InstrumentType
import org.yaabelozerov.investo.network.model.OrderBookDTO
import org.yaabelozerov.investo.network.model.OrderBookRequest
import org.yaabelozerov.investo.network.model.ShareDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.readBuffer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.io.decodeFromSource

enum class ApiBaseUrl(val url: String) {
    PROD_BASE_URL("https://invest-public-api.tinkoff.ru/rest/"),
    SANDBOX_BASE_URL("https://sandbox-invest-public-api.tinkoff.ru/rest/")
}

class TinkoffApi(private val client: HttpClient) {
    suspend fun getCurrencies(token: String, url: ApiBaseUrl): CurrencyDTO {
        return client.post(url.url + "tinkoff.public.invest.api.contract.v1.InstrumentsService/Currencies"){
            contentType(ContentType.Application.Json)
            setBody(InstrumentRequestBody(InstrumentStatus.INSTRUMENT_STATUS_UNSPECIFIED))
            headers {
                append("Authorization", "Bearer $token")
            }
        }.body()
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun getShares(token: String, url: ApiBaseUrl): ShareDTO {
        val buf =  client.post(url.url + "tinkoff.public.invest.api.contract.v1.InstrumentsService/Shares"){
            contentType(ContentType.Application.Json)
            headers {
                append("Authorization", "Bearer $token")
            }
            setBody(InstrumentRequestBody(InstrumentStatus.INSTRUMENT_STATUS_UNSPECIFIED))
        }.bodyAsChannel().readBuffer()
        return Json.decodeFromSource<ShareDTO>(buf)
    }

    suspend fun getOrderBook(request: OrderBookRequest, token: String, url: ApiBaseUrl): OrderBookDTO {
        return client.post(url.url + "tinkoff.public.invest.api.contract.v1.MarketDataService/GetOrderBook"){
            contentType(ContentType.Application.Json)
            setBody(request)
            headers {
                append("Authorization", "Bearer $token")
            }
        }.body()
    }

    suspend fun findShares(query: String, token: String, url: ApiBaseUrl): FindInstrumentDTO {
        return client.post(url.url + "tinkoff.public.invest.api.contract.v1.InstrumentsService/FindInstrument"){
            contentType(ContentType.Application.Json)
            setBody(FindInstrumentRequest(query, InstrumentType.INSTRUMENT_TYPE_SHARE, true))
            headers {
                append("Authorization", "Bearer $token")
            }
        }.body()
    }

    suspend fun getShareByFigi(req: GetShareByRequest, token: String, url: ApiBaseUrl): ShareDTO? {
        return try {
            client.post(url.url + "tinkoff.public.invest.api.contract.v1.InstrumentsService/ShareBy"){
                contentType(ContentType.Application.Json)
                setBody(req)
                headers {
                    append("Authorization", "Bearer $token")
                }
            }.body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

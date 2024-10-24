package com.yaabelozerov.t_invest.data.remote

import com.yaabelozerov.t_invest.data.remote.model.CurrencyDTO
import com.yaabelozerov.t_invest.data.remote.model.FindInstrumentDTO
import com.yaabelozerov.t_invest.data.remote.model.FindInstrumentRequest
import com.yaabelozerov.t_invest.data.remote.model.GetShareByRequest
import com.yaabelozerov.t_invest.data.remote.model.InstrumentRequestBody
import com.yaabelozerov.t_invest.data.remote.model.InstrumentStatus
import com.yaabelozerov.t_invest.data.remote.model.InstrumentType
import com.yaabelozerov.t_invest.data.remote.model.OrderBookDTO
import com.yaabelozerov.t_invest.data.remote.model.OrderBookRequest
import com.yaabelozerov.t_invest.data.remote.model.ShareDTO
import com.yaabelozerov.t_invest.util.ApiBaseUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

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
        }.bodyAsChannel()
        return Json.decodeFromStream<ShareDTO>(buf.toInputStream())
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
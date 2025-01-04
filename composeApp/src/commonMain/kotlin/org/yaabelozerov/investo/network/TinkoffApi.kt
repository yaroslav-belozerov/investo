package org.yaabelozerov.investo.network

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
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

enum class ApiBaseUrl(val url: String) {
    PROD_BASE_URL("https://invest-public-api.tinkoff.ru/rest/"), SANDBOX_BASE_URL("https://sandbox-invest-public-api.tinkoff.ru/rest/")
}

class TinkoffApi(private val client: HttpClient) {
    suspend fun getCurrencies(token: String, url: ApiBaseUrl): Result<CurrencyDTO> =
        client.post(url.url + "tinkoff.public.invest.api.contract.v1.InstrumentsService/Currencies") {
            contentType(ContentType.Application.Json)
            setBody(InstrumentRequestBody(InstrumentStatus.INSTRUMENT_STATUS_UNSPECIFIED))
            headers {
                append("Authorization", "Bearer $token")
            }
        }.wrap()

    suspend fun getOrderBook(
        request: OrderBookRequest, token: String, url: ApiBaseUrl
    ): Result<OrderBookDTO> =
        client.post(url.url + "tinkoff.public.invest.api.contract.v1.MarketDataService/GetOrderBook") {
            contentType(ContentType.Application.Json)
            setBody(request)
            headers {
                append("Authorization", "Bearer $token")
            }
        }.wrap()

    suspend fun findShares(
        query: String, token: String, url: ApiBaseUrl
    ): Result<FindInstrumentDTO> =
        client.post(url.url + "tinkoff.public.invest.api.contract.v1.InstrumentsService/FindInstrument") {
            contentType(ContentType.Application.Json)
            setBody(
                FindInstrumentRequest(
                    query, InstrumentType.INSTRUMENT_TYPE_SHARE, true
                )
            )
            headers {
                append("Authorization", "Bearer $token")
            }
        }.wrap()

    suspend fun getShareByFigi(
        req: GetShareByRequest, token: String, url: ApiBaseUrl
    ): Result<ShareDTO> =
        client.post(url.url + "tinkoff.public.invest.api.contract.v1.InstrumentsService/ShareBy") {
            contentType(ContentType.Application.Json)
            setBody(req)
            headers {
                append("Authorization", "Bearer $token")
            }
        }.wrap()
}

private suspend inline fun <reified T> HttpResponse.wrap(): Result<T> {
    return if (status != HttpStatusCode.OK) {
        Result.failure(
            Throwable(
                "Http Error, status code $status"
            )
        )
    } else {
        try {
            Result.success(this.body<T>())
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}

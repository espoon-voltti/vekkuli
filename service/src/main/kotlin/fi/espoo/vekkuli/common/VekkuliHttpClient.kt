package fi.espoo.vekkuli.common

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.headers
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class VekkuliHttpClient {
    companion object {
        suspend fun makePostRequest(
            url: String,
            bodyAsJSONString: String,
            headers: Map<String, String> = emptyMap(),
        ): HttpResponse {
            val client =
                HttpClient(CIO) {
                    expectSuccess = true
                    install(ContentNegotiation) {
                        json(
                            Json {
                                ignoreUnknownKeys = true
                                prettyPrint = true
                                encodeDefaults = false
                            }
                        )
                    }
                }

            val response =
                client.request(url) {
                    method = HttpMethod.Post
                    headers {
                        headers.forEach { (key, value) ->
                            append(key, value)
                        }
                    }
                    // We need to use byte array to prevent Ktor from double encoding the JSON
                    setBody(ByteArrayContent(bodyAsJSONString.toByteArray(), contentType = ContentType.Application.Json))
                }
            client.close()
            return response
        }

        suspend fun makeGetRequest(
            url: String,
            headers: Map<String, String> = emptyMap(),
        ): HttpResponse {
            val client =
                HttpClient(CIO) {
                    expectSuccess = true
                }
            val response =
                client.get(url) {
                    headers {
                        headers.forEach { (key, value) ->
                            append(key, value)
                        }
                    }
                }
            client.close()
            return response
        }
    }
}

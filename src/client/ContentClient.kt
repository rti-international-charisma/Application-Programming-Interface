package com.rti.charisma.api.client

import com.fasterxml.jackson.databind.SerializationFeature
import com.rti.charisma.api.config.ACCESS_TOKEN
import com.rti.charisma.api.config.CMS_BASE_URL
import com.rti.charisma.api.config.ConfigProvider
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.text.DateFormat

class ContentClient {
    private val accessToken = ConfigProvider.get(ACCESS_TOKEN)
    private val baseUrl = ConfigProvider.get(CMS_BASE_URL)


    private val client: HttpClient =
        HttpClient(Apache) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.HEADERS
            }

            install(JsonFeature) {
                serializer = JacksonSerializer() {
                    enable(SerializationFeature.INDENT_OUTPUT)
                    dateFormat = DateFormat.getDateInstance()
                }
            }
            engine {
                // this: ApacheEngineConfig
                followRedirects = true
                socketTimeout = 10_000
                connectTimeout = 10_000
                connectionRequestTimeout = 20_000
                customizeClient {
                    // this: HttpAsyncClientBuilder
                    setMaxConnTotal(1000)
                    setMaxConnPerRoute(100)
                    //
                }
                customizeRequest {
                    // this: RequestConfig.Builder
                }
            }
        }

    suspend fun request(endpoint: String): String {
        val cmsResponse = client.request<String> {
            url("$baseUrl${endpoint}")
            method = HttpMethod.Get
            header("Authorization", "Bearer $accessToken")
        }
        client.close();
        return cmsResponse
    }
}
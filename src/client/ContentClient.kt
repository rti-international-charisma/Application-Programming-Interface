package com.rti.charisma.api.client

import com.fasterxml.jackson.databind.SerializationFeature
import com.rti.charisma.api.config.ACCESS_TOKEN
import com.rti.charisma.api.config.CMS_BASE_URL
import com.rti.charisma.api.config.ConfigProvider
import com.rti.charisma.api.exception.ContentException
import com.rti.charisma.api.exception.ContentRequestException
import com.rti.charisma.api.model.Assessment
import com.rti.charisma.api.model.PageContent
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.text.DateFormat


class ContentClient {
    val accessToken = ConfigProvider.get(ACCESS_TOKEN)
    val baseUrl = ConfigProvider.get(CMS_BASE_URL)

    val client: HttpClient =
        HttpClient(Apache) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.HEADERS
            }

            install(JsonFeature) {
                serializer = JacksonSerializer {
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

    suspend fun getPage(endpoint: String): PageContent {
        /* This completes the job and throws
            kotlinx.coroutines.JobCancellationException: Parent job is Completed
                   client.close()
         */
        try {
            return client.request {
                url("$baseUrl${endpoint}")
                method = HttpMethod.Get
                header("Authorization", "Bearer $accessToken")
            }
        } catch (e: ClientRequestException) {
            throw ContentRequestException("Failed to fetch content, ${e.message}}")
        } catch (e: ServerResponseException) {
            throw ContentException("Error while fetching content from server")
        } catch (e: Exception) {
            throw ContentException("Unexpected error while fetching content from server")
        }

    }

    suspend fun getAssessment(endpoint: String): Assessment {
        try {
            return client.request {
                url("$baseUrl${endpoint}")
                method = HttpMethod.Get
                header("Authorization", "Bearer $accessToken")
            }
        } catch (e: ClientRequestException) {
            throw ContentRequestException("Failed to fetch content, ${e.message}}")
        } catch (e: ServerResponseException) {
            throw ContentException("Error while fetching content from server")
        } catch (e: Exception) {
            throw ContentException("Unexpected error while fetching content from server")
        }

    }

    suspend fun getAsset(endpoint: String): ByteArray {
        return client.request {
            url("$baseUrl${endpoint}")
            method = HttpMethod.Get
            header("Authorization", "Bearer $accessToken")
        }
    }
}





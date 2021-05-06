package com.rti.charisma.api.client

import com.fasterxml.jackson.databind.SerializationFeature
import com.rti.charisma.api.config.ACCESS_TOKEN
import com.rti.charisma.api.config.CMS_BASE_URL
import com.rti.charisma.api.config.ConfigProvider
import com.rti.charisma.api.content.Assessment
import com.rti.charisma.api.content.PageContent
import com.rti.charisma.api.exception.ContentException
import com.rti.charisma.api.exception.ContentRequestException
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.slf4j.LoggerFactory
import java.text.DateFormat

class ContentClient {
    private val accessToken = ConfigProvider.get(ACCESS_TOKEN)
    private val baseUrl = ConfigProvider.get(CMS_BASE_URL)
    private val logger = LoggerFactory.getLogger(ContentClient::class.java)

    private val client: HttpClient =
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
                followRedirects = true
                socketTimeout = 10_000
                connectTimeout = 10_000
                connectionRequestTimeout = 20_000
                customizeClient {
                    setMaxConnTotal(1000)
                    setMaxConnPerRoute(100)
                    //
                }
                customizeRequest {
                }
            }
        }

    suspend fun getPage(endpoint: String): PageContent {
        /* This completes the job and throws
            kotlinx.coroutines.JobCancellationException: Parent job is Completed
                   client.close()
         */
        logger.info("Sending request to cms, '$endpoint'")
        try {
            return client.request {
                url("$baseUrl${endpoint}")
                method = HttpMethod.Get
                header("Authorization", "Bearer $accessToken")
            }
        } catch (e: ClientRequestException) {
            logger.warn("Bad request for, '$endpoint', ${e.localizedMessage}")
            throw ContentRequestException("Failed to fetch content, ${e.localizedMessage}}")
        } catch (e: ServerResponseException) {
            logger.warn("CMS failed to process request, '$endpoint', ${e.localizedMessage}")
            throw ContentException("Failed while fetching content from server", e)
        } catch (e: Exception) {
            logger.error("Unexpected failure for, '$endpoint', ${e.stackTrace}")
            throw ContentException("Unexpected failure while fetching content from server", e)
        }
    }

    suspend fun getAssessment(endpoint: String): Assessment {
        logger.info("Sending request to cms, '$endpoint'")

        try {
            return client.request {
                url("$baseUrl${endpoint}")
                method = HttpMethod.Get
                header("Authorization", "Bearer $accessToken")
            }
        }  catch (e: ClientRequestException) {
            logger.warn("Bad request for assessment, '$endpoint', ${e.localizedMessage}")
            throw ContentRequestException("Failed to fetch content, ${e.localizedMessage}}")
        } catch (e: ServerResponseException) {
            logger.warn("CMS failed to process assessment request, '$endpoint', ${e.localizedMessage}")
            throw ContentException("Failed while fetching content from server", e)
        } catch (e: Exception) {
            logger.error("Unexpected failure for, '$endpoint', ${e.stackTrace}")
            throw ContentException("Unexpected failure while fetching assessment content from server", e)
        }

    }

    suspend fun getAsset(endpoint: String): ByteArray {
        logger.info("Sending request to cms, '$endpoint'")
        try{
            return client.request {
                url("$baseUrl${endpoint}")
                method = HttpMethod.Get
                header("Authorization", "Bearer $accessToken")
            }
        } catch (e: ClientRequestException) {
            logger.warn("Bad request for asset, '$endpoint', ${e.localizedMessage}")
            throw ContentRequestException("Failed to fetch asset, ${e.localizedMessage}}")
        } catch (e: ServerResponseException) {
            logger.warn("CMS failed to process asset request, '$endpoint', ${e.localizedMessage}")
            throw ContentException("Failed while fetching asset from server", e)
        } catch (e: Exception) {
            logger.error("Unexpected failure for, '$endpoint', ${e.stackTrace}")
            throw ContentException("Unexpected failure while fetching asset content from server", e)
        }
    }
}





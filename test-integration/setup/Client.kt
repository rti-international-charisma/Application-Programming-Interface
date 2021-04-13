package setup

import io.ktor.client.*
import io.ktor.client.engine.mock.*


val client = HttpClient(MockEngine) {
    engine {
        addHandler { request ->
            when (request.url.fullUrl) {
                "${homePageUrl}" -> {
                    val responseHeaders = headers("Content-Type" to listOf(ContentType.Applicatio .toString()))
                    respond("Hello, world", headers = responseHeaders)
                }
                else -> error("Unhandled ${request.url.fullUrl}")
            }
        }
    }
}

private val homePageUrl: String = "http://chari-loadb-150mi7h76f40q-0c42746b9ba8f8ab.elb.ap-south-1.amazonaws.com:8055/homepage"
private val Url.hostWithPortIfRequired: String get() = if (port == protocol.defaultPort) host else hostWithPort
private val Url.fullUrl: String get() = "${protocol.name}://$hostWithPortIfRequired$fullPath"
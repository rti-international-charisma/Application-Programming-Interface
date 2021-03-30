package service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.rti.charisma.api.client.ContentClient
import com.rti.charisma.api.exception.ContentNotFoundException
import com.rti.charisma.api.route.HomePage


class ContentService(private val contentClient: ContentClient) {

     suspend fun getHomePage(): HomePage {
        try {
            val response = contentClient.request("/items/homepage?fields=*.*")
            return jacksonObjectMapper().readValue(response)

        } catch (e: Exception) {
            throw ContentNotFoundException(e.stackTraceToString());
        }
    }

}
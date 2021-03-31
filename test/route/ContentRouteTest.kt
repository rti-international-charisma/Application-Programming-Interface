package com.rti.charisma.api.route

import com.rti.charisma.api.commonModule
import com.rti.charisma.api.contentModule
import com.rti.charisma.api.exception.ContentNotFoundException
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import service.ContentService

class ContentRouteTest {
    private val contentService = mockk<ContentService>(relaxed = true)

    @Test
    fun `it should return 200 OK with json response`() = testApp {
        coEvery { contentService.getHomePage() } returns HomePage(data = mutableMapOf("title" to "test-title", "image" to "url"))
        handleRequest(HttpMethod.Get, "/homepage") {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertNotNull(response.content)
        }
    }

    @Test
    fun `it should throw internal server error if error while fetching content`() = testApp {
        coEvery { contentService.getHomePage() } throws ContentNotFoundException("some error")

        handleRequest(HttpMethod.Get, "/homepage") {
        }.apply {
            assertEquals(500, response.status()?.value)
        }
    }

    @Test
    fun `it should return 404 not found error for incorrect request`() = testApp {
        handleRequest(HttpMethod.Get, "/hmpage") {
        }.apply {
            assertEquals(404, response.status()?.value)
            
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit){
        return withTestApplication({
            commonModule()
            contentModule(contentService)
        }){ callback()}
    }
}
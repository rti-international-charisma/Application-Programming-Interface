package route

import com.contentful.java.cda.CDAClient
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rti.charisma.api.Signup
import com.rti.charisma.api.mainWithDependencies
import com.rti.charisma.api.service.UserService
import io.ktor.http.HttpMethod
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import service.ContentService

class UserRouteTest {
    private val contentClient = mockk<CDAClient>(relaxed = true)
    private val contentService = mockk<ContentService>(relaxed = true)
    private val userService = mockk<UserService>(relaxed = true)

    @Test
    fun `it should return 200 OK when user is registered successfully`() = testApp {
        val signupModel = Signup("username1", "password", 1, "sec q answer")
        handleRequest(HttpMethod.Post, "/signup") {
            setBody(jacksonObjectMapper().writeValueAsString(signupModel))
        }.apply {
            assertEquals(200, response.status()?.value)
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            mainWithDependencies(
                    contentClient,
                    contentService,
                    userService
            )
        }){ callback()}
    }
}
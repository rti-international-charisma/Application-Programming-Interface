package route

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rti.charisma.api.loginModule
import com.rti.charisma.api.commonModule
import com.rti.charisma.api.route.Signup
import com.rti.charisma.api.service.UserService
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.HttpMethod
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import javax.sql.DataSource

class UserRouteTest {
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

    @Test
    fun `it should return all Security Questions `() = testApp {
        handleRequest(HttpMethod.Get, "/securityquestions/") {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertNotNull(response.content)
        }
    }

    @Test
    fun `it should return Security Questions by id`() = testApp {
        handleRequest(HttpMethod.Get, "/securityquestions/1") {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertNotNull(response.content)
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit){
        return withTestApplication({
            commonModule()
            loginModule(
                    inMemoryDataSoure(),
                    userService
            )
        }){ callback()}
    }

    private fun inMemoryDataSoure(): DataSource {
        val config = HikariConfig()
        config.jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
        config.driverClassName = "org.h2.Driver"
        config.validate()
        return HikariDataSource(config)
    }
}
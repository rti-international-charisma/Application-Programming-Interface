package route

import com.rti.charisma.api.commonModule
import com.rti.charisma.api.db.tables.User
import com.rti.charisma.api.fixtures.AssessmentFixture
import com.rti.charisma.api.loginModule
import com.rti.charisma.api.route.response.AssessmentScoreResponse
import com.rti.charisma.api.service.AssessmentService
import com.rti.charisma.api.service.JWTService
import com.rti.charisma.api.service.UserService
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.sql.DataSource

class AssessmentRouteTest {
    private lateinit var testUser: User
    private val userService = mockk<UserService>(relaxed = true)
    private val assessmentService = mockk<AssessmentService>(relaxed = true)

    @BeforeEach
    fun setup() {
        testUser = User(
            1,
            "username",
            1,
            5,
            5,
            "hashedAnswer",
            "hashedPassword"
        )
    }

    @Test
    fun `it should return 201 Created when user scores are added successfully`() = testApp {
        every { userService.findUserById(1) } returns testUser

        handleRequest(HttpMethod.Post, "assessment/scores") {
            addHeader("Authorization", "Bearer ${getToken()}")
            setBody(AssessmentFixture.assessmentScoreJson())
        }.apply {
            assertEquals(201, response.status()?.value)
        }
    }

    @Test
    fun `it should return 401 Unauthorised when request is received with no token`() = testApp {
        handleRequest(HttpMethod.Post, "assessment/scores") {
            setBody(AssessmentFixture.assessmentScoreJson())
        }.apply {
            assertEquals(401, response.status()?.value)
        }
        verify(verifyBlock = { userService.findUserById(any()) }, exactly = 0)

    }

    @Test
    fun `it should return 401 if user id not found while adding scores`() = testApp {
        every { userService.findUserById(1) } throws RuntimeException()

        handleRequest(HttpMethod.Post, "assessment/scores") {
            addHeader("Authorization", "Bearer ${getToken()}")
            setBody(AssessmentFixture.assessmentScoreJson())
        }.apply {
            assertEquals(401, response.status()?.value)
        }
        verify(verifyBlock = { assessmentService.addAssessmentScore(any(), any()) }, exactly = 0)
    }

    @Test
    fun `it should return 200 with user assessment result`() = testApp {
        every { userService.findUserById(1) } returns testUser
        every { assessmentService.getAssessmentScore(any()) } returns AssessmentFixture.assessmentResult()

        handleRequest(HttpMethod.Get, "assessment/scores") {
            addHeader("Authorization", "Bearer ${getToken()}")
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())
            assertEquals(AssessmentFixture.assessmentScoreJson(), response.content)
        }
    }

    @Test
    fun `it should return 200 with empty result`() = testApp {
        every { userService.findUserById(1) } returns testUser
        every { assessmentService.getAssessmentScore(any()) } returns AssessmentScoreResponse(emptyList())
        handleRequest(HttpMethod.Get, "assessment/scores") {
            addHeader("Authorization", "Bearer ${getToken()}")
        }.apply {
            assertEquals(200, response.status()?.value)
            assertEquals("application/json; charset=UTF-8", response.contentType().toString())

            assertEquals(emptyResponse(), response.content)
        }
    }


    @Test
    fun `it should return 500 when finding scores throws exception`() = testApp {
        every { userService.findUserById(1) } returns testUser
        every { assessmentService.getAssessmentScore(any()) } throws RuntimeException()

        handleRequest(HttpMethod.Get, "assessment/scores") {
            addHeader("Authorization", "Bearer ${getToken()}")
        }.apply {
            assertEquals(500, response.status()?.value)
        }
    }

    @Test
    fun `it should return 401 if user id not found while fetching scores`() = testApp {
        every { userService.findUserById(1) } throws RuntimeException()

        handleRequest(HttpMethod.Get, "assessment/scores") {
            addHeader("Authorization", "Bearer ${getToken()}")

        }.apply {
            assertEquals(401, response.status()?.value)
        }
        verify(verifyBlock = { assessmentService.getAssessmentScore(any()) }, exactly = 0)
    }

    @Test
    fun `it should return 401 if no token found in request while fetching scores`() = testApp {
        handleRequest(HttpMethod.Get, "assessment/scores") {
        }.apply {
            assertEquals(401, response.status()?.value)
        }
        verify(verifyBlock = { assessmentService.getAssessmentScore(any()) }, exactly = 0)
        verify(verifyBlock = { userService.findUserById(any()) }, exactly = 0)
    }

    private fun getToken(): String {
        return JWTService.generateToken(testUser)
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        return withTestApplication({
            commonModule()
            loginModule(
                inMemoryDataSoure(),
                userService,
                assessmentService
            )
        }) { callback() }
    }

    private fun inMemoryDataSoure(): DataSource {
        val config = HikariConfig()
        config.jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
        config.driverClassName = "org.h2.Driver"
        config.validate()
        return HikariDataSource(config)
    }


    private fun emptyResponse(): String {
        return """{
  "sections" : [ ]
}"""
    }
}
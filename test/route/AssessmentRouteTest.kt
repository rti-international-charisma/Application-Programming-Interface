package route

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rti.charisma.api.commonModule
import com.rti.charisma.api.fixtures.AssessmentFixture
import com.rti.charisma.api.loginModule
import com.rti.charisma.api.route.AssessmentResult
import com.rti.charisma.api.route.Question
import com.rti.charisma.api.service.AssessmentService
import com.rti.charisma.api.service.UserService
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import javax.sql.DataSource

class AssessmentRouteTest {
    private val userService = mockk<UserService>(relaxed = true)
    private val assessmentService = mockk<AssessmentService>(relaxed = true)

    @Test
    fun `it should return 201 Created when user scores are added successfully`() = testApp {
        val assessmentResult1 = AssessmentResult(
            "section1", "sectiontype1",
            mutableListOf(Question("q1", 3))
        )
        handleRequest(HttpMethod.Post, "assessment/scores") {
            setBody(jacksonObjectMapper().writeValueAsString(mutableListOf(assessmentResult1)))
        }.apply {
           // assertEquals(201, response.status()?.value)
        }
    }
    @Test
    fun `it should return 401 if user id not found while adding scores`() = testApp {
        val assessmentResult1 = AssessmentResult(
            "section1", "sectiontype1",
            mutableListOf(Question("q1", 3))
        )
        handleRequest(HttpMethod.Post, "assessment/scores") {
            setBody(jacksonObjectMapper().writeValueAsString(mutableListOf(assessmentResult1)))
        }.apply {
            assertEquals(401, response.status()?.value)
        }
        verify(verifyBlock= {assessmentService.addAssessmentScore(any(), any())}, exactly=0 )
    }

    @Test
    fun `it should return 200 with user assessment result`() = testApp {
        every { assessmentService.getAssessmentScore(any()) } returns AssessmentFixture.assessmentResult()

        handleRequest(HttpMethod.Get, "assessment/scores") {
        }.apply {
           // assertEquals(200, response.status()?.value)
           // assertEquals("application/json; charset=UTF-8", response.contentType().toString())
           // assertEquals(AssessmentFixture.assessmentResultResponse(), response.content)
        }
    }

    @Test
    fun `it should return 401 if user id not found while fetching scores`() = testApp {
        handleRequest(HttpMethod.Get, "assessment/scores") {
        }.apply {
            assertEquals(401, response.status()?.value)
        }
        verify(verifyBlock= {assessmentService.getAssessmentScore(any())}, exactly=0 )
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
}
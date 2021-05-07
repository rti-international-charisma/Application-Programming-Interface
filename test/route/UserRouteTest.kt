package route

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.rti.charisma.api.route.response.ErrorResponse
import com.rti.charisma.api.loginModule
import com.rti.charisma.api.commonModule
import com.rti.charisma.api.db.tables.User
import com.rti.charisma.api.exception.LoginAttemptsExhaustedException
import com.rti.charisma.api.exception.LoginException
import com.rti.charisma.api.route.response.UserResponse
import com.rti.charisma.api.route.Login
import com.rti.charisma.api.route.Signup
import com.rti.charisma.api.route.VerifySecQuestion
import com.rti.charisma.api.service.AssessmentService
import com.rti.charisma.api.service.JWTService
import com.rti.charisma.api.service.UserService
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.HttpMethod
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import javax.sql.DataSource

class UserRouteTest {
    private val userService = mockk<UserService>(relaxed = true)
    private val assessmentService = mockk<AssessmentService>(relaxed = true)

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

    @Test
    fun `it should login user`() = testApp {
        val loginModel = Login("username", "password")
        every { userService.login(loginModel) } returns UserResponse(User(1, "username", password = "hashedPassword", loginAttemptsLeft = 5, resetPasswordAttemptsLeft = 5, sec_answer = "hashedAnswer"), "jwt-token")
        handleRequest(HttpMethod.Post, "/login") {
            setBody(jacksonObjectMapper().writeValueAsString(loginModel))
        }.apply {
            assertEquals(200, response.status()?.value)
        }
    }

    @Test
    fun `it should return 401 when login user with incorrect password`() = testApp {
        val loginModel = Login("username", "password")

        every { userService.login(loginModel) } throws LoginException("Username and password do not match. You have 4 Login attempts left.")

        handleRequest(HttpMethod.Post, "/login") {
            setBody(jacksonObjectMapper().writeValueAsString(loginModel))
        }.apply {
            assertEquals(401, response.status()?.value)
            val errorResponse = jacksonObjectMapper().readValue<ErrorResponse>(response.content!!)
            assertEquals("Username and password do not match. You have 4 Login attempts left.", errorResponse.body)
        }
    }

    @Test
    fun `it should return 401 LoginAttemptsExhausted when login user with incorrect password more than 5 times`() = testApp {
        val loginModel = Login("username", "password")

        every { userService.login(loginModel) } throws LoginAttemptsExhaustedException()

        handleRequest(HttpMethod.Post, "/login") {
            setBody(jacksonObjectMapper().writeValueAsString(loginModel))
        }.apply {
            assertEquals(401, response.status()?.value)
            val errorResponse = jacksonObjectMapper().readValue<ErrorResponse>(response.content!!)
            assertEquals("Reset Password", errorResponse.body)
        }
    }

    @Test
    fun `it should return 200 when request is correct`() = testApp {
        every { userService.isUsernameAvailable("username") } returns true

        handleRequest(HttpMethod.Get, "/user/availability/username") {
        }.apply {
            assertEquals(200, response.status()?.value)
            assertNotNull(response.content)
        }
    }

    @Test
    fun `it should return 400 if username is absent`() = testApp {
        every { userService.isUsernameAvailable("username") } returns false

        handleRequest(HttpMethod.Get, "/user/availability/") {
        }.apply {
            assertEquals(400, response.status()?.value)
        }
    }

    @Test
    fun `it should return 200 verify security question`() = testApp {
        val verifySecQuestion = VerifySecQuestion("username", 1, "SecAnswer")

        every { userService.verifySecurityQuestion(verifySecQuestion) } returns UserResponse(User(
                1,
                "username",
                password = "hashedPassword",
                loginAttemptsLeft = 5,
                resetPasswordAttemptsLeft = 5,
                sec_answer = "hashedAnswer"
        ), resetPasswordToken = "reset-password-jwt-token")

        handleRequest(HttpMethod.Post, "/verify-securityquestion") {
            setBody(jacksonObjectMapper().writeValueAsString(verifySecQuestion))
        }.apply {
            assertEquals(200, response.status()?.value)
        }
    }

    @Test
    fun `reset-password should throw 401 if token is invalid`() = testApp {
        every { userService.findUserById(1) } returns User(1, "username", 1, 5, 5, "hashedAnswer", "hashedPassword")
        handleRequest(HttpMethod.Post, "/reset-password") {
                addHeader("Authorization", "invalid token")
        }.apply {
            assertEquals(401, response.status()?.value)
        }
    }

    @Test
    fun `reset-password should throw 401 if token is expired`() = testApp {
        every { userService.findUserById(1) } returns User(1, "username", 1, 5, 5, "hashedAnswer", "hashedPassword")
        handleRequest(HttpMethod.Post, "/reset-password") {
            addHeader("Authorization", "Bearer ${getExpiredToken()}")
        }.apply {
            assertEquals(401, response.status()?.value)
        }
    }

    @Test
    fun `reset-password should return 200 for valid token`() = testApp {
        every { userService.findUserById(1) } returns User(1, "username", 1, 5, 5, "hashedAnswer", "hashedPassword")
        handleRequest(HttpMethod.Post, "/reset-password") {
            addHeader("Authorization", "Bearer ${getToken()}")
            setBody("""{
                         "newPassword": "newpassword"
                        }"""
            )
        }.apply {
            assertEquals(200, response.status()?.value)
        }
    }

    private fun getToken(): String {
        val user = User(1, "username", 1, 5, 5, "hashedAnswer", "hashedPassword")
        return JWTService.generateResetPasswordToken(user)
    }

    private fun getExpiredToken() = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJSZXNldFBhc3N3b3JkIiwiaXNzIjoiQ2hhcmlzbWFBcGlSZXNldFBhc3N3b3JkIiwiaWQiOjEsImV4cCI6MTYyMDMyMjk4Nn0.ks2RwJsl0ZinLc0qRk1g2Zyd-Is5SWOPXTd2CYfHkCqr5qY6nV6MPx_OYGwO2CwvGgyKPx8lR_bQddWpV2fkCQ"

    private fun testApp(callback: TestApplicationEngine.() -> Unit){
        return withTestApplication({
            commonModule()
            loginModule(
                inMemoryDataSoure(),
                userService,
                assessmentService
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
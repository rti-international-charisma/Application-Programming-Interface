package service

import com.rti.charisma.api.db.tables.SecurityQuestion
import com.rti.charisma.api.db.tables.User
import com.rti.charisma.api.exception.SecurityQuestionException
import com.rti.charisma.api.exception.UserAlreadyExistException
import com.rti.charisma.api.exception.LoginException
import com.rti.charisma.api.repository.UserRepository
import com.rti.charisma.api.route.Login
import com.rti.charisma.api.route.Signup
import com.rti.charisma.api.service.JWTService
import com.rti.charisma.api.service.UserService
import com.rti.charisma.api.util.hash
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

class UserServiceTest {

    private lateinit var userService: UserService
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val mockJWTService = mockk<JWTService>(relaxed = true)

    @BeforeEach
    fun setup() {
        userService = UserService(userRepository, mockJWTService)
    }

    @Test
    fun `it should throw UserAlreadyExistException when user with username is already present`() {
        val signupModel = Signup("someusername", "password", 1, "security question answer")
        every { userRepository.doesUserExist("someusername") } returns true
        assertFailsWith(UserAlreadyExistException::class) {
            userService.registerUser(signupModel)
        }
    }

    @Test
    fun `it should add user`() {
        val signupModel = Signup("someusername", "password", 1, "security question answer")
        every { userRepository.getSecurityQuestions(any()) } returns listOf(SecurityQuestion(1, "question"))
        every { userRepository.doesUserExist(signupModel.username) } returns false
        every { userRepository.registerUser(signupModel) } returns 1

        val registeredUserId = userService.registerUser(signupModel)

        verify { userRepository.registerUser(signupModel) }
        assertEquals(1, registeredUserId)
    }

    @Test
    fun `it should return all security questions`() {
        val qId = null
        every { userRepository.getSecurityQuestions(null) } returns listOf(SecurityQuestion(1, "question1"), SecurityQuestion(2, "question2"))

        val securityQuestions = userService.getSecurityQuestions(qId)

        verify { userRepository.getSecurityQuestions(null) }
        assertNotNull(securityQuestions)
    }

    @Test
    fun `it should throw error while registering if security question is absent`() {
        val qId = 89
        every { userRepository.getSecurityQuestions(qId) } returns listOf()
        assertFailsWith(SecurityQuestionException::class) {
            userService.getSecurityQuestions(qId)
        }
    }

    @Test
    fun `it should return security question by id`() {
        val qId = 1
        every { userRepository.getSecurityQuestions(qId) } returns listOf(SecurityQuestion(1, "question1"))

        val securityQuestions = userService.getSecurityQuestions(qId)

        verify { userRepository.getSecurityQuestions(qId) }
        assertNotNull(securityQuestions)
        assertEquals(1, securityQuestions.size)
    }

    @Test
    fun `it should throw error if security question by id sis absent`() {
        val qId = 30
        every { userRepository.getSecurityQuestions(qId) } returns listOf()

        assertFailsWith(SecurityQuestionException::class) {
            userService.getSecurityQuestions(qId)
        }
    }

    @Test
    fun `it should throw error while login if the user does not exist`() {
        val loginModel = Login("username", "password")

        every { userRepository.findUserByUsername("username") } returns null

        assertFailsWith(LoginException::class) {
            userService.login(loginModel)
        }
    }

    @Test
    fun `it should throw error while login if the credentials do not match`() {
        val password = "password"
        val loginModel = Login("username", password)

        every { userRepository.findUserByUsername(loginModel.username) } returns User(1, "username", password = "hashedPassword")

        val exception = assertFailsWith(LoginException::class) {
            userService.login(loginModel)
        }

        assertEquals("Username and password do not match", exception.localizedMessage)
    }

    @Test
    fun `it should generate token when credentials are correct`() {
        val password = "password"
        val loginModel = Login("username", password)
        val hashedPassword = password.hash()
        val user = User(1, "username", password = hashedPassword)

        every { userRepository.findUserByUsername(loginModel.username) } returns user

        val userResponse = userService.login(loginModel)

        verify { mockJWTService.generateToken(user) }
        assertNotNull(userResponse)
        assertNotNull(userResponse.token)
    }

    @Test
    fun `it should return true when user with username is present` () {
        every { userRepository.findUserByUsername("username") } returns  User(1, "username", 1, "hashed")

        val users = userService.findUsersByUsername("username")

        assertTrue(users)
    }

    @Test
    fun `it should return false when user with username is present` () {
        every { userRepository.findUserByUsername("username") } returns null

        val users = userService.findUsersByUsername("username")

        assertFalse(users)
    }
}
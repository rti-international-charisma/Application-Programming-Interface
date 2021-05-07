package service

import com.rti.charisma.api.db.tables.SecurityQuestion
import com.rti.charisma.api.db.tables.User
import com.rti.charisma.api.exception.*
import com.rti.charisma.api.repository.UserRepository
import com.rti.charisma.api.route.Login
import com.rti.charisma.api.route.Signup
import com.rti.charisma.api.route.VerifySecQuestion
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
        every { userRepository.registerUser(signupModel, 5, 5) } returns 1

        val registeredUserId = userService.registerUser(signupModel)

        verify { userRepository.registerUser(signupModel, 5, 5) }
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

        every { userRepository.findUserByUsername(loginModel.username) } returns User(1, "username", password = "hashedPassword", loginAttemptsLeft = 5, sec_answer = "hashedAnswer", resetPasswordAttemptsLeft = 5)

        val exception = assertFailsWith(LoginException::class) {
            userService.login(loginModel)
        }

        assertEquals("Username and password do not match. You have 4 Login attempts left.", exception.localizedMessage)
    }

    @Test
    fun `it should generate token when credentials are correct`() {
        val password = "password"
        val loginModel = Login("username", password)
        val hashedPassword = password.hash()
        val user = User(1, "username", password = hashedPassword, loginAttemptsLeft = 5, sec_answer = "hashedAnswer", resetPasswordAttemptsLeft = 5)

        every { userRepository.findUserByUsername(loginModel.username) } returns user

        val userResponse = userService.login(loginModel)

        verify { mockJWTService.generateToken(user) }
        assertNotNull(userResponse)
        assertNotNull(userResponse.token)
    }

    @Test
    fun `it should return false when user with username is present` () {
        every { userRepository.findUserByUsername("username") } returns  User(1, "username", 1, 5, 5, "hashedAnswer", "hashed")

        val users = userService.isUsernameAvailable("username")

        assertFalse(users)
    }

    @Test
    fun `it should return true when user with username is absent` () {
        every { userRepository.findUserByUsername("username") } returns null

        val users = userService.isUsernameAvailable("username")

        assertTrue(users)
    }

    @Test
    fun `it should return error if 0 login attempts are left with user while login` () {
        every { userRepository.findUserByUsername("username") } returns User(1, "username", 1, 0, 5,"hashedAnswer",  "hashed")

        val login = Login("username", "password")
        assertFailsWith(LoginAttemptsExhaustedException::class) {
            userService.login(login)
        }
    }

    @Test
    fun `it should reduce login attempts and return error if attempts left are not zero` () {
        val user = User(1, "username", 1, 5, 5,"hashedAnswer", "hashed")
        every { userRepository.findUserByUsername("username") } returns user

        val login = Login("username", "password")
        val loginException = assertFailsWith(LoginException::class, "asdasd") {
            userService.login(login)
        }

        assertEquals("Username and password do not match. You have 4 Login attempts left.", loginException.localizedMessage)

        verify { userRepository.updateUser(user.copy(loginAttemptsLeft = 4)) }
    }

    @Test
    fun `it should reset login attempts if correct password is entered` () {
        val password = "password"
        val loginModel = Login("username", password)
        val hashedPassword = password.hash()
        val user = User(1, "username", 1, 3, 5,"hashedAnswer", hashedPassword)

        every { userRepository.findUserByUsername("username") } returns user

        userService.login(loginModel)

        verify { userRepository.updateUser(user.copy(loginAttemptsLeft = 5)) }
    }

    @Test
    fun `it should throw User does not exist exception while resetting password if user does not exist`() {
        val verifySecQuestion = VerifySecQuestion("username", 1, "sec answer")

        every { userRepository.findUserByUsername(any()) } returns null

        val exception = assertFailsWith(LoginException::class) {
            userService.verifySecurityQuestion(verifySecQuestion)
        }

        assertEquals("User does not exist", exception.localizedMessage);
    }

    @Test
    fun `it should throw exception if security question does not match`() {
        val verifySecQuestion = VerifySecQuestion("username", 2, "sec answer")
        val user = User(1, "username", 1, 5,5, "hashedAnswer", "hashedPassword")

        every { userRepository.findUserByUsername(any()) } returns user

        val loginException = assertFailsWith(LoginException::class) {
            userService.verifySecurityQuestion(verifySecQuestion)
        }

        assertEquals("Incorrect security question", loginException.localizedMessage)
    }

    @Test
    fun `it should throw exception with correct left reset password attempts if security question answer is incorrect`() {
        val correctSecAnswer = "CorrectAnswer"
        val verifySecQuestion = VerifySecQuestion("username", 1, "IncorrectAnswer")
        val user = User(1, "username", 1, 5, 5, correctSecAnswer.hash(), "hashedPassword")

        every { userRepository.findUserByUsername(any()) } returns user

        val loginException = assertFailsWith(LoginException::class) {
            userService.verifySecurityQuestion(verifySecQuestion)
        }

        assertEquals("The answer you have entered does not match what we have on file. " +
                "Please try again, you have 4 number of attempts left.", loginException.localizedMessage)
    }

    @Test
    fun `it should reduce count when security question answer is incorrect`() {
        val verifySecQuestion = VerifySecQuestion("username", 1, "incorrect answer")
        val user = User(1, "username", 1, 5, 5,"hashedAnswer", "hashedPassword")

        every { userRepository.findUserByUsername(any()) } returns user

        assertFailsWith(LoginException::class) {
            userService.verifySecurityQuestion(verifySecQuestion)
        }

        verify { userRepository.updateUser(user.copy(resetPasswordAttemptsLeft = 4)) }
    }

    @Test
    fun `it should reset password reset attempts when correct security question answer is provided`() {
        val correctSecAnswer = "CorrectAnswer"
        val verifySecQuestion = VerifySecQuestion("username", 1, correctSecAnswer)
        val user = User(1, "username", 1, 5, 2, correctSecAnswer.hash(), "hashedPassword")

        every { userRepository.findUserByUsername(any()) } returns user

        userService.verifySecurityQuestion(verifySecQuestion)

        verify { userRepository.updateUser(user.copy(resetPasswordAttemptsLeft = 5)) }
    }

    @Test
    fun `it should throw error when reset password attempts are exhausted`() {
        val verifySecQuestion = VerifySecQuestion("username", 1, "incorrect answer")
        val user = User(1, "username", 1, 3, 1,"hashedAnswer", "hashedPassword")

        every { userRepository.findUserByUsername(any()) } returns user

        val exception = assertFailsWith(ResetPasswordAttemptsExhaustedException::class) {
            userService.verifySecurityQuestion(verifySecQuestion)
        }

        assertEquals("The answer you have entered does not match what we have on file and this account will be deactivated." +
                " Please create a new account", exception.localizedMessage)

    }

    @Test
    fun `it should lock account when password reset attempts are exhausted`() {
        val verifySecQuestion = VerifySecQuestion("username", 1, "incorrect answer")
        val user = User(1, "username", 1, 3, 0,"hashedAnswer", "hashedPassword")

        every { userRepository.findUserByUsername(any()) } returns user

        val exception = assertFailsWith(ResetPasswordAttemptsExhaustedException::class) {
            userService.verifySecurityQuestion(verifySecQuestion)
        }

        assertEquals("This account is deactivated. Please create a new account", exception.localizedMessage)
    }


    @Test
    fun `it should return Reset password token when sec question and answer is correct`() {
        val correctSecAnswer = "CorrectAnswer"
        val verifySecQuestion = VerifySecQuestion("username", 1, correctSecAnswer)
        val user = User(1, "username", 1, 5, 2, correctSecAnswer.hash(), "hashedPassword")

        every { userRepository.findUserByUsername(any()) } returns user

        val userResponse = userService.verifySecurityQuestion(verifySecQuestion)

        verify { mockJWTService.generateResetPasswordToken(any()) }

        assertNotNull(userResponse)
        assertNotNull(userResponse.resetPasswordToken)
    }

    @Test
    fun `it should throw error when updating password if password is null`() {
        val user = User(1, "username", 1, 5, 2,"oldHashedAnswer", "hashedPassword")

        every { userRepository.findUserById(1) } returns user
        assertFailsWith(LoginException::class) {
            userService.updatePassword(1, null)
        }
    }

    @Test
    fun `it should update password`() {
        val user = User(1, "username", 1, 5, 2,"oldHashedAnswer", "hashedPassword")

        every { userRepository.findUserById(1) } returns user

        userService.updatePassword(1, "newpassword")

        verify { userRepository.updateUser(user.copy(password = "newpassword".hash())) }

    }
}
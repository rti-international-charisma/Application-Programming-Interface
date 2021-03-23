package service

import com.rti.charisma.api.exception.UserAlreadyExistException
import com.rti.charisma.api.Signup
import com.rti.charisma.api.repository.UserRepository
import com.rti.charisma.api.service.UserService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UserServiceTest {

    private lateinit var userService: UserService
    private val userRepository = mockk<UserRepository>(relaxed = true)

    @BeforeEach
    fun setup() {
        userService = UserService(userRepository)
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
        every { userRepository.doesUserExist(signupModel.username) } returns false
        every { userRepository.registerUser(signupModel) } returns 1

        val registeredUserId = userService.registerUser(signupModel)

        verify { userRepository.registerUser(signupModel) }
        assertEquals(1, registeredUserId)
    }
}
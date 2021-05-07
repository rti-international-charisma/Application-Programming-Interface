package com.rti.charisma.api.service

import com.rti.charisma.api.config.ConfigProvider
import com.rti.charisma.api.config.LOGIN_ATTEMPTS
import com.rti.charisma.api.db.tables.SecurityQuestion
import com.rti.charisma.api.db.tables.User
import com.rti.charisma.api.exception.LoginAttemptsExhaustedException
import com.rti.charisma.api.exception.LoginException
import com.rti.charisma.api.exception.SecurityQuestionException
import com.rti.charisma.api.exception.UserAlreadyExistException
import com.rti.charisma.api.repository.UserRepository
import com.rti.charisma.api.route.Login
import com.rti.charisma.api.route.Signup
import com.rti.charisma.api.route.response.UserResponse
import com.rti.charisma.api.util.hash
import org.slf4j.LoggerFactory

class UserService(private val userRepository: UserRepository, private val jwtService : JWTService) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    fun registerUser(signupModel: Signup): Int {
        if (userRepository.doesUserExist(signupModel.username)) {
            logger.warn("User already exists")
            throw UserAlreadyExistException()
        } else {
            userRepository.getSecurityQuestions(signupModel.secQuestionId).firstOrNull()?.let {
                return userRepository.registerUser(signupModel, ConfigProvider.get(LOGIN_ATTEMPTS).toInt())
            } ?: run {
                logger.warn("Security question does not exist, ${signupModel.secQuestionId}")
                throw SecurityQuestionException("Security question with Id: ${signupModel.secQuestionId} is not present")
            }
        }
    }

    fun getSecurityQuestions(secQId: Int?): List<SecurityQuestion> {
        val securityQuestions = userRepository.getSecurityQuestions(secQId)
        if (securityQuestions.isNotEmpty()) {
            return securityQuestions
        } else {
            logger.warn("Failed to get Security question with Id, $secQId")
            throw SecurityQuestionException("Security question with Id: $secQId is not present")
        }
    }

    fun login(loginModel: Login): UserResponse {
        val user = userRepository.findUserByUsername(loginModel.username)
        user?.let {
            if (loginModel.password.hash() == it.password) {
                user.loginAttemptsLeft = ConfigProvider.get(LOGIN_ATTEMPTS).toInt()
                userRepository.updateUser(user)
                return UserResponse(user, jwtService.generateToken(it))
            } else {
                if (user.loginAttemptsLeft > 0) {
                    user.loginAttemptsLeft--
                    userRepository.updateUser(user)
                    throw LoginException("Username and password do not match. You have ${user.loginAttemptsLeft} Login attempts left.")
                } else {
                    logger.warn("Login attempts exhausted for user, ${user.id}")
                    throw LoginAttemptsExhaustedException()
                }
            }
        }
        logger.warn("User does not exist, ${loginModel.username}")
        throw LoginException("User does not exist")
    }

    fun findUserById(userId: Int): User? = userRepository.findUserById(userId)

    fun isUsernameAvailable(username: String): Boolean {
        userRepository.findUserByUsername(username)?.let { return false }
        return true
    }
}
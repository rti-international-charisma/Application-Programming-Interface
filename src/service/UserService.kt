package com.rti.charisma.api.service

import com.rti.charisma.api.config.ConfigProvider
import com.rti.charisma.api.config.LOGIN_ATTEMPTS
import com.rti.charisma.api.config.RESET_ATTEMPTS
import com.rti.charisma.api.db.tables.SecurityQuestion
import com.rti.charisma.api.db.tables.User
import com.rti.charisma.api.exception.*
import com.rti.charisma.api.repository.UserRepository
import com.rti.charisma.api.route.Login
import com.rti.charisma.api.route.Signup
import com.rti.charisma.api.route.VerifySecQuestion
import com.rti.charisma.api.route.response.UserResponse
import com.rti.charisma.api.util.hash
import org.slf4j.LoggerFactory

class UserService(private val userRepository: UserRepository, private val jwtService: JWTService) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    /**
     * Register User
     * Verifies if user with similar username is not present and the security question with that id is present
     */
    fun registerUser(signupModel: Signup): Int {
        if (userRepository.doesUserExist(signupModel.username)) {
            logger.warn("User already exists")
            throw UserAlreadyExistException()
        } else {
            userRepository.getSecurityQuestions(signupModel.secQuestionId).firstOrNull()?.let {
                return userRepository.registerUser(
                    signupModel,
                    ConfigProvider.get(LOGIN_ATTEMPTS).toInt(),
                    ConfigProvider.get(RESET_ATTEMPTS).toInt()
                )
            } ?: run {
                logger.warn("Security question does not exist, ${signupModel.secQuestionId}")
                throw SecurityQuestionException("Security question with Id: ${signupModel.secQuestionId} is not present")
            }
        }
    }

    /**
     * Returns a list of Security Questions
     * If secQId is provided then returns that particular security question.
     * Else returns all security question.
     */
    fun getSecurityQuestions(secQId: Int?): List<SecurityQuestion> {
        val securityQuestions = userRepository.getSecurityQuestions(secQId)
        if (securityQuestions.isNotEmpty()) {
            return securityQuestions
        } else {
            logger.warn("Failed to get Security question with Id, $secQId")
            throw SecurityQuestionException("Security question with Id: $secQId is not present")
        }
    }

    /**
     * Logs in a user.
     * Returns [UserResponse]
     *
     * If the user tries login with incorrect password, decrements the loginAttemptsLeft for that user by 1 and @throws [LoginException]
     * If the user tries login with incorrect password for more than [LOGIN_ATTEMPTS] times @throws [LoginAttemptsExhaustedException]
     * If the user has exhausted resetPassword attempts @throws [LoginException]
     * @see [ConfigProvider]
     * Returns UserResponse with [User] and JWT valid for [JWTService.validityInMs] duration.
     */
    fun login(loginModel: Login): UserResponse {
        val user = userRepository.findUserByUsername(loginModel.username)
        user?.let {
            if (user.resetPasswordAttemptsLeft <= 0) {
                logger.warn("Deactivated user tried login, ${user.id}")
                throw LoginException("This account is deactivated. Please create a new account")
            }
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

    /**
     * Used for resetting the Password.
     * If the user tries to verify security question with incorrect answer, decrement the resetPasswordAttemptsLeft by 1 and @throws [ResetPasswordAttemptsExhaustedException]
     * If the user tries to verify security question with incorrect answer for more than [RESET_ATTEMPTS] times then blocks the account forever and @throws [ResetPasswordAttemptsExhaustedException]
     *
     * ResetPasswordAttemptsLeft is reset to [RESET_ATTEMPTS] when user enters correct secret answer.
     * Submitting correct secret question - answer combination, returns [UserResponse] with [UserResponse.resetPasswordToken] set. Which is a JWT valid for set time.
     * [UserResponse.resetPasswordToken] is verified at the time of actually resetting the password.
     * @see [JWTService]
     */
    fun verifySecurityQuestion(verifySecQuestion: VerifySecQuestion): UserResponse {
        val user = userRepository.findUserByUsername(verifySecQuestion.username)
        user?.let {
            if (user.resetPasswordAttemptsLeft <= 0) {
                logger.warn("This account is deactivated. Please create a new account. UserId ${user.id}")
                throw ResetPasswordAttemptsExhaustedException("This account is deactivated. Please create a new account")
            }

            if (verifySecQuestion.secQuestionId == user.sec_q_id) {
                if (verifySecQuestion.secQuestionAnswer.hash() == user.sec_answer) {
                    user.resetPasswordAttemptsLeft = ConfigProvider.get(RESET_ATTEMPTS).toInt()
                    userRepository.updateUser(user)
                    return UserResponse(user, resetPasswordToken = jwtService.generateResetPasswordToken(user))
                } else {
                    if (user.resetPasswordAttemptsLeft == 1) {
                        user.resetPasswordAttemptsLeft--
                        user.loginAttemptsLeft = 0
                        userRepository.updateUser(user)
                        logger.warn("Deactivating account. Reset password attempts exhausted userId: ${user.id}")
                        throw ResetPasswordAttemptsExhaustedException(
                            "The answer you have entered does not match what we have on file and this account will be deactivated. Please create a new account"
                        )
                    } else if (user.resetPasswordAttemptsLeft > 0) {
                        user.resetPasswordAttemptsLeft--
                        userRepository.updateUser(user)
                        logger.warn("Incorrect secret question answer . Decrementing reset password attempts for userId: ${user.id}")
                        throw LoginException(
                            "The answer you have entered does not match what we have on file. Please try again, you have ${user.resetPasswordAttemptsLeft} number of attempts left."
                        )
                    }
                }
            } else {
                logger.warn("Incorrect security question UserId: ${user.id}")
                throw LoginException("Incorrect security question")
            }
        } ?: run {
            logger.warn("User does not exist Username: ${verifySecQuestion.username}")
            throw LoginException("User does not exist")
        }
        logger.error("Something went wrong while verifying security question for Username: ${verifySecQuestion.username}")
        throw LoginException("Something went wrong")
    }

    fun updatePassword(userId: Int, newPassword: String?) {
        newPassword?.let {
            val user = userRepository.findUserById(userId)
            userRepository.updateUser(user!!.copy(password = newPassword.hash()))
        } ?: run {
            logger.error("Something went wrong while updating password for UserId: $userId")
            throw LoginException("Something went wrong")
        }
    }

    fun deleteInactiveUsers(durationInDays: Long): Int {
        try {
           return userRepository.deleteInactiveUsers(durationInDays)
        } catch (e: Exception) {
            logger.error("Failed to delete inactive users", e.localizedMessage )
            throw DataBaseException("Failed to delete inactive users", e)
        }
    }
}
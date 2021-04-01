package com.rti.charisma.api.service

import com.rti.charisma.api.exception.SecurityQuestionException
import com.rti.charisma.api.db.tables.SecurityQuestion
import com.rti.charisma.api.db.tables.User
import com.rti.charisma.api.exception.UserAlreadyExistException
import com.rti.charisma.api.exception.LoginException
import com.rti.charisma.api.model.UserResponse
import com.rti.charisma.api.repository.UserRepository
import com.rti.charisma.api.route.Login
import com.rti.charisma.api.route.Signup
import com.rti.charisma.api.util.hash

class UserService(private val userRepository: UserRepository, private val jwtService : JWTService) {
    fun registerUser(signupModel: Signup): Int {
        if (userRepository.doesUserExist(signupModel.username)) {
            throw UserAlreadyExistException()
        } else {
            userRepository.getSecurityQuestions(signupModel.secQuestionId).firstOrNull()?.let {
                return userRepository.registerUser(signupModel)
            } ?: run {
                throw SecurityQuestionException("Security question with Id: ${signupModel.secQuestionId} is not present")
            }
        }
    }

    fun getSecurityQuestions(secQId: Int?): List<SecurityQuestion> {
        val securityQuestions = userRepository.getSecurityQuestions(secQId)
        if (securityQuestions.isNotEmpty()) {
            return securityQuestions
        } else {
            throw SecurityQuestionException("Security question with Id: $secQId is not present")
        }
    }

    fun login(loginModel: Login): UserResponse {
        val user = userRepository.findUserByUsername(loginModel.username)
        user?.let {
            if (loginModel.password.hash() == it.password) {
                return UserResponse(user, jwtService.generateToken(it))
            } else {
                throw LoginException("Username and password do not match")
            }
        }
        throw LoginException("User does not exist.")
    }

    fun findUserById(userId: Int): User? = userRepository.findUserById(userId)

    fun findUsersByUsername(username: String): Boolean {
        userRepository.findUserByUsername(username)?.let { return true }
        return false
    }
}
package com.rti.charisma.api.service

import com.rti.charisma.api.Signup
import com.rti.charisma.api.exception.UserAlreadyExistException
import com.rti.charisma.api.repository.UserRepository

class UserService(private val userRepository: UserRepository) {
    fun registerUser(signupModel: Signup): Int {
        if (userRepository.doesUserExist(signupModel.username)) {
            throw UserAlreadyExistException()
        } else {
            return userRepository.registerUser(signupModel)
        }
    }
}
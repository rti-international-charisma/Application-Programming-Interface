package com.rti.charisma.api.repository

import com.rti.charisma.api.db.CharismaDB
import com.rti.charisma.api.db.tables.SecurityQuestions
import com.rti.charisma.api.db.tables.Users
import com.rti.charisma.api.route.Signup
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryImplTest {

    private lateinit var db: Database

    @BeforeAll
    fun setup() {
        db = CharismaDB.init(InMemoryDB.inMemoryDataSource())
    }

    @BeforeEach
    fun setupData() {
        transaction {
            SchemaUtils.create(Users, SecurityQuestions)
            SecurityQuestions.insert {
                it[sec_q_id] = 1
                it[question] = "Security Question 1"
            }

            SecurityQuestions.insert {
                it[sec_q_id] = 2
                it[question] = "Security Question 2"
            }

            Users.insert {
                it[username] = "username"
                it[password] = "hashed-password"
                it[sec_q_id] = 1
                it[sec_answer] = "hashed-answer"
                it[loginAttempts] = 5
                it[resetPasswordAttempts] = 5
                it[lastLogin] = LocalDateTime.now().minusDays(3)
            }
        }
    }

    @AfterEach
    fun cleanupData(){
        transaction {
            SchemaUtils.drop(Users, SecurityQuestions)
        }
    }

    private val userRepository = UserRepositoryImpl()

    @Test
    fun `it should return false if username does not exist`() {
        val doesUserExist = userRepository.doesUserExist("non-existant-username")
        assertFalse(doesUserExist)
    }

    @Test
    fun `it should return true if username exist`() {
        val doesUserExist = userRepository.doesUserExist("username")
        assertTrue(doesUserExist)
    }

    @Test
    fun `it should return user from username`() {
        val user = userRepository.findUserByUsername("username")
        assertNotNull(user)
    }

    @Test
    fun `it should return null if username is not present`() {
        val user = userRepository.findUserByUsername("username1")
        assertNull(user)
    }

    @Test
    fun `it should return user by id`() {
        val user = userRepository.findUserById(1)
        assertNotNull(user)
    }

    @Test
    fun `it should return null if user is not present id`() {
        val user = userRepository.findUserById(3)
        assertNull(user)
    }

    @Test
    fun `it should register user `() {
        val signupModel = Signup("username1, ", "password", 1, "Answer")
        val userId = userRepository.registerUser(signupModel, 5, 5)
        assertNotNull(userId)
    }

    @Test
    fun `it should hash username and password `() {
        val signupModel = Signup("username1, ", "password", 1, "Answer")
        val userId = userRepository.registerUser(signupModel, 5, 5)
        val user = userRepository.findUserById(userId)
        assertNotEquals("password", user?.password)
    }


    @Test
    fun `it should delete user with last login before allowed date `() {
        assertNotNull(userRepository.findUserByUsername("username"))

        val result = userRepository.deleteInactiveUsers(2)

        assertEquals(1, result)
        assertNull(userRepository.findUserByUsername("username"))

    }

    @Test
    fun `it should delete users with last login before allowed date and returns number of users deleted `() {
        transaction {
            Users.insert {
                it[username] = "username1"
                it[password] = "hashed-password"
                it[sec_q_id] = 1
                it[sec_answer] = "hashed-answer"
                it[loginAttempts] = 5
                it[resetPasswordAttempts] = 5
                it[lastLogin] = LocalDateTime.now().minusDays(1)
            }

            Users.insert {
                it[username] = "username2"
                it[password] = "hashed-password"
                it[sec_q_id] = 1
                it[sec_answer] = "hashed-answer"
                it[loginAttempts] = 5
                it[resetPasswordAttempts] = 5
                it[lastLogin] = LocalDateTime.now().minusDays(4)
            }
        }


        assertNotNull(userRepository.findUserByUsername("username"))
        assertNotNull(userRepository.findUserByUsername("username1"))
        assertNotNull(userRepository.findUserByUsername("username2"))

        val result = userRepository.deleteInactiveUsers(2)

        assertEquals(2, result)
        assertNull(userRepository.findUserByUsername("username"))
        assertNull(userRepository.findUserByUsername("username2"))
        assertNotNull(userRepository.findUserByUsername("username1"))
    }
}
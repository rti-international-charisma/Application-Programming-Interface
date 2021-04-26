package repository

import com.rti.charisma.api.db.CharismaDB
import com.rti.charisma.api.db.tables.SecurityQuestions
import com.rti.charisma.api.db.tables.Users
import com.rti.charisma.api.repository.InMemoryDB
import com.rti.charisma.api.repository.UserRepositoryImpl
import com.rti.charisma.api.route.Signup
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import javax.sql.DataSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryImplTest {

    private lateinit var db: Database

    @AfterAll
    fun cleanup() {
        TransactionManager.closeAndUnregister(db)
    }

    @BeforeAll
    fun setup() {
        db = CharismaDB.init(InMemoryDB.inMemoryDataSource())
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
            }
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
        val userId = userRepository.registerUser(signupModel, 5)
        assertNotNull(userId)
    }

    @Test
    fun `it should hash username and password `() {
        val signupModel = Signup("username1, ", "password", 1, "Answer")
        val userId = userRepository.registerUser(signupModel, 5)
        val user = userRepository.findUserById(userId)
        assertNotEquals("password", user?.password)
    }
}
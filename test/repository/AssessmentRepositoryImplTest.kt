package repository

import com.rti.charisma.api.db.CharismaDB
import com.rti.charisma.api.db.tables.*
import com.rti.charisma.api.repository.AssessmentRepository
import com.rti.charisma.api.repository.AssessmentRepositoryImpl
import com.rti.charisma.api.repository.InMemoryDB
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AssessmentRepositoryImplTest {
    private var testUserId: Int = 0
    private var securityQuestionId: Int = 0
    private lateinit var db: Database
    private val repository: AssessmentRepository = AssessmentRepositoryImpl()

    @BeforeAll
    fun setup() {
        db = CharismaDB.init(InMemoryDB.inMemoryDataSource())
        transaction {
            SchemaUtils.create(Users, SecurityQuestions, SectionScores, Answers)

            securityQuestionId = SecurityQuestions.insert {
                it[question] = "Security Question 1"
            } get SecurityQuestions.sec_q_id

            testUserId = Users.insert {
                it[username] = "username"
                it[password] = "hashed-password"
                it[sec_q_id] = securityQuestionId
                it[sec_answer] = "hashed-answer"
                it[loginAttempts] = 5
                it[resetPasswordAttempts] = 5
            } get Users.id
        }
    }

    private fun addUser(): Int {
        return transaction {
            Users.insert {
                it[username] = "username"
                it[password] = "hashed-password"
                it[sec_q_id] = securityQuestionId
                it[sec_answer] = "hashed-answer"
                it[loginAttempts] = 5
                it[resetPasswordAttempts] = 5
            } get Users.id
        }
    }

    @Test
    fun `it should return true is user score exists`() {
        transaction {
            var assessmentSectionId = SectionScores.insert {
                it[SectionScores.userId] = testUserId
                it[SectionScores.sectionId] = "test-assessment-section-id"
                it[SectionScores.sectionType] = "TEST SECTION"
            } get SectionScores.id

            Answers.insert {
                it[questionId] = "test-question-id"
                it[score] = 3
                it[Answers.assessmentSectionId] = assessmentSectionId
            }
        }
        val userScoreExists = repository.userScoreExists(testUserId)

        assertTrue(userScoreExists)
    }

    @Test
    fun `it should return false is user score does not exists`() {
        transaction {
            var assessmentSectionId = SectionScores.insert {
                it[SectionScores.userId] = testUserId
                it[SectionScores.sectionId] = "test-assessment-section-id"
                it[SectionScores.sectionType] = "TEST SECTION"
            } get SectionScores.id

            Answers.insert {
                it[questionId] = "test-question-id"
                it[score] = 3
                it[Answers.assessmentSectionId] = assessmentSectionId
            }
        }
        val userScoreExists = repository.userScoreExists(2334)

        assertFalse(userScoreExists)
    }

    @Test
    fun `it should insert user score with all sections and answers`() {
        //given
        val sectionScore1 = createSectionEntry(testUserId,"section-id-1", "section-type-1", 11, 12)
        val sectionScore2 = createSectionEntry(testUserId,"section-id-2", "section-type-2", 21, 22)

        //when
        repository.insertScore(mutableListOf(sectionScore1, sectionScore2))

        //then
        transaction {
            val sectionScore1FromDB =
                SectionScores.select { (SectionScores.userId eq testUserId) and (SectionScores.sectionId eq "section-id-1") }
                    .firstOrNull()
            val sectionScore2FromDB =
                SectionScores.select { (SectionScores.userId eq testUserId) and (SectionScores.sectionId eq "section-id-2") }
                    .firstOrNull()
            assertNotNull(sectionScore1FromDB)
            assertEquals("section-type-1", sectionScore1FromDB[SectionScores.sectionType])
            assertNotNull(sectionScore2FromDB)
            assertEquals("section-type-2", sectionScore2FromDB[SectionScores.sectionType])

            val answersForSection1FromDB =
                Answers.select { Answers.assessmentSectionId eq sectionScore1FromDB[SectionScores.id] }
            assertEquals(2, answersForSection1FromDB.count())
            assertEquals(11, answersForSection1FromDB.first()[Answers.score])
            assertEquals(12, answersForSection1FromDB.last()[Answers.score])

            val answersForSection2FromDB =
                Answers.select { Answers.assessmentSectionId eq sectionScore2FromDB[SectionScores.id] }
            assertEquals(2, answersForSection2FromDB.count())
            assertEquals(21, answersForSection2FromDB.first()[Answers.score])
            assertEquals(22, answersForSection2FromDB.last()[Answers.score])
        }

    }

    @Test
    fun `it should update user score with all sections and answers`() {
        //given
        val sectionScore1 = createSectionEntry(testUserId,"section-id-1", "section-type-1", 11, 12)
        val sectionScore2 = createSectionEntry(testUserId,"section-id-2", "section-type-2", 21, 22)
        repository.insertScore(mutableListOf(sectionScore1, sectionScore2))

        //when
        val sectionScoreNew1 = createSectionEntry(testUserId,"section-id-1", "section-type-11", 111, 112)
        val sectionScoreNew2 = createSectionEntry(testUserId,"section-id-new-2", "section-type-new-12", 221, 222)
        val sectionScoreNew3 = createSectionEntry(testUserId,"section-id-3", "section-type-13", 331, 332)
        repository.replaceScore(mutableListOf(sectionScoreNew1, sectionScoreNew2, sectionScoreNew3))

        //then
        transaction {
            val sectionScoresFromDB = SectionScores.select { (SectionScores.userId eq testUserId) }
            assertNotNull(sectionScoresFromDB)
            assertEquals(3, sectionScoresFromDB.count())

            val sectionTypes = sectionScoresFromDB.map { it[SectionScores.sectionType] }
            assertTrue(
                sectionTypes.containsAll(
                    mutableListOf(
                        "section-type-11",
                        "section-type-new-12",
                        "section-type-13"
                    )
                )
            )
            assertFalse(sectionTypes.contains("section-type-12"))

            val sectionIds = sectionScoresFromDB.map { it[SectionScores.id] }

            val answersForSection1FromDB = Answers.select { Answers.assessmentSectionId eq sectionIds[0] }
            assertEquals(2, answersForSection1FromDB.count())
            assertEquals(111, answersForSection1FromDB.first()[Answers.score])
            assertEquals(112, answersForSection1FromDB.last()[Answers.score])

            val answersForSection2FromDB = Answers.select { Answers.assessmentSectionId eq sectionIds[1] }
            assertEquals(2, answersForSection2FromDB.count())
            assertEquals(221, answersForSection2FromDB.first()[Answers.score])
            assertEquals(222, answersForSection2FromDB.last()[Answers.score])

            val answersForSection3FromDB = Answers.select { Answers.assessmentSectionId eq sectionIds[2] }
            assertEquals(2, answersForSection3FromDB.count())
            assertEquals(331, answersForSection3FromDB.first()[Answers.score])
            assertEquals(332, answersForSection3FromDB.last()[Answers.score])
        }

    }

    @Test
    fun `it should return all sections and relevant answers for a user`() {
        //given
        val userId = addUser()
        val sectionScore1 = createSectionEntry(userId,"section-id-1", "section-type-1", 11, 12)
        val sectionScore2 = createSectionEntry(userId,"section-id-2", "section-type-2", 21, 22)
        repository.insertScore(mutableListOf(sectionScore1, sectionScore2))

        //when
        val userSections: List<SectionScore> = repository.findSectionsByUser(userId = userId)

        //then
        assertEquals(2, userSections.size)
        assertEquals("section-id-1", userSections[0].sectionId)
        assertEquals("section-type-1", userSections[0].sectionType)
        assertEquals(2, userSections[0].answers.size)
        assertEquals("section-id-2", userSections[1].sectionId)
        assertEquals("section-type-2", userSections[1].sectionType)
        assertEquals(2, userSections[1].answers.size)
    }

    @Test
    fun `it should return empty list if no scores found for user`() {
        //when
        val userSections: List<SectionScore> = repository.findSectionsByUser(userId = 1231)

        //then
        assertTrue(userSections.isEmpty())

    }

    @Test
    fun `it should return empty answers list if no answers in sections found for user`() {
        //given
        val userId = addUser()
        transaction {  SectionScores.insert {
           it[SectionScores.sectionId] = "section1"
           it[SectionScores.sectionType] = "sectionType1"
           it[SectionScores.userId] = userId
       }}

        //when
        val userSections: List<SectionScore> = repository.findSectionsByUser(userId)

        //then
        assertEquals(1, userSections.size)
        assertEquals("section1", userSections[0].sectionId)
        assertEquals("sectionType1", userSections[0].sectionType)
        assertTrue(userSections[0].answers.isEmpty())
    }


    private fun createSectionEntry(userId: Int,sectionId: String, sectionType: String, score1: Int, score2: Int): SectionScore {
        return SectionScore(
            user = userId,
            sectionId = sectionId,
            sectionType = sectionType,
            answers = mutableListOf(
                Answer(questionId = "question-$score1", score = score1),
                Answer(questionId = "question-$score2", score = score2)
            )
        )
    }

}
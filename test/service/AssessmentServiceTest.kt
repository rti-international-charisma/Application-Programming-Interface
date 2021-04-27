package service

import com.rti.charisma.api.db.tables.Answer
import com.rti.charisma.api.db.tables.SectionScore
import com.rti.charisma.api.repository.AssessmentRepository
import com.rti.charisma.api.route.AssessmentResult
import com.rti.charisma.api.route.Question
import com.rti.charisma.api.service.AssessmentService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertSame

class AssessmentServiceTest {
    private val repository = mockk<AssessmentRepository>(relaxed = true)
    private val service = AssessmentService(repository)


    @Test
    fun `it should insert scores for a user`()  {
        val userId = 2
        every { repository.userScoreExists(userId) } returns false

        val expectedSections = expected(userId)

        val section1 = request()
        val assessmentResults = mutableListOf(section1)

        //when
        service.addAssessmentScore(userId, assessmentResults)

        //then
        verify { repository.insertScore(eq(expectedSections)) }
    }

    @Test
    fun `it should replace scores for a user`() {
        val userId = 2
        val expectedSections = expected(userId)
        val section1 = request()
        val assessmentResults = mutableListOf(section1)

        every { repository.userScoreExists(userId) } returns true

        //when
        service.addAssessmentScore(userId, assessmentResults)

        //then
        verify { repository.replaceScore(eq(expectedSections)) }
    }


    @Test
    fun `it should return scores for a user`() {
        val userId = 2

        val sectionScore1 =  SectionScore(
            user = userId,
            sectionId = "section-1",
            sectionType = "section-type1",
            answers = mutableListOf(Answer(questionId = "question1", score = 11))
        )
        val sectionScore2 =  SectionScore(
            user = userId,
            sectionId = "section-2",
            sectionType = "section-type2",
            answers = mutableListOf(
                Answer(questionId = "question2", score = 22),
                Answer(questionId = "question3", score = 33))
        )
        every { repository.findSectionsByUser(userId) } returns mutableListOf(sectionScore1, sectionScore2)

        //when
        val assessmentScore = service.getAssessmentScore(userId)

        //then
        assertEquals(2, assessmentScore.sections.size)

        assertEquals("section-1", assessmentScore.sections[0].sectionId)
        assertEquals("section-type1", assessmentScore.sections[0].sectionType)
        assertEquals(1, assessmentScore.sections[0].answers.size)
        assertEquals("question1", assessmentScore.sections[0].answers[0].questionId)
        assertEquals(11, assessmentScore.sections[0].answers[0].score)


        assertEquals("section-2", assessmentScore.sections[1].sectionId)
        assertEquals("section-type2", assessmentScore.sections[1].sectionType)
        assertEquals(2, assessmentScore.sections[1].answers.size)
        assertEquals("question2", assessmentScore.sections[1].answers[0].questionId)
        assertEquals("question3", assessmentScore.sections[1].answers[1].questionId)
        assertEquals(22, assessmentScore.sections[1].answers[0].score)
        assertEquals(33, assessmentScore.sections[1].answers[1].score)

    }

    @Test
    fun `it should return empty response if no score found for a user`() {
        val userId = 2
        every { repository.findSectionsByUser(userId) } returns emptyList()

        //when
        val assessmentScore = service.getAssessmentScore(userId)

        //then
        assertTrue(assessmentScore.sections.isEmpty())
    }

    private fun request(): AssessmentResult {
        return AssessmentResult(
            "section-1",
            "section-type",
            mutableListOf(Question("question1", 44))
        )
    }

    private fun expected(userId: Int): MutableList<SectionScore> {
        return mutableListOf(
            SectionScore(
                user = userId,
                sectionId = "section-1",
                sectionType = "section-type",
                answers = mutableListOf(Answer(questionId = "question1", score = 44))
            )
        )
    }

}


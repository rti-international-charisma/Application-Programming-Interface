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
import org.junit.jupiter.api.Test

class AssessmentServiceTest {
    private val repository = mockk<AssessmentRepository>(relaxed = true)
    private val service = AssessmentService(repository)


    @Test
    fun `it should insert scores for a user`() = runBlockingTest {
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
    fun `it should replace scores for a user`() = runBlockingTest {
        val userId = 2
        every { repository.userScoreExists(userId) } returns true

        val expectedSections = expected(userId)

        val section1 = request()
        val assessmentResults = mutableListOf(section1)

        //when
        service.addAssessmentScore(userId, assessmentResults)

        //then
        verify { repository.replaceScore(eq(expectedSections)) }
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


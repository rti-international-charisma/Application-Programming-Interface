package content.serialiser

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rti.charisma.api.fixtures.AssessmentFixture
import com.rti.charisma.api.model.AssessmentSection
import com.rti.charisma.api.model.Option
import com.rti.charisma.api.model.Question
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AssessmentSectionConversionsTest {
    @Test
    fun `it should serialise assessment sections `() {

        val assessmentSection = assessmentSection("published")

        val json = jacksonObjectMapper().writeValueAsString(assessmentSection)

        assertEquals(AssessmentFixture.assessmentSectionsJson(), json)
    }

    @Test
    fun `it should serialise only published section`() {
        val assessments = assessmentSection("archived")

        val json = jacksonObjectMapper().writeValueAsString(assessments)

        assertTrue(json.isEmpty())
    }

    @Test
    fun `it should ignore empty questions in section`() {
        val assessments = AssessmentSection("section name", "published", "introduction", emptyList())

        val json = jacksonObjectMapper().writeValueAsString(assessments)

        assertEquals("""{"section":"section name","introduction":"introduction","questions":[]}""",json)
    }

    private fun assessmentSection(status: String): AssessmentSection {
        val option1 = Option("option1", 1)
        val option2 = Option("option2", 2)
        val question1 = Question("question text1", "description1", mutableListOf(option1, option2))
        val question2 = Question("question text2", "description2", mutableListOf(option1, option2))
        return AssessmentSection("section name", status, "introduction", mutableListOf(question1, question2))
    }
}
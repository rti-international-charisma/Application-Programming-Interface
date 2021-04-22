package route.response

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rti.charisma.api.fixtures.AssessmentFixture
import com.rti.charisma.api.fixtures.PageContentFixture
import com.rti.charisma.api.model.AssessmentSection
import com.rti.charisma.api.model.Option
import com.rti.charisma.api.model.Question
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PageConversionsTest {
    @Test
    fun `it should serialise page content `() {

        val pageContent = PageContentFixture.pageWithVideoSection("published")

        val json = jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(pageContent)

        assertEquals(PageContentFixture.pageWithVideoSectionResponseJson(), json)
    }

    @Test
    fun `it should serialise only published page`() {
        val pageContent = PageContentFixture.pageWithVideoSection("archived")

        val json = jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(pageContent)

        assertEquals("""{ }""", json)
    }

    @Test
    fun `it should not serialised null content`() {

        val pageContent = PageContentFixture.withNoVideoSectionAndSteps("published")
        val json = jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(pageContent)

        assertEquals(PageContentFixture.pageWithoutVideoSectionJson(),json)
    }

}
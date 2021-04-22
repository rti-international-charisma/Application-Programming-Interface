package service

import com.rti.charisma.api.client.ContentClient
import com.rti.charisma.api.exception.ContentException
import com.rti.charisma.api.exception.ContentRequestException
import com.rti.charisma.api.fixtures.AssessmentFixture
import com.rti.charisma.api.fixtures.PageContentFixture
import com.rti.charisma.api.model.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExperimentalCoroutinesApi
class ContentServiceTest {

    private val contentClient = mockk<ContentClient>(relaxed = true)
    private val contentService = ContentService(contentClient)

    @Test
    fun `it should parse page response with video sections and steps`() = runBlockingTest {
        val expectedHomePage = PageContentFixture.withVideoSectionAndSteps()

        coEvery { contentClient.getPage("/items/homepage?fields=*.*.*") } returns PageContentFixture.pageFromCmsWithVideos()

        val homePage: Page = contentService.getHomePage()

        assertEquals(expectedHomePage, homePage)
    }

    @Test
    fun `it should parse page response for given page id with images and no video sections`() = runBlockingTest {
        val expectedPageContent = PageContentFixture.withNoVideoSectionAndSteps("published")
        val pageId = "test-page"

        coEvery {
            contentClient.getPage("/items/pages/${pageId}?fields=*.*.*") } returns PageContentFixture.pageFromCmsWithImages()

        val pageContent = contentService.getPage(pageId)

        assertEquals(expectedPageContent, pageContent)
    }

    @Test
    fun `it should throw exception on error processing response`() = runBlockingTest {
        coEvery { contentClient.getPage("/items/homepage?fields=*.*.*") } throws (ContentException("Content error"))
        assertFailsWith(
            exceptionClass = ContentException::class,
            block = { contentService.getHomePage() }
        )
    }

    @Test
    fun `it should throw exception on error fetching response`() = runBlockingTest {
        coEvery { contentClient.getPage("/items/homepage?fields=*.*.*") } throws (ContentRequestException("Content error"))
        assertFailsWith(
            exceptionClass = ContentRequestException::class,
            block = { contentService.getHomePage() }
        )
    }

    @Test
    fun `it should throw exception on error processing page response`() = runBlockingTest {
        coEvery { contentClient.getPage("/items/pages/test-page?fields=*.*.*") } throws (ContentException("Content error"))
        assertFailsWith(
            exceptionClass = ContentException::class,
            block = { contentService.getPage("test-page") }
        )
    }

    @Test
    fun `it should throw exception on error fetching page response`() = runBlockingTest {
        coEvery { contentClient.getPage("/items/pages/test-page?fields=*.*.*") } throws (ContentRequestException("Content Request Error"))
        assertFailsWith(
            exceptionClass = ContentRequestException::class,
            block = { contentService.getPage("test-page") }
        )
    }

    @Test
    fun `it should parse assessment response`() = runBlockingTest {
        val expectedAssessmentContent = AssessmentFixture.assessment()

        coEvery {
            contentClient.getAssessment(
                "/items/sections?fields=*," +
                        "questions.questions_id.text,questions.questions_id.options.options_id.*"
            )
        } returns AssessmentFixture.assessmentCmsContent()

        val assessment = contentService.getAssessment()

        assertEquals(expectedAssessmentContent, assessment)
    }

    @Test
    fun `it should throw exception on error processing assessment response`() = runBlockingTest {
        coEvery {
            contentClient.getAssessment(
                "/items/sections?fields=*," +
                        "questions.questions_id.text,questions.questions_id.options.options_id.*"
            )
        } throws (ContentException("Content error"))
        assertFailsWith(
            exceptionClass = ContentException::class,
            block = { contentService.getAssessment() }
        )
    }

    @Test
    fun `it should throw exception on error fetching assessment response`() = runBlockingTest {
        coEvery {
            contentClient.getAssessment(
                "/items/sections?fields=*," +
                        "questions.questions_id.text,questions.questions_id.options.options_id.*"
            )
        } throws (ContentRequestException("Content Request Error"))
        assertFailsWith(
            exceptionClass = ContentRequestException::class,
            block = { contentService.getAssessment() }
        )
    }


}
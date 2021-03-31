package service

import com.rti.charisma.api.client.ContentClient
import com.rti.charisma.api.exception.ContentNotFoundException
import com.rti.charisma.api.route.HomePage
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
    fun `it should throws content not found exception on error fetching response`() = runBlockingTest {
        coEvery { contentClient.request("/items/homepage") } throws (RuntimeException("Some error"))
        assertFailsWith(
            exceptionClass = ContentNotFoundException::class,
            block = { contentService.getHomePage() }
        )
    }

    @Test
    fun `it should parse homepage response`() = runBlockingTest {
        val expectedHomePage = createHomePage()
        val jsonString = """{ "data": {"title": "test-title", "description": "test-description"} }"""

        coEvery { contentClient.request("/items/homepage?fields=*.*") } returns jsonString
        val homePage = contentService.getHomePage()

        assertEquals(expectedHomePage, homePage)
    }

    private fun createHomePage(): HomePage {
        return HomePage(mutableMapOf("title" to "test-title", "description" to "test-description"))
    }
}
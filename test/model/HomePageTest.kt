package model

import com.contentful.java.cda.CDAAsset
import com.contentful.java.cda.CDAClient
import com.contentful.java.cda.CDAEntry
import com.rti.charisma.api.model.HomePage
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class HomePageTest {
    @Test
    fun `should convert CDA Entry to Homepage content ` ()  {
        val cdaEntry = mockk<CDAEntry>(relaxed = true)
        every { cdaEntry.rawFields() } returns mutableMapOf("title" to Any(), "id" to Any())
        every { cdaEntry.getField<String>("title") } returns "test-title"
        every { cdaEntry.getField<String>("id") } returns "test-id"

        val homePage = HomePage.converter(cdaEntry)

        assertEquals("test-title", homePage.textContent["title"])
        assertEquals("test-id", homePage.textContent["id"] )
        assertEquals(mutableMapOf(), homePage.assets)
    }


}


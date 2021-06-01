package com.rti.charisma.api.content.serialiser

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rti.charisma.api.content.*
import com.rti.charisma.api.fixtures.PageContentFixture
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PageConversionsTest {
    @Test
    fun `it should serialise page content with video section `() {

        val pageContent = PageContentFixture.pageWithVideoSection("published")

        val json = jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(pageContent)

        assertEquals(PageContentFixture.pageWithVideoSectionResponseJson(), json)
    }

    @Test
    fun `it should ignore page content with  empty image fields `() {

        val pageContent = givenPageWithEmptyUrls()

        val json = jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(pageContent)

        assertEquals(expectedJsonWithNullAssets(), json)
    }

    @Test
    fun `it should ignore page content with no image fields `() {

        val pageContent = givenPageWithNoUrls()

        val json = jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(pageContent)

        assertEquals(expectedJsonWithNullAssets(), json)
    }

    @Test
    fun `it should serialise page content with counselling modules`() {

        val pageContent = PageContentFixture.pageWithCounsellingModules("published")

        val json = jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(pageContent)

        assertEquals(PageContentFixture.pageWithCounsellingResponseJson(), json)
    }

    @Test
    fun `it should serialise only published page`() {
        val pageContent = PageContentFixture.pageWithVideoSection("archived")

        val json = jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(pageContent)

        assertEquals("""{ }""", json)
    }

    @Test
    fun `it should return empty page for no status`() {
        val pageContent = PageContentFixture.pageWithVideoSection("")

        val json = jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(pageContent)

        assertEquals("""{ }""", json)
    }

    @Test
    fun `it should serialise minimal content - no video and counselling modules`() {
        val pageContent = PageContentFixture.withNoVideoSectionAndSteps("published")
        val json = jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(pageContent)

        assertEquals(PageContentFixture.pageWithoutVideoAndStepsJson(), json)
    }

    private fun givenPageWithEmptyUrls(): Page {
        val heroImage = HeroImage("title", "intro", "")
        val image = PageImage(ImageFile("", "title"))
        val document = PageDocuments(Document("", "title"))
        val videoSection = VideoSection(
            videos = listOf(
                PageVideo(
                    videoUrl = "",
                    videoImage = "",
                    actionText = "",
                    actionLink = "",
                    isPrivate = false
                )
            )
        )
        return Page(
            "id", null, null, null,
            null, "Published",
            heroImage,
            "This is caption for completed test",
            "This is caption for partially completed test",
            listOf(image),
            listOf(document),
            videoSection,
            null,
            null,
            null,
            null,
        )
    }

    private fun givenPageWithNoUrls(): Page {
        val heroImage = HeroImage("title", "intro",  "")
        val image = PageImage(ImageFile("", "title"))
        val document = PageDocuments(Document("", "title"))
        val videoSection = VideoSection(
            videos = listOf(
                PageVideo(
                    videoUrl = null,
                    videoImage = null,
                    actionText = "",
                    actionLink = "",
                    isPrivate = false
                )
            )
        )
        return Page(
            "id", null, null, null,
            null, "Published",
            heroImage,
            "This is caption for completed test",
            "This is caption for partially completed test",
            listOf(image),
            listOf(document),
            videoSection,
            null,
            null,
            null,
            null,
        )
    }

    private fun expectedJsonWithNullAssets() = """{
  "title" : null,
  "introduction" : null,
  "description" : null,
  "summary" : null,
  "heroImageCaptionTestComplete" : "This is caption for completed test",
  "heroImageCaptionTestIncomplete" : "This is caption for partially completed test",
  "heroImage" : {
    "title" : "title",
    "introduction" : "intro",
    "imageUrl" : null
  },
  "images" : [ {
    "title" : "title",
    "imageUrl" : null
  } ],
  "documents" : [ {
    "title" : "title",
    "documentUrl" : null
  } ],
  "videoSection" : {
    "introduction" : "",
    "summary" : "",
    "videos" : [ {
      "title" : "",
      "description" : "",
      "videoUrl" : null,
      "videoImage" : null,
      "actionText" : "",
      "actionLink" : "",
      "isPrivate" : false
    } ]
  }
}"""

}
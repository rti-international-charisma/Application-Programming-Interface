package integration.api

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import integration.setup.ServerTest
import integration.setup.responses.ServiceResponse
import io.restassured.RestAssured.get
import org.junit.jupiter.api.*
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContentTest : ServerTest() {

    private lateinit var wireMockServer: WireMockServer

    @BeforeAll
    fun startWiremock() {
        wireMockServer = WireMockServer(
            WireMockConfiguration
                .options()
                .withRootDirectory("test-resources")
                .port(8055)
        )
        wireMockServer.start()
        createStub()
    }

    @AfterAll
    fun stopServer(){
        wireMockServer.resetAll()
        wireMockServer.stop()
    }

    @Test
    fun `it should fetch homepage content`() {

        val homePage = get("/home")
            .then()
            .statusCode(200)
            .extract().asString()

        assertEquals(ServiceResponse.homepage, homePage)
    }

    @Test
    fun `it should fetch assessments`() {


        val assessments = get("/assessments")
            .then()
            .statusCode(200)
            .extract().asString()

        assertEquals(ServiceResponse.assessments, assessments)
    }

    @Test
    fun `it should fetch referrals`() {


        val referrals = get("/referrals")
            .then()
            .statusCode(200)
            .extract().asString()

        assertEquals(ServiceResponse.referrals, referrals)
    }

    @Test
    fun `it should fetch counselling modules`() {

        val modules = get("/modules?partner_score=12&prep_consent=agree")
            .then()
            .statusCode(200)
            .extract().asString()

        assertEquals(ServiceResponse.modules, modules)
    }

    @Test
    fun `it should fetch a counselling module for given module id`() {

        val modules = get("/modules/prep_use")
            .then()
            .statusCode(200)
            .extract().asString()

        assertEquals(ServiceResponse.moduleForId, modules)
    }

    @Test
    fun `it should fetch page content for given page`() {

        val assessmentIntro = get("/content/assessment-intro")
            .then()
            .statusCode(200)
            .extract().asString()

        assertEquals(ServiceResponse.introPage, assessmentIntro)
    }


    private fun createStub(){
        wireMockServer.stubFor(
            addMapping("/items/pages/assessment-intro?([a-z]*)", "page.json", 2)
        )
        wireMockServer.stubFor(
            addMapping("/items/counselling_module/partner_comm?([a-z]*)",
                "counselling-modules.json", 1)
        )
        wireMockServer.stubFor(
            addMapping(
                "/items/counselling_module/(([a-z])*_([a-z])*)?([a-z]*)",
                "counselling-module-prep.json",
                2
            )
        )
        wireMockServer.stubFor(
            addMapping("/items/referrals", "referrals.json", 2)
        )
        wireMockServer.stubFor(
            addMapping("/items/sections?([a-z]*)", "assessments.json", 2)
        )
        wireMockServer.stubFor(
            addMapping("/items/homepage?([a-z]*)", "homepage.json", 2)
        )
    }

    private fun addMapping(urlPattern: String, stubFile: String, priority: Int) =
        WireMock.get(WireMock.urlPathMatching(urlPattern))
            .atPriority(priority)
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withBodyFile("responses/$stubFile")
            )
}
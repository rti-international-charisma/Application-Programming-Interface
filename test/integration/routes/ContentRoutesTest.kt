package integration.routes

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import integration.setup.ServerTest
import io.restassured.RestAssured.get
import org.junit.jupiter.api.*
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContentRoutesTest : ServerTest() {

    private lateinit var wireMockServer: WireMockServer

    @BeforeAll
    fun startWiremock() {
        wireMockServer = WireMockServer(WireMockConfiguration.options().port(8055))
        wireMockServer.start()
    }

    @BeforeEach
    fun before() {
        createStub()
    }

    @AfterEach
    fun tearDown() {
        wireMockServer.resetAll()
        wireMockServer.stop()
    }

    fun createStub() {
        wireMockServer.stubFor(
            WireMock.get(com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching("/items/homepage/*"))
                .willReturn(
                    WireMock.aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("responses/response_home.json")
                )
        )
    }

    @Test
    fun getHomePageContent() {
        val homePage = get("/home")
            .then()
           // .statusCode(200)
            .extract().asString()
        //assertEquals("""""", homePage)
    }
}
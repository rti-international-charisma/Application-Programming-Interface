//package integration.setup
//
//
//import com.rti.charisma.api.main
//import io.ktor.application.*
//import io.ktor.server.engine.*
//import io.ktor.server.netty.*
//import io.ktor.util.*
//import io.restassured.RestAssured
//import io.restassured.response.ResponseBodyExtractionOptions
//import io.restassured.specification.RequestSpecification
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import org.junit.jupiter.api.BeforeAll
//import java.util.concurrent.TimeUnit
//
//
//open class ServerTest {
//
//    protected fun RequestSpecification.When(): RequestSpecification {
//        return this.`when`()
//    }
//
//    // allows response.to<Widget>() -> Widget instance
//    protected inline fun <reified T> ResponseBodyExtractionOptions.to(): T {
//        return this.`as`(T::class.java)
//    }
//
//    companion object {
//
//        private var serverStarted = false
//
//        private lateinit var server: ApplicationEngine
//
//        @KtorExperimentalAPI
//        @ExperimentalCoroutinesApi
//        @BeforeAll
//        @JvmStatic
//        fun startServer() {
//            if (!serverStarted) {
//                server = embeddedServer(factory = Netty, port = 8080, module = Application::main)
//                server.start()
//                serverStarted = true
//
//                RestAssured.baseURI = "http://localhost"
//                RestAssured.port = 8080
//                Runtime.getRuntime().addShutdownHook(Thread { server.stop(0, 0, TimeUnit.SECONDS) })
//            }
//        }
//    }
//
//
//}
//
//

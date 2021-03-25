package util

import com.rti.charisma.api.util.hash
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class HashTest {

    @Test
    fun `it should hash string`() {
        val string = "test-string"
        assertEquals("df5c99b90b0281069d43ba1bb0ec0daf1aba516a", string.hash())
    }
}
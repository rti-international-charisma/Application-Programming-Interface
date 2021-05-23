package com.rti.charisma.api.util

import com.rti.charisma.api.config.ConfigProvider
import com.rti.charisma.api.config.HASH_SECRET_KEY
import io.ktor.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object Hash {
    fun stringHash(str: String): String {
        val hashKey = ConfigProvider.get(HASH_SECRET_KEY).toByteArray()

        val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")

        val hmac = Mac.getInstance("HmacSHA1")
        hmac.init(hmacKey)
        return hex(hmac.doFinal(str.toByteArray(Charsets.UTF_8)))
    }
}

fun String.hash(): String {
    return Hash.stringHash(this)
}

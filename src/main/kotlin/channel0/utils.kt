package channel0

import channel0.domain.model.DevicePrincipal
import org.slf4j.Logger
import org.springframework.security.core.context.SecurityContextHolder
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64


inline fun <T> logTiming(actionName: String, log: Logger, block: () -> T): T {
    val start = System.currentTimeMillis()
    val result = block()
    val end = System.currentTimeMillis()
    println("$actionName took ${end - start} ms") // or use logger.info(...)
    return result
}

object AuthUtils {
    fun getUserId(): Long {
        val principal = SecurityContextHolder.getContext().authentication.principal as DevicePrincipal
        return principal.userId
    }

    fun getDeviceId(): String {
        val principal = SecurityContextHolder.getContext().authentication.principal as DevicePrincipal
        return principal.deviceId
    }

    fun generateDeviceToken(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    fun hashToken(token:String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(token.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(hash)
    }
}


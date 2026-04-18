package channel0.config.rateLimit

import channel0.backend.data.config.BucketWrapper
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Component
class RateLimiterService {

    private val log = LoggerFactory.getLogger(RateLimiterService::class.java)

    private val buckets = ConcurrentHashMap<String, BucketWrapper>()

    private val EXPIRATION_TIME = 30 * 60 * 1000L // 30 minutes

    private fun newBucket(): Bucket {
        val limit = Bandwidth.simple(20, Duration.ofSeconds(1))
        return Bucket.builder()
            .addLimit(limit)
            .build()
    }

    fun tryConsume(key: String): Boolean {
        val now = System.currentTimeMillis()

        val wrapper = buckets.compute(key) { _, existing ->
            if (existing == null) {
                BucketWrapper(newBucket(), now)
            } else {
                existing.lastAccess = now
                existing
            }
        }!!
        log.debug("rate filter hit")

        return wrapper.bucket.tryConsume(1)
    }

    @Scheduled(fixedRate = 10 * 60 * 1000) // every 10 minutes
    fun cleanup() {
        val now = System.currentTimeMillis()

        buckets.entries.removeIf { (_, wrapper) ->
            now - wrapper.lastAccess > EXPIRATION_TIME
        }
    }

}
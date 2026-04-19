package channel0.backend.data.config

import io.github.bucket4j.Bucket

data class BucketWrapper(
    val bucket: Bucket,
    @Volatile var lastAccess: Long
)

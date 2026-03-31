package channel0.backend.data.dto.request

data class SegmentRequest(
    val videoId: String,
    val fallbackVideoID: String?
)
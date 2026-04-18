package channel0.data.dto.request

data class SegmentRequest(
    val videoId: String,
    val fallbackVideoID: String?
)
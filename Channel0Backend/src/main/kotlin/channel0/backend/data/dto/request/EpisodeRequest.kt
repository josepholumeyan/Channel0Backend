package channel0.backend.data.dto.request

data class EpisodeRequest(
    val id: String,
    val number: Int,
    val segments: List<SegmentRequest>
)


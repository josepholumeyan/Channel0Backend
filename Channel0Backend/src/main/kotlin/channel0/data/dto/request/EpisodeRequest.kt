package channel0.backend.data.dto.request

import channel0.data.dto.request.SegmentRequest

data class EpisodeRequest(
    val id: String,
    val number: Int,
    val segments: List<SegmentRequest>
)


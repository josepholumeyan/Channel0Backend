package channel0.data.dto.request

import channel0.backend.data.dto.request.EpisodeRequest

data class SeasonRequest(
    val id: String,
    val number: Int,
    val episodes: List<EpisodeRequest>
)

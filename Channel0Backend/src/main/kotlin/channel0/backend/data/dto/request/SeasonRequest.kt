package channel0.backend.data.dto.request

data class SeasonRequest(
    val id: String,
    val number: Int,
    val episodes: List<EpisodeRequest>
)

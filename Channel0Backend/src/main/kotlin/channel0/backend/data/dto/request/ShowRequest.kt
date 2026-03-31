package channel0.backend.data.dto.request

data class ShowRequest(
    val id: String,
    val name: String,
    val seasons: List<SeasonRequest>
)


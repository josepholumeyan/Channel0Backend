package channel0.data.dto.request

data class ChannelRequest(
    val id: String,
    val name: String,
    val shows: List<ShowRequest>,
    val clipShows: List<ShowRequest> = emptyList()
)
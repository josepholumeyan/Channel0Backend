package channel0.backend.data.dto.request

import channel0.data.dto.request.ShowRequest

data class ChannelRequest(
    val id: String,
    val name: String,
    val shows: List<ShowRequest>,
    val clipShows: List<ShowRequest> = emptyList()
)
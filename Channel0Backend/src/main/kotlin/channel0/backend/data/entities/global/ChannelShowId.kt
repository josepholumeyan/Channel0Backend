package channel0.backend.data.entities.global

import jakarta.persistence.Embeddable

@Embeddable
data class ChannelShowId(
    val channelId: String = "",
    val showId: String = ""
)
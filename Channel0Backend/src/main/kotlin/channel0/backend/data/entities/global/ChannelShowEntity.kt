package channel0.backend.data.entities.global

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "channel_shows")
data class ChannelShowEntity(
    @EmbeddedId
    val id: ChannelShowId,

    val name: String,

    val orderIndex: Int,

    var clip: Boolean = false
)
package channel0.backend.data.entities.global

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "channels")
data class ChannelEntity(
    @Id
    val id: String,
    val name: String,
    var showCount : Int = 0
)
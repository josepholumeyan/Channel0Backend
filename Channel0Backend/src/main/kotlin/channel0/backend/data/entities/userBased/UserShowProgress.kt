package channel0.backend.data.entities.userBased

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "User_Show_Progress")
data class UserShowProgress(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO )
    val id: Long?,
    val userId: Long,
    var showId: String,
    var currentSeasonId: String?,
    var currentEpisodeId: String?,
    var currentSegmentId: Long?,
    var currentSeasonIndex: Int = 0,
    var currentEpisodeIndex: Int = 0,
    var currentSegmentIndex: Int = 0
)

package channel0.data.entities.userBased

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "User_Show_Progress",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "show_id"])]
)
data class UserShowProgress(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
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

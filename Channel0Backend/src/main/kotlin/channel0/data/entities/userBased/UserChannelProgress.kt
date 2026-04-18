package channel0.data.entities.userBased

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(name="User_Channel_Progress",
    uniqueConstraints = [UniqueConstraint(columnNames = ["channel_id","user_id"])]
)
data class UserChannelProgress(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long?,
    val userId : Long,
    val channelId : String,
    val name : String,
    var currentShowId : String?,
    var currentClipShowId : String?,
    var currentShowIndex : Int = 0,
    var currentClipShowIndex : Int = 0,
    var episodesPlayedInCurrentShow: Int = 0
)
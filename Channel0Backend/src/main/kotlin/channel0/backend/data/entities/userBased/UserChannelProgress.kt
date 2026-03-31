package channel0.backend.data.entities.userBased

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name="User_Channel_Progress")
data class UserChannelProgress(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
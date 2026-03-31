package channel0.backend.data.entities.userBased

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "User_Channel_Show")
data class UserChannelShow(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    val id : Long?,
    val userId : Long,
    val name : String,
    val channelId : String,
    val showId : String,
    var position : Int,
    var clip : Boolean,
    var enabled : Boolean
)

package channel0.data.repositories

import channel0.data.entities.userBased.UserChannelProgress
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserChannelProgressRepository : JpaRepository<UserChannelProgress, Long> {

    fun findByChannelIdAndUserId(
        channelId: String,
        userId: Long
    ): UserChannelProgress?
}
package channel0.data.repositories

import channel0.data.entities.userBased.UserChannelShow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserChannelShowRepository : JpaRepository <UserChannelShow, Long> {

    /**
     * get User Enabled Shows
     */
    fun findByChannelIdAndUserIdAndClipFalseAndEnabledTrueOrderByPosition(
        channelId: String,
        userId: Long
    ): List<UserChannelShow>

    /**
     * get User Enabled ClipShows
     */
    fun findByChannelIdAndUserIdAndClipTrueAndEnabledTrueOrderByPosition(
        channelId: String,
        userId: Long
    ): List<UserChannelShow>

    /**
     * get All User ChannelShows
     */
    fun findByChannelIdAndUserIdOrderByPosition(
        channelId: String,
        userId: Long
    ): List<UserChannelShow>

    fun findByShowIdAndUserId (showId: String,userId: Long): UserChannelShow?


    /**
     * get A User ChannelShow
     */
    fun findByChannelIdAndShowIdAndUserId(
        channelId: String,
        showId: String,
        userId: Long
    ): UserChannelShow?
}
package channel0.data.repositories

import channel0.data.entities.userBased.UserChannelShow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
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

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    update UserChannelShow u
    set u.enabled = :enabled
    where u.channelId = :channelId
      and u.userId = :userId
      and u.showId in :showIds
""")
    fun updateEnabledForShows(
        channelId: String,
        userId: Long,
        showIds: List<String>,
        enabled: Boolean
    ): Int


    @Modifying
    @Query("DELETE FROM UserChannelShow ucs WHERE ucs.userId = :userId")
    fun deleteUserChannelShows(@Param("userId") userId: Long)
}
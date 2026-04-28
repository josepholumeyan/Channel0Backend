package channel0.data.repositories

import channel0.data.entities.userBased.UserChannelProgress
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserChannelProgressRepository : JpaRepository<UserChannelProgress, Long> {

    fun findByChannelIdAndUserId(
        channelId: String,
        userId: Long
    ): UserChannelProgress?

    @Modifying
    @Query("DELETE FROM UserChannelProgress ucp WHERE ucp.userId = :userId")
    fun deleteUserChannelProgress(@Param("userId") userId: Long)
}
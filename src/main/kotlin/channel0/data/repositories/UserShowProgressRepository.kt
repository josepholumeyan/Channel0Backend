package channel0.data.repositories

import channel0.data.entities.userBased.UserShowProgress
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserShowProgressRepository : JpaRepository<UserShowProgress, Long> {

    //    get User's Show progress
    fun findByShowIdAndUserId (showId:String, userId:Long) : UserShowProgress?

    @Transactional
    fun deleteByShowIdAndUserId(showId: String,userId: Long)

    @Modifying
    @Query("DELETE FROM UserShowProgress usp WHERE usp.userId = :userId")
    fun deleteUserShowsProgress(@Param("userId") userId: Long)
}
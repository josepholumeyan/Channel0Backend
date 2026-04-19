package channel0.data.repositories

import channel0.data.entities.userBased.UserShowProgress
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserShowProgressRepository : JpaRepository<UserShowProgress, Long> {

    //    get User's Show progress
    fun findByShowIdAndUserId (showId:String, userId:Long) : UserShowProgress?

    @Transactional
    fun deleteByShowIdAndUserId(showId: String,userId: Long)
}
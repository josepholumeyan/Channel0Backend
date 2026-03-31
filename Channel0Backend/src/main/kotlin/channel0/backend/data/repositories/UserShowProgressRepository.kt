package channel0.backend.data.repositories

import channel0.backend.data.entities.userBased.UserShowProgress
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserShowProgressRepository : JpaRepository<UserShowProgress, Long> {

    //    get User's Show progress
    fun findByShowIdAndUserId (showId:String, userId:Long) : UserShowProgress?
}
package channel0.data.repositories

import channel0.data.entities.userBased.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity,Long> {
}
package channel0.backend.data.repositories

import channel0.backend.data.entities.userBased.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity,Long> {
}
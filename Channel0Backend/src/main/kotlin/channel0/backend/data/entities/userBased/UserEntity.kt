package channel0.backend.data.entities.userBased

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "Users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    val id: Long?
)

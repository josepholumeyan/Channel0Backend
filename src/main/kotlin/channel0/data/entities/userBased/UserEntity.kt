package channel0.data.entities.userBased

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(name = "Users",
    uniqueConstraints = [UniqueConstraint(columnNames = ["id"])]
    )
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    val id: Long?
)

package channel0.data.entities.userBased

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.sql.Timestamp

@Entity
@Table(name = "Devices",
    uniqueConstraints = [UniqueConstraint(columnNames = ["deviceId"])]
)
data class DeviceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    val id: Long?,
    val deviceId : String,
    var deviceToken : String,
    val userId: Long,
    var timeRegistered: Timestamp,
    var lastSeen: Timestamp
)
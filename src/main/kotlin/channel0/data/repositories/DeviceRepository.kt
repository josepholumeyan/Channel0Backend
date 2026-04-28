package channel0.data.repositories


import channel0.data.entities.userBased.DeviceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DeviceRepository : JpaRepository<DeviceEntity, Long> {

    @Query("""
        SELECT d.userId 
        FROM DeviceEntity d 
        WHERE d.deviceId = :deviceId 
        AND d.deviceToken = :deviceToken
    """)
    fun findUserIdByDevice(
        @Param("deviceId") deviceId: String,
        @Param("deviceToken") deviceToken: String
    ): Long?

    fun findByDeviceId(deviceId: String): DeviceEntity?

    @Modifying
    @Query("UPDATE DeviceEntity d set d.lastSeen = CURRENT_TIMESTAMP WHERE d.deviceId = :deviceId")
    fun touchLastSeen(@Param("deviceId") deviceId: String)

    @Modifying
    @Query("DELETE FROM DeviceEntity d WHERE d.userId = :userId")
    fun deleteUserDevices(@Param("userId") userId: Long)
}
package channel0.domain.services

import channel0.AuthUtils
import channel0.data.entities.userBased.DeviceEntity
import channel0.data.repositories.DeviceRepository
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.sql.Timestamp
import java.time.Instant

@Service
class DeviceService(
    private val deviceRepository: DeviceRepository
) {

    fun registerDevice(deviceId: String,userId:Long):String {
        val existingDevice = deviceRepository.findByDeviceId(deviceId)

        if(existingDevice!=null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Device id already exists")
        }

        val rawToken = AuthUtils.generateDeviceToken()
        val hashedToken = AuthUtils.hashToken(rawToken)

        deviceRepository.save(
            DeviceEntity(
                id=null,
                deviceId = deviceId,
                deviceToken = hashedToken,
                userId = userId,
                timeRegistered = Timestamp.from(Instant.now()),
                lastSeen = Timestamp.from(Instant.now())
            )
        )

        return rawToken
    }

    fun findUserIdForDevice(deviceId: String,deviceToken:String):Long? {
        val hashedToken = AuthUtils.hashToken(deviceToken)
        return deviceRepository.findUserIdByDevice(deviceId,hashedToken)
    }

    @Transactional
    fun touchLastSeen(deviceId: String){
        deviceRepository.touchLastSeen(deviceId)
    }
}
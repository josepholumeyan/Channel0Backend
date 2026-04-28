package channel0.controller

import channel0.AuthUtils
import channel0.config.security.annotation.PublicEndpoint
import channel0.data.dto.responses.DeviceToken
import channel0.domain.services.DeviceService
import channel0.domain.services.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
    private val deviceService: DeviceService
) {

    @PublicEndpoint
    @PostMapping
    fun createUser(
        @RequestParam deviceId : String
    ): DeviceToken {
       val userId = userService.createUser()
       val token = deviceService.registerDevice(deviceId,userId)
       return DeviceToken(token)
    }

    @PostMapping("/delete")
    fun clearUserData(){
        userService.deleteUserData(AuthUtils.getUserId())
    }

}

package channel0.config.security

import channel0.config.security.annotation.PublicEndpoint
import channel0.domain.model.DevicePrincipal
import channel0.domain.services.DeviceService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class DeviceAuthInterceptor(
    private val deviceService: DeviceService
) : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {

        val uri = request.requestURI

        if (uri.startsWith("/admin")) {
            return true
        }

        if (handler is HandlerMethod) {
            val isPublic =
                handler.hasMethodAnnotation(PublicEndpoint::class.java) ||
                        handler.beanType.isAnnotationPresent(PublicEndpoint::class.java)

            if (isPublic) {
                return true
            }
        }

        val deviceId = request.getHeader("X-Device-Id")
        val token = request.getHeader("X-Device-Token")

        if (deviceId.isNullOrBlank() || token.isNullOrBlank()) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            return false
        }

        val userId = deviceService.findUserIdForDevice(deviceId,token)

        if (userId == null) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            return false
        }

        deviceService.touchLastSeen(deviceId)

        val principal = DevicePrincipal(
            userId,deviceId
        )

        val authentication = UsernamePasswordAuthenticationToken(
            principal,
            null,
            emptyList()
        )

        SecurityContextHolder.getContext().authentication = authentication

        return true
    }

}

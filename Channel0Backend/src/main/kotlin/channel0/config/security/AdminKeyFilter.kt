package channel0.config.security

import channel0.controller.PlaybackController
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class AdminKeyFilter : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(PlaybackController::class.java)

    @Value("\${admin.key}")
    lateinit var adminKey: String

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        log.debug("filter hit")
        // only protect admin routes
        if (request.requestURI.startsWith("/admin")) {
            log.debug("filter hit for admin")
            val key = request.getHeader("X-ADMIN-KEY")


            if (key != adminKey) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("Unauthorized")
                return
            }
        }

        filterChain.doFilter(request, response)
    }
}
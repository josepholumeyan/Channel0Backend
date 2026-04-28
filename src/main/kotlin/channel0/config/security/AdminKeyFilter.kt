package channel0.config.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Order(1)
@Component
class AdminKeyFilter : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(AdminKeyFilter::class.java)

    @Value("\${admin.key}")
    lateinit var adminKey: String

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val uri = request.requestURI

        // ONLY admin routes
        if (!uri.startsWith("/admin")) {
            filterChain.doFilter(request, response)
            return
        }

        log.debug("Admin filter hit for: $uri")

        val key = request.getHeader("X-ADMIN-KEY")

        if (key == null || key != adminKey) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Unauthorized")
            return
        }

        filterChain.doFilter(request, response)
    }
}

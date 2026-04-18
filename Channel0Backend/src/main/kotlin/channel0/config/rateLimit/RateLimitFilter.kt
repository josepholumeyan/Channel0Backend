package channel0.config.rateLimit

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class RateLimitFilter(
    private val rateLimiterService: RateLimiterService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val ip = request.getHeader("X-Forwarded-For")
            ?.split(",")
            ?.first()
            ?.trim()
            ?: request.remoteAddr

        val allowed = rateLimiterService.tryConsume(ip)

        if (!allowed) {
            response.status = 429
            response.writer.write("Too many requests")
            return
        }

        filterChain.doFilter(request, response)
    }
}

package channel0.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import kotlin.jvm.java

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val adminKeyFilter: AdminKeyFilter
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }

            // FULLY disable default security behavior
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .logout { it.disable() }

            .authorizeHttpRequests {
                it.requestMatchers("/admin/**").permitAll()  // allow, filter will handle auth
                it.anyRequest().permitAll()
            }

            // put filter EARLY in chain
            .addFilterBefore(adminKeyFilter, UsernamePasswordAuthenticationFilter::class.java)

            .build()
    }
}

package channel0

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Channel0BackendApplication

fun main(args: Array<String>) {
    runApplication<Channel0BackendApplication>(*args)
}
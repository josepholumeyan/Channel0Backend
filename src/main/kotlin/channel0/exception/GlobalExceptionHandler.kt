package channel0.exception

import channel0.data.dto.responses.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log =
        LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(AppException::class)
    fun handleAppException(e: AppException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(e.status)
            .body(
                ErrorResponse(
                    message = e.message,
                    status = e.status
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error("Unhandled exception", e)

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorResponse(
                    message = "Internal server error",
                    status = 500
                )
            )
    }
}

package channel0.exception

open class AppException (val status: Int,
                         override val message: String
    ): RuntimeException(message)

class BadRequestException(message: String): AppException(400,message)


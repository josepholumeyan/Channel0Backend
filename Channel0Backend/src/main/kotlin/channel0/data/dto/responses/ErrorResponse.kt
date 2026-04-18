package channel0.data.dto.responses

data class ErrorResponse(
    val message: String,
    val status : Int,
    val timestamp:Long = System.currentTimeMillis()
)

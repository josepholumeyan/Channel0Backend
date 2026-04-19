package channel0.data.dto.responses

data class ShowDto(
    val id:String,
    val showName : String,
    val enabled : Boolean,
    val clip : Boolean,
    val position: Int
)
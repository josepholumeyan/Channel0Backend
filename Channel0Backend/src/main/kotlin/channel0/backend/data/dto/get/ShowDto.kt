package channel0.backend.data.dto.get

data class ShowDto(
    val id:String,
    val showName : String,
    val enabled : Boolean,
    val clip : Boolean,
    val position: Int
)
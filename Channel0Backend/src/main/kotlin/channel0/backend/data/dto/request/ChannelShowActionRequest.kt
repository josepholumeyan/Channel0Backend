package channel0.backend.data.dto.request

data class ChannelShowActionRequest(
    val userId:Long,
    val showIds: List<String>
)

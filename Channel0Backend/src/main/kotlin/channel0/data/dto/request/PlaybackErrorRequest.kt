package channel0.data.dto.request

data class PlaybackErrorRequest(
    val videoId : String,
    val episodeId : String,
    val channelId : String,
    val userId: Long,
    val showName: String,
    val showId : String
)

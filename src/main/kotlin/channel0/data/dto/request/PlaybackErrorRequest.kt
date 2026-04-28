package channel0.data.dto.request

data class PlaybackErrorRequest(
    val videoId : String,
    val episodeId : String,
    val channelId : String,
    val showName: String,
    val showId : String
)

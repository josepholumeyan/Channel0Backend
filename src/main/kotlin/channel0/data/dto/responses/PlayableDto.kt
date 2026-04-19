package channel0.data.dto.responses

data class PlayableDto(
    val showName : String,
    val showId : String,
    val clipShow : Boolean,
    val episodeId: String,
    val videoId : String,
    val fallbackId : String?,
    val orderIndex : Int
)
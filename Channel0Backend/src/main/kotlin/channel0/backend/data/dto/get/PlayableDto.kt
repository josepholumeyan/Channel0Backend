package channel0.backend.data.dto.get

data class PlayableDto(
    val showName : String,
    val showId : String,
    val clipShow : Boolean,
    val episodeId: String,
    val videoId : String,
    val fallbackId : String?,
    val orderIndex : Int
)
package channel0.domain.services

import channel0.data.dto.request.PlaybackErrorRequest
import channel0.data.dto.responses.PlayableDto
import channel0.data.entities.userBased.UserChannelProgress
import channel0.data.entities.userBased.UserShowProgress
import channel0.data.repositories.EpisodeRepository
import channel0.data.repositories.UserShowProgressRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class ShowService(
    private val playbackService: PlaybackService,
    private val episodeRepository: EpisodeRepository,
    private val userShowProgressRepository: UserShowProgressRepository
) {

    private val log = LoggerFactory.getLogger(ShowService::class.java)

    /**
     * Logs that a playable is broken
     * Disables an episode when a segment playable within it is broken
     * clears the lookup convenience (currentEpisodeId) to prevent direct fetching of the disabled episode (currentEpisodeIndex ensures progression will continue smoothly)
     * Skips to the next show
     */
    @Transactional
    fun handleBrokenPlayable(
        playbackErrorRequest: PlaybackErrorRequest,
        channelProgress: UserChannelProgress,
        userShowProgress: UserShowProgress
    ): PlayableDto {

        log.warn(
            "Playable failed. videoId={}, episodeId={}, show={}",
            playbackErrorRequest.videoId,
            playbackErrorRequest.episodeId,
            playbackErrorRequest.showName
        )

        val rowsAffected = episodeRepository.disableEpisode(playbackErrorRequest.episodeId)

        if (rowsAffected > 0) {
            log.warn("Episode disabled. episodeId={}", playbackErrorRequest.episodeId)
        } else {
            log.error("Failed to disable episode. episodeId={}", playbackErrorRequest.episodeId)
        }

        userShowProgress.currentEpisodeId = null
        userShowProgressRepository.save(userShowProgress)

        return playbackService.goToNextShow(channelProgress)
    }
}

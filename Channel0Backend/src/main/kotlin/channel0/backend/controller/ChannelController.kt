package channel0.backend.controller

import channel0.backend.data.dto.get.ChannelDto
import channel0.backend.data.dto.get.PlayableDto
import channel0.backend.data.dto.get.ShowDto
import channel0.backend.data.dto.request.ChannelShowActionRequest
import channel0.backend.data.repositories.UserChannelProgressRepository
import channel0.backend.data.repositories.UserShowProgressRepository
import channel0.backend.domain.services.ChannelService
import channel0.backend.domain.services.Mapper
import channel0.backend.domain.services.ShowService
import channel0.backend.domain.services.UserService
import channel0Backend.channel0.backend.logTiming
import org.apache.juli.logging.Log
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Clock.system

@RestController
@RequestMapping("/channels")
class ChannelController(
    private val channelService: ChannelService,
    private val showService: ShowService,
    private val userService: UserService,
    private val userShowProgressRepository: UserShowProgressRepository,
    private val userChannelProgressRepository: UserChannelProgressRepository,
    private val mapper: Mapper
) {

    /**
     * Creates a new user if they don't already exist.
     * Returns the user ID.
     * Note: Currently passing null in the call — you might want to accept a userId parameter in real use.
     */
    @PostMapping("/user/create")
    fun createUser(): Long {
        return userService.createUser()
    }

    /**
     * Returns a list of all channels as DTOs.
     */
    @GetMapping
    fun getChannels(): List<ChannelDto> {
        return mapper.channelToDto(channelService.getChannels())
    }

    /**
     * Returns all shows (enabled or disabled) for a given channel and user.
     */
    @GetMapping("/{channelId}/shows")
    fun getChannelShows(
        @PathVariable channelId: String,
        @RequestParam userId: Long
    ): List<ShowDto> {
        return mapper.userChannelShowToDto(channelService.getChannelShows(channelId, userId))
    }

    /**
     * Returns only the enabled shows for a given channel and user.
     */
    @GetMapping("/{channelId}/shows/enabled")
    fun getEnabledChannelShows(
        @PathVariable channelId: String,
        @RequestParam userId: Long
    ): List<ShowDto> {
        return mapper.userChannelShowToDto(channelService.getEnabledChannelShows(channelId, userId))
    }

    /**
     * Disables a channel show for the user.
     */
    @PostMapping("/{channelId}/shows/disable")
    fun disableShow(
        @PathVariable channelId: String,
        @RequestBody request: ChannelShowActionRequest
    ) {
        channelService.disableShow(channelId, request.showIds, request.userId)
    }

    /**
     * Enables a previously disabled channel show for the user.
     */
    @PostMapping("/{channelId}/shows/enable")
    fun enableShow(
        @PathVariable channelId: String,
        @RequestBody request: ChannelShowActionRequest
    ) {
        channelService.enableShow(channelId, request.showIds, request.userId)
    }

    /**
     * Returns the next segment to play for the user.
     * Handles lazy creation of channel and show progress if missing.
     */
    @GetMapping("/playback/getNextSegment")
    fun getNextSegment(
        @RequestParam channelId: String,
        @RequestParam userId: Long
    ): PlayableDto {
        val channelProgress = userChannelProgressRepository.findByChannelIdAndUserId(channelId, userId)
            ?: userService.createUserChannelProgress(channelId, userId)

        val showId = channelProgress.currentShowId
            ?: showService.resolveCurrentChannelShowId(channelProgress)

        val showProgress = userShowProgressRepository.findByShowIdAndUserId(showId, userId)
            ?: userService.createUserShowProgress(showId, userId)

        return showService.getNextSegment(channelProgress, showProgress)
    }

    /**
     * Skips to the next episode within the current show.
     */
    @GetMapping("/playback/goToNextEpisode")
    fun goToNextEpisode(
        @RequestParam channelId: String,
        @RequestParam userId: Long
    ): PlayableDto {
        val channelProgress = logTiming("findOrCreateChannelProgress") {
            userChannelProgressRepository.findByChannelIdAndUserId(channelId, userId)
                ?: userService.createUserChannelProgress(channelId, userId)
        }

        val showId = logTiming("findOrCreateCurrenShowId") {
            channelProgress.currentShowId
                ?: showService.resolveCurrentChannelShowId(channelProgress)
        }

        val showProgress = logTiming("findOrCreateShowrogress") {
            userShowProgressRepository.findByShowIdAndUserId(showId, userId)
                ?: userService.createUserShowProgress(showId, userId)
        }

        val nextEpisode =
            logTiming("goToNextEpisode") {
                showService.goToNextEpisode(channelProgress, showProgress)
            }

        return nextEpisode
    }

    /**
     * Skips to the next show within the current channel.
     * Returns the first playable segment of that show.
     */
    @GetMapping("/playback/goToNextShow")
    fun goToNextShow(
        @RequestParam channelId: String,
        @RequestParam userId: Long
    ): PlayableDto {
        val channelProgress = userChannelProgressRepository.findByChannelIdAndUserId(channelId, userId)
            ?: userService.createUserChannelProgress(channelId, userId)

        return showService.goToNextShow(channelProgress)
    }

    /**
     * Disables a show during playback and immediately moves to the next available show.
     * Minimizes API calls by returning the next playable segment.
     */
    @PostMapping("playback/disableShowDuringPlayback")
    fun disableShowDuringPlayback(
        @RequestParam channelId: String,
        @RequestBody request: ChannelShowActionRequest

    ): PlayableDto {
        println("disabling shows at ${System.currentTimeMillis()}")
        disableShow(channelId, request)
        println("returning playable at ${System.currentTimeMillis()}")
        return goToNextShow(channelId, request.userId)
    }
}

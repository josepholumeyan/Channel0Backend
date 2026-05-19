package channel0.controller

import channel0.AuthUtils
import channel0.data.dto.request.ChannelShowActionRequest
import channel0.data.dto.request.PlaybackErrorRequest
import channel0.data.dto.responses.PlayableDto
import channel0.domain.services.ChannelService
import channel0.domain.services.PlaybackService
import channel0.domain.services.ShowService
import channel0.domain.services.UserService
import channel0.logTiming
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/playback")
class PlaybackController(
    private val playbackService: PlaybackService,
    private val userService: UserService,
    private val channelService : ChannelService,
    private val showService: ShowService
) {

    private val log = LoggerFactory.getLogger(PlaybackController::class.java)

    @GetMapping("/next")
    fun getNextSegment(
        @RequestParam channelId: String,
    ): PlayableDto {

        println("Starting request time is ${System.currentTimeMillis()}")

        val userId = AuthUtils.getUserId()

        val channelProgress = userService.getOrCreateUserChannelProgress(channelId, userId)

        log.debug("ChannelProgress → showId=${channelProgress.currentShowId}, index=${channelProgress.currentShowIndex}")

        val showId = channelProgress.currentShowId
            ?: playbackService.resolveCurrentChannelShowId(channelProgress)

        val showProgress = userService.getOrCreateUserShowProgress(showId, userId)

        val next = logTiming("getNextSegment",log) { playbackService.getNextSegment(channelProgress, showProgress) }

        log.debug("Returning segment → show=${next.showName}, index=${next.orderIndex}, videoId=${next.videoId}")

        return next
    }

    @GetMapping("/next-episode")
    fun goToNextEpisode(
        @RequestParam channelId: String,
    ): PlayableDto {

        val userId = AuthUtils.getUserId()

        val channelProgress = userService.getOrCreateUserChannelProgress(channelId, userId)

        val showId = channelProgress.currentShowId
            ?: playbackService.resolveCurrentChannelShowId(channelProgress)

        val showProgress = userService.getOrCreateUserShowProgress(showId, userId)

        return logTiming("goToNextEpisode",log) { playbackService.goToNextEpisode(channelProgress, showProgress) }
    }

    @GetMapping("/next-show")
    fun goToNextShow(
        @RequestParam channelId: String,
    ): PlayableDto {

        val channelProgress =  userService.getOrCreateUserChannelProgress(channelId, AuthUtils.getUserId())

        return logTiming("goToNextShow",log) { playbackService.goToNextShow(channelProgress) }
    }

    @PostMapping("/disable-show")
    fun disableShowDuringPlayback(
        @RequestParam channelId: String,
        @RequestBody request: ChannelShowActionRequest
    ): PlayableDto {

        val userId = AuthUtils.getUserId()

        channelService.disableShow(channelId, request.showIds, userId)

        val channelProgress = userService.getOrCreateUserChannelProgress(channelId, userId)

        return logTiming("Disabling show during PlayBack",log){playbackService.goToNextShow(channelProgress)}
    }

    @PostMapping("/error")
    fun handleBrokenPlayable(
        @RequestBody request: PlaybackErrorRequest
    ): PlayableDto {

        val userId = AuthUtils.getUserId()

        val channelProgress = userService.getOrCreateUserChannelProgress(request.channelId, userId)

        val showProgress =  userService.getOrCreateUserShowProgress(request.showId, userId,request.showName)

        log.warn("Broken playable → videoId=${request.videoId}, show=${request.showName}")

        return logTiming("Handling Broken Playable",log) {
            showService.handleBrokenPlayable(
                request,
                channelProgress,
                showProgress
            )
        }
    }
}

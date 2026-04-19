package channel0.controller

import channel0.data.dto.responses.ChannelDto
import channel0.data.dto.responses.ShowDto
import channel0.backend.data.dto.request.ChannelShowActionRequest
import channel0.domain.services.ChannelService
import channel0.domain.services.Mapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/channels")
class ChannelController(
    private val channelService: ChannelService,
    private val mapper: Mapper
) {

    @GetMapping
    fun getChannels(): List<ChannelDto> {
        return mapper.channelToDto(channelService.getChannels())
    }

    @GetMapping("/{channelId}/shows")
    fun getChannelShows(
        @PathVariable channelId: String,
        @RequestParam userId: Long
    ): List<ShowDto> {
        return mapper.userChannelShowToDto(
            channelService.getChannelShows(channelId, userId)
        )
    }

    @GetMapping("/{channelId}/shows/enabled")
    fun getEnabledChannelShows(
        @PathVariable channelId: String,
        @RequestParam userId: Long
    ): List<ShowDto> {
        return mapper.userChannelShowToDto(
            channelService.getEnabledChannelShows(channelId, userId)
        )
    }

    @PostMapping("/{channelId}/shows/disable")
    fun disableShow(
        @PathVariable channelId: String,
        @RequestBody request: ChannelShowActionRequest
    ) {
        channelService.disableShow(channelId, request.showIds, request.userId)
    }

    @PostMapping("/{channelId}/shows/enable")
    fun enableShow(
        @PathVariable channelId: String,
        @RequestBody request: ChannelShowActionRequest
    ) {
        channelService.enableShow(channelId, request.showIds, request.userId)
    }
}


package channel0.backend.domain.services

import channel0.backend.data.dto.get.ChannelDto
import channel0.backend.data.dto.get.ShowDto
import channel0.backend.data.entities.global.ChannelEntity
import channel0.backend.data.entities.userBased.UserChannelShow
import org.springframework.stereotype.Service

@Service
class Mapper {
    fun channelToDto(channelEntities: List<ChannelEntity>): List<ChannelDto> {
        val channelDTOs = channelEntities.map {
            ChannelDto(it.id,it.name,it.showCount)
        }
        return channelDTOs
    }

    fun userChannelShowToDto(userChannelShows: List<UserChannelShow>):List<ShowDto>{
        val showDTOs = userChannelShows.map{
            ShowDto(it.showId,it.name,it.enabled,it.clip,it.position)
        }
        return showDTOs
    }

}
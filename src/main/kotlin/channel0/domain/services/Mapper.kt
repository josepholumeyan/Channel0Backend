package channel0.domain.services

import channel0.data.dto.responses.ChannelDto
import channel0.data.dto.responses.EpisodeAdminDto
import channel0.data.dto.responses.ShowDto
import channel0.data.entities.global.ChannelEntity
import channel0.data.entities.global.EpisodeEntity
import channel0.data.entities.userBased.UserChannelShow
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

    fun episodeToAdminDto( episodes: List<EpisodeEntity> ): List<EpisodeAdminDto>{
        val episodeDTOs = episodes.map{
            EpisodeAdminDto(it.id,it.show.id,it.enabled )
        }
        return episodeDTOs
    }

}
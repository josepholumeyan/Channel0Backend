package channel0.domain.services

import channel0.data.dto.request.PopulateRequest
import channel0.data.dto.request.ShowRequest
import channel0.data.entities.global.ChannelEntity
import channel0.data.entities.global.ChannelShowEntity
import channel0.data.entities.global.ChannelShowId
import channel0.data.entities.global.EpisodeEntity
import channel0.data.entities.global.SeasonEntity
import channel0.data.entities.global.SegmentEntity
import channel0.data.entities.global.ShowEntity
import channel0.data.repositories.ChannelRepository
import channel0.data.repositories.ChannelShowRepository
import channel0.data.repositories.DeviceRepository
import channel0.data.repositories.EpisodeRepository
import channel0.data.repositories.SeasonRepository
import channel0.data.repositories.SegmentRepository
import channel0.data.repositories.ShowRepository
import channel0.data.repositories.UserRepository
import channel0.domain.validation.validate
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class AdminService(
    private val channelRepo: ChannelRepository,
    private val userRepo: UserRepository,
    private val deviceRepo: DeviceRepository,
    private val showRepo: ShowRepository,
    private val seasonRepo: SeasonRepository,
    private val episodeRepo: EpisodeRepository,
    private val segmentRepo: SegmentRepository,
    private val channelShowRepo: ChannelShowRepository
) {

    @Transactional
    fun populateDatabase(request: PopulateRequest) {

        validate(request)
        clearDatabase()

        request.channels.forEach { channelReq ->

            val channel = channelRepo.save(
                ChannelEntity(
                    id = channelReq.id,
                    name = channelReq.name
                )
            )

            processShows(
                channelId = channel.id,
                shows = channelReq.shows,
                clip = false
            )

            processShows(
                channelId = channel.id,
                shows = channelReq.clipShows,
                clip = true
            )
        }
    }

    private fun processShows(
        channelId: String,
        shows: List<ShowRequest>,
        clip: Boolean
    ) {

        shows.forEachIndexed { index, showReq ->

            val show = showRepo.save(
                ShowEntity(
                    id = showReq.id,
                    name = showReq.name,
                    clip = clip
                )
            )

            channelShowRepo.save(
                ChannelShowEntity(
                    id = ChannelShowId(channelId,show.id),
                    name = show.name,
                    orderIndex = index,
                    clip = clip
                )
            )

            showReq.seasons.forEach { seasonReq ->

                val season = seasonRepo.save(
                    SeasonEntity(
                        id = seasonReq.id,
                        seasonNumber = seasonReq.number,
                        show = show
                    )
                )

                seasonReq.episodes.forEach { episodeReq ->

                    val episode = episodeRepo.save(
                        EpisodeEntity(
                            id = episodeReq.id,
                            episodeNumber = episodeReq.number,
                            season = season,
                            show = show
                        )
                    )

                    val segments = episodeReq.segments.mapIndexed { index, segmentReq ->
                        SegmentEntity(
                            showId = show.id,
                            videoId = segmentReq.videoId,
                            fallBackVideoId = segmentReq.fallbackVideoID,
                            orderIndex = index,
                            episode = episode
                        )
                    }

                    segmentRepo.saveAll(segments)

                }
            }
        }
    }

    private fun clearDatabase() {
        segmentRepo.deleteAll()
        episodeRepo.deleteAll()
        seasonRepo.deleteAll()
        channelShowRepo.deleteAll()
        showRepo.deleteAll()
        channelRepo.deleteAll()
    }

    @Transactional
    fun updateShowCountForChannels(){
        val channels = channelRepo.findAll()
        channels.forEach {
            it.showCount = channelShowRepo.countByIdChannelId(it.id).toInt()
        }
        channelRepo.saveAll(channels)
    }

    fun getDisabledEpisodes():List<EpisodeEntity> {
        return episodeRepo.getDisabledEpisodes()
    }

    fun getUserCount():Long {
        return this@AdminService.userRepo.count()
    }

    fun getDeviceCount():Long {
        return deviceRepo.count()
    }

}
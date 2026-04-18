package channel0.domain.validation

import channel0.backend.data.dto.request.ChannelRequest
import channel0.backend.data.dto.request.EpisodeRequest
import channel0.data.dto.request.PopulateRequest
import channel0.data.dto.request.SeasonRequest
import channel0.data.dto.request.ShowRequest



    fun validate(request: PopulateRequest) {
        require(request.channels.isNotEmpty()) {
            "Populate failed: No channels provided"
        }

        val channelIds = mutableSetOf<String>()

        request.channels.forEach { channel ->
            require(channelIds.add(channel.id)) {
                "Duplicate channel id: ${channel.id}"
            }
            validateChannel(channel)
        }
    }

    private fun validateChannel(channel: ChannelRequest) {

        require(channel.shows.isNotEmpty()) {
            "Channel ${channel.id} has no shows"
        }

        val showIds = mutableSetOf<String>()

        channel.shows.forEach { show ->
            require(showIds.add(show.id)) {
                "Duplicate show id ${show.id} in channel ${channel.id}"
            }
            validateShow(show)
        }

        channel.clipShows.forEach { show ->
            require(showIds.add(show.id)) {
                "Duplicate show id ${show.id} across shows & clipShows in channel ${channel.id}"
            }
            validateShow(show)
        }
    }

    private fun validateShow(show: ShowRequest) {

        require(show.seasons.isNotEmpty()) {
            "Show ${show.id} has no seasons"
        }

        val seasonIds = mutableSetOf<String>()

        show.seasons.forEach { season ->
            require(seasonIds.add(season.id)) {
                "Duplicate season id ${season.id} in show ${show.id}"
            }
            validateSeason(season)
        }
    }

    private fun validateSeason(season: SeasonRequest) {

        require(season.episodes.isNotEmpty()) {
            "Season ${season.id} has no episodes"
        }

        val episodeIds = mutableSetOf<String>()

        season.episodes.forEach { episode ->
            require(episodeIds.add(episode.id)) {
                "Duplicate episode id ${episode.id} in season ${season.id}"
            }
            validateEpisode(episode)
        }
    }

    private fun validateEpisode(episode: EpisodeRequest) {

        require(episode.segments.isNotEmpty()) {
            "Episode ${episode.id} has no segments"
        }

        episode.segments.forEachIndexed { index, segment ->
            require(segment.videoId.isNotBlank()) {
                "Segment at index $index in episode ${episode.id} has empty videoId"
            }
        }
    }

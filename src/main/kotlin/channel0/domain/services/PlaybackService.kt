package channel0.domain.services


import channel0.data.dto.responses.PlayableDto
import channel0.data.entities.global.SegmentEntity
import channel0.data.entities.userBased.UserChannelProgress
import channel0.data.entities.userBased.UserChannelShow
import channel0.data.entities.userBased.UserShowProgress
import channel0.data.repositories.ChannelShowRepository
import channel0.data.repositories.EpisodeRepository
import channel0.data.repositories.SeasonRepository
import channel0.data.repositories.SegmentRepository
import channel0.data.repositories.UserChannelProgressRepository
import channel0.data.repositories.UserChannelShowRepository
import channel0.data.repositories.UserShowProgressRepository
import channel0.exception.BadRequestException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class PlaybackService(
    private val segmentRepository: SegmentRepository,
    private val episodeRepository: EpisodeRepository,
    private val seasonRepository: SeasonRepository,
    private val channelShowRepository: ChannelShowRepository,
    private val userService : UserService,
    private val userShowProgressRepository: UserShowProgressRepository,
    private val userChannelShowRepository: UserChannelShowRepository,
    private val userChannelProgressRepository: UserChannelProgressRepository
) {

    private val log = LoggerFactory.getLogger(PlaybackService::class.java)

    /**
     * Returns the next playable segment for a show based on the user's current progress.
     *
     * This function is the primary entry point for show playback progression. It operates
     * using a **staging model**:
     *
     * - `currentSegmentId` represents the segment that should play **now**.
     * - `currentSegmentIndex` represents the index used to stage the **next segment**.
     *
     * Workflow:
     * 1. Retrieve all segment IDs for the current episode.
     * 2. If `currentSegmentId` exists, play that segment.
     * 3. If it does not exist, reconstruct it using `currentSegmentIndex`.
     * 4. After resolving the current segment, stage the next one by advancing the index.
     * 5. Persist the updated progress state.
     *
     * If the episode is empty or the index goes out of bounds, progression is escalated
     * to `goToNextEpisode()` which attempts to recover by advancing the playback pipeline.
     *
     * This function **guarantees that something should always play**, unless deeper
     * progression logic determines the show must advance.
     */
    fun getNextSegment(channelProgress: UserChannelProgress, showProgress: UserShowProgress): PlayableDto {

        var segment: SegmentEntity?
        val showName = userChannelShowRepository.findByShowIdAndUserId(showProgress.showId,showProgress.userId)?.name ?: "Unknown Show"
        val segmentsForEpisode = segmentRepository.findSegmentIdsByEpisodeId(
            showProgress.currentEpisodeId ?: return goToNextEpisode(
                channelProgress,
                showProgress
            )
        )




        if (segmentsForEpisode.isEmpty()) {
            segment = null
        } else {
            val currentSegmentId = showProgress.currentSegmentId
                ?: segmentsForEpisode.getOrElse(showProgress.currentSegmentIndex++) {
                    return goToNextEpisode(
                        channelProgress,
                        showProgress
                    )
                }


            segment = segmentRepository.findById(currentSegmentId).orElse(null)


            showProgress.currentSegmentIndex++
            showProgress.currentSegmentId = segmentsForEpisode.getOrNull(showProgress.currentSegmentIndex)


            userShowProgressRepository.save(showProgress)
        }


        return if (segment != null) {
            (PlayableDto(showName = showName, showId = segment.showId, clipShow = false,segment.episode.id, segment.videoId, segment.fallBackVideoId, orderIndex = segment.orderIndex ))
        } else {
            //            Episode is broken
            goToNextEpisode(channelProgress, showProgress)
        }
    }


    /**
     * Advances playback to the next episode within the current show.
     *
     * This function is responsible for managing **episode-level progression**.
     *
     * Progression order:
     * 1. Attempt to move to the next episode in the current season.
     * 2. If the season is exhausted, attempt to move to the next season.
     * 3. If no valid episode can be resolved, fall back to `goToNextShow()`.
     *
     * Defensive retry loops are used to recover from unexpected edge cases such as:
     * - Empty seasons
     * - Missing episode data
     * - Corrupted progression states
     *
     * The function also enforces a **channel policy**:
     * After a configured number of episodes (`episodesPlayedInCurrentShow > 2`),
     * the system transitions to the next show instead of continuing indefinitely.
     *
     * Once a valid episode is resolved:
     * - Episode pointer is updated
     * - Segment staging is reset
     * - Progress is persisted
     * - Playback resumes through `getNextSegment()`
     *
     * This function is part of a **fail-safe playback pipeline** where returning
     * `null` is avoided and escalation continues until a playable segment is found.
     */
    fun goToNextEpisode(channelProgress: UserChannelProgress, showProgress: UserShowProgress): PlayableDto {
        var trials = 0
        var playable: PlayableDto? = null


        // Step 1: Try to get next episode in current season
        val currentSeasonId = showProgress.currentSeasonId
        var nextEpisodeId: String?

        if (channelProgress.episodesPlayedInCurrentShow > 1) {
            // Possibly time to shift to next show
            while (trials < 2) {
                log.debug("go to next show called from goTONextEpisode trial ${System.currentTimeMillis()}")
                playable = goToNextShow(channelProgress)
                trials++
            }
            return playable ?: throw IllegalStateException("Could not find valid playable in next show")
        } else if (currentSeasonId == null) {
            nextEpisodeId = goToNextSeason(showProgress)
        } else {
            // Normal episode progression
            showProgress.currentEpisodeIndex++
            val episodesForSeason = episodeRepository.findEpisodeIdsBySeasonId(currentSeasonId)
            nextEpisodeId = episodesForSeason.getOrElse(showProgress.currentEpisodeIndex) {
                goToNextSeason(showProgress)
            }
        }


        // Step 2: Defensive loop in case season was empty
        trials = 0
        while (nextEpisodeId == null && trials < 2) {
            nextEpisodeId = goToNextSeason(showProgress)
            trials++
        }


        // Step 3: Last-resort shift to next show
        if (nextEpisodeId == null) {
            trials = 0
            while (trials < 2 && playable == null) {
                log.debug("go to next show called in loop because season was empty ${System.currentTimeMillis()}")
                playable = goToNextShow(channelProgress)
                trials++
            }
            return playable ?: throw IllegalStateException("Could not find valid playable after trying next show")
        }


        // Step 4: Update progress pointers
        showProgress.currentEpisodeId = nextEpisodeId
        showProgress.currentSegmentIndex = 0
        showProgress.currentSegmentId = segmentRepository.findSegmentIdsByEpisodeId(nextEpisodeId).getOrNull(0)
        channelProgress.episodesPlayedInCurrentShow++


        userChannelProgressRepository.save(channelProgress)
        userShowProgressRepository.save(showProgress)


        // Step 5: Return next playable segment
        return getNextSegment(channelProgress, showProgress)
    }


    /**
     * Advances playback to the next season within a show.
     *
     * This function is used as part of the recovery and progression pipeline
     * when the current season is exhausted or when the episode pointer becomes invalid.
     *
     * Behavior:
     * - Retrieves all seasons for the show.
     * - Advances the `currentSeasonIndex`.
     * - If the index exceeds the number of seasons, it wraps back to the first season.
     *
     * Once a valid season is determined:
     * - Episode pointer is reset to the first episode of the new season
     * - Segment staging is reset
     * - Progress state is persisted
     *
     * The function returns the ID of the first episode of the new season.
     *
     * If the show contains no seasons, this represents a **data integrity failure**
     * and an exception is thrown.
     */
    fun goToNextSeason(showProgress: UserShowProgress): String? {
        val seasonIdsForShow = seasonRepository.findSeasonIdsByShowId(showProgress.showId)
        if (seasonIdsForShow.isEmpty()) throw IllegalStateException("Show ${showProgress.showId} has no seasons!")


        showProgress.currentSeasonIndex++
        val nextSeasonId = seasonIdsForShow.getOrElse(showProgress.currentSeasonIndex) {
            showProgress.currentSeasonIndex = 0
            seasonIdsForShow[0]
        }


        // Reset episode/segment pointers
        showProgress.currentSeasonId = nextSeasonId
        showProgress.currentEpisodeIndex = 0
        showProgress.currentSegmentIndex = 0
        showProgress.currentEpisodeId = episodeRepository.findEpisodeIdsBySeasonId(nextSeasonId).getOrNull(0)
        showProgress.currentSegmentId =
            showProgress.currentEpisodeId?.let { segmentRepository.findSegmentIdsByEpisodeId(it).getOrNull(0) }


        userShowProgressRepository.save(showProgress)


        return showProgress.currentEpisodeId
    }


    /**
     * Advances the channel to the next show and returns the playable segment.
     *
     * Behavior:
     *
     * 1. Ensures the user has channel shows created (lazy creation).
     * 2. Advances both:
     *      - normal show pointer
     *      - clip show pointer
     * 3. Persists the new pointer state.
     * 4. Plays a clip show segment before the next normal show.
     *
     * Clip shows function like **TV interstitials**.
     *
     * If the clip show is invalid (missing season/episode/segment),
     * the system **falls back to normal show playback** using ShowService.
     *
     * This guarantees the system invariant:
     *
     *      "Something always plays."
     */
    fun goToNextShow(channelProgress: UserChannelProgress): PlayableDto {

        // Advance pointers
        channelProgress.currentShowIndex++
        channelProgress.currentClipShowIndex++
        channelProgress.episodesPlayedInCurrentShow = 0

        val currentClipShow= resolveCurrentChannelShow(channelProgress,true)
        val currentShow = resolveCurrentChannelShow(channelProgress)

        // Ensure clip show progress exists
        val clipShowProgress =
            userShowProgressRepository.findByShowIdAndUserId(currentClipShow.showId, channelProgress.userId)
                ?: userService.createUserShowProgress(showId = currentClipShow.showId, channelProgress.userId)

        var currentEpisodeId = clipShowProgress.currentEpisodeId

        // Resolve first episode if missing
        if (currentEpisodeId == null) {

            val seasonId =
                clipShowProgress.currentSeasonId
                    ?: seasonRepository.findFirstSeasonIdByShowId(clipShowProgress.showId)
                    ?: return (
                            //skip to normal show
                            getNextSegment(
                                channelProgress,
                                userShowProgressRepository.findByShowIdAndUserId(currentShow.showId,channelProgress.userId)
                                    ?: userService.createUserShowProgress(showId = currentShow.showId, channelProgress.userId)
                            )
                            )

            val episodesForSeason = episodeRepository.findEpisodeIdsBySeasonId(seasonId)

            currentEpisodeId =
                episodesForSeason.getOrElse(clipShowProgress.currentEpisodeIndex){
                    episodesForSeason[0]
                }

            clipShowProgress.currentEpisodeId = currentEpisodeId
            userShowProgressRepository.save(clipShowProgress)
        }

        // Resolve first segment of clip show
        val firstSegmentId =
            segmentRepository.findSegmentIdsByEpisodeId(currentEpisodeId).getOrNull(0)
                ?: return (
//                        skip to normalShow
                        getNextSegment(
                            channelProgress,
                            userShowProgressRepository.findByShowIdAndUserId(currentShow.showId,channelProgress.userId)
                                ?: userService.createUserShowProgress(showId = currentShow.showId, channelProgress.userId)
                        )
                        )

        val firstSegment =
            segmentRepository.findById(firstSegmentId).orElseThrow {
                IllegalStateException("Segment entity not found for id $firstSegmentId")
            }

        return PlayableDto(
            showName = currentClipShow.name,
            showId = firstSegment.showId,
            clipShow = currentClipShow.clip,
            episodeId = firstSegment.episode.id,
            videoId = firstSegment.videoId,
            fallbackId = firstSegment.fallBackVideoId,
            orderIndex = firstSegment.orderIndex
        )
    }

    fun resolveCurrentChannelShow(channelProgress: UserChannelProgress, clip: Boolean = false): UserChannelShow {
        if (clip) {
            var channelClipShows =
                userChannelShowRepository.findByChannelIdAndUserIdAndClipTrueAndEnabledTrueOrderByPosition(
                    channelProgress.channelId,
                    channelProgress.userId
                )

            // Lazy creation of clip shows
            if (channelClipShows.isEmpty()) {
                channelClipShows = userService.createUserChannelShow(
                    channelShowRepository.findByIdChannelIdAndClipTrueOrderByOrderIndexAsc(channelId = channelProgress.channelId),
                    channelProgress.userId
                )
            }

            if (channelClipShows.isEmpty()) throw BadRequestException("no clip shows enabled for this channel")
            val show = channelClipShows.getOrElse(channelProgress.currentClipShowIndex) {
                channelProgress.currentClipShowIndex = 0
                channelClipShows[0]
            }
            userChannelProgressRepository.save(channelProgress)
            return show

        } else {
            var channelShows =
                userChannelShowRepository.findByChannelIdAndUserIdAndClipFalseAndEnabledTrueOrderByPosition(
                    channelProgress.channelId,
                    channelProgress.userId
                )

            // Lazy creation of user channel shows
            if (channelShows.isEmpty()) {
                channelShows = userService.createUserChannelShow(
                    channelShowRepository.findByIdChannelIdAndClipFalseOrderByOrderIndexAsc(channelId = channelProgress.channelId),
                    channelProgress.userId
                )
            }
            if (channelShows.isEmpty()) throw BadRequestException("no shows for this channel")
            val channelShow = channelShows.getOrElse(channelProgress.currentShowIndex) {
                channelProgress.currentShowIndex = 0
                channelShows[0]
            }
            channelProgress.currentShowId = channelShow.showId
            userChannelProgressRepository.save(channelProgress)
            return channelShow
        }
    }

    //    wrapper function for when only show id is needed
    fun resolveCurrentChannelShowId (channelProgress: UserChannelProgress, clip: Boolean = false): String{
        return resolveCurrentChannelShow(channelProgress,clip).showId
    }
}
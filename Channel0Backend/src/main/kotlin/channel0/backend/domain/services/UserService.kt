package channel0.backend.domain.services

import channel0.backend.data.entities.global.ChannelShowEntity
import channel0.backend.data.entities.userBased.UserChannelProgress
import channel0.backend.data.entities.userBased.UserChannelShow
import channel0.backend.data.entities.userBased.UserShowProgress
import channel0.backend.data.entities.userBased.UserEntity
import channel0.backend.data.repositories.ChannelRepository
import channel0.backend.data.repositories.ChannelShowRepository
import channel0.backend.data.repositories.EpisodeRepository
import channel0.backend.data.repositories.SeasonRepository
import channel0.backend.data.repositories.SegmentRepository
import channel0.backend.data.repositories.UserChannelProgressRepository
import channel0.backend.data.repositories.UserChannelShowRepository
import channel0.backend.data.repositories.UserShowProgressRepository
import channel0.backend.data.repositories.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userChannelShowRepository: UserChannelShowRepository,
    private val userShowProgressRepository: UserShowProgressRepository,
    private val userChannelProgressRepository: UserChannelProgressRepository,
    private val channelShowRepository: ChannelShowRepository,
    private val channelRepository: ChannelRepository,
    private val seasonRepository: SeasonRepository,
    private val episodeRepository: EpisodeRepository,
    private val segmentRepository: SegmentRepository
) {

    /**
     * Creates a new user in the system if one does not already exist.
     *
     * @param userId The unique identifier of the user.
     *
     * This is lazy creation: we only insert a user if they are interacting
     * with the system for the first time. This prevents unnecessary database
     * rows for inactive users.
     */
    @Transactional
    fun createUser(userId: Long? = null): Long {

        // reuse existing user
        if (userId != null && userRepository.existsById(userId)) {
            return userId
        }

        // create new user
        val user = userRepository.save(UserEntity(null))
        return user.id!!
    }


    /**
     * Creates a new UserShowProgress entry if it does not already exist for the user.
     *
     * @param showId The ID of the show for which to create progress tracking.
     * @param userId The ID of the user.
     * @return The created (or existing) UserShowProgress entity.
     *
     * Steps:
     * 1. Check if progress already exists. If yes, return it.
     * 2. Fetch first season of the show.
     * 3. Fetch first episode of that season, if season exists.
     * 4. Fetch first segment of that episode, if episode exists.
     * 5. Save and return UserShowProgress entity.
     *
     * This ensures lazy creation of progress tracking when the user first
     * interacts with a show.
     */
    @Transactional
    fun createUserShowProgress(showId: String, userId: Long): UserShowProgress {
        val existing = userShowProgressRepository.findByShowIdAndUserId(showId,userId)
        if (existing != null) return existing

        val currentSeasonId = seasonRepository.findFirstSeasonIdByShowId(showId)
        var currentSegmentId :Long? = null
        var currentEpisodeId : String? = null
        if (currentSeasonId != null){
            currentEpisodeId = episodeRepository.findEpisodeIdsBySeasonId(currentSeasonId).getOrNull(0)
            currentSegmentId = if (currentEpisodeId != null) segmentRepository.findSegmentIdsByEpisodeId(currentEpisodeId).getOrNull(0) else null
        }

        val progress = UserShowProgress(
            id = null,
            userId = userId,
            showId = showId,
            currentSeasonId = currentSeasonId,
            currentEpisodeId = currentEpisodeId,
            currentSegmentId = currentSegmentId,
            currentSeasonIndex = 0,
            currentEpisodeIndex = 0,
            currentSegmentIndex = 0
        )
        userShowProgressRepository.save(progress)
        return progress
    }

    /**
     * Creates one or more UserChannelShow entries for a user if they do not exist.
     *.
     * @param channelShows The list of ChannelShows to associate with this user for this channel.
     * @param userId The ID of the user.
     * @return List of created (or already existing) UserChannelShow entities.
     *
     * Notes:
     * - Checks for existing entries to avoid duplicates.
     * - Automatically assigns incremental positions to shows.
     * - Saves all shows in bulk using saveAll for efficiency.
     */
    @Transactional
    fun createUserChannelShow(
        channelShows: List<ChannelShowEntity>,
        userId: Long,
    ): List<UserChannelShow> {
        val userChannelShows : MutableList<UserChannelShow> = mutableListOf()

        for (channelShow in channelShows){
            val existing = userChannelShowRepository.findByChannelIdAndShowIdAndUserId(channelShow.id.channelId, channelShow.id.showId, userId)
            if (existing != null) continue

            val channelShow = UserChannelShow(
                id = null,
                userId = userId,
                channelId = channelShow.id.channelId,
                name = channelShow.name,
                showId = channelShow.id.showId,
                position = channelShow.orderIndex,
                clip = channelShow.clip,
                enabled = true
            )
            userChannelShows.add(channelShow)
        }
        userChannelShowRepository.saveAll(userChannelShows)
        return userChannelShows
    }

    /**
     * Creates a UserChannelProgress entry if it does not exist.
     *
     * @param channelId The ID of the channel.
     * @param userId The ID of the user.
     * @return The created (or existing) UserChannelProgress entity.
     *
     * Steps:
     * 1. Check if progress already exists. If yes, return it.
     * 2. Retrieve default first show IDs for normal and clip shows from the channel.
     * 3. Initialize indices and counters to 0.
     * 4. Save and return.
     *
     * This ensures lazy creation of channel progress tracking for new users.
     */
    @Transactional
    fun createUserChannelProgress(channelId: String,userId: Long ): UserChannelProgress {
        val existing = userChannelProgressRepository.findByChannelIdAndUserId(channelId,userId)
        if (existing != null) return existing

        val channelProgress = UserChannelProgress(
            id = null,
            userId = userId,
            channelId = channelId,
            name = channelRepository.findChannelNameById(channelId),
            currentShowIndex = 0,
            currentClipShowIndex = 0,
            episodesPlayedInCurrentShow = 0,
            currentShowId = channelShowRepository.findFirstByIdChannelIdAndClipFalseOrderByOrderIndexAsc(channelId)?.id?.showId,
            currentClipShowId = channelShowRepository.findFirstByIdChannelIdAndClipTrueOrderByOrderIndexAsc(channelId)?.id?.showId,
        )
        userChannelProgressRepository.save(channelProgress)
        return channelProgress
    }
}
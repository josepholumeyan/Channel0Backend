package channel0.domain.services


import channel0.data.entities.global.ChannelEntity
import channel0.data.entities.userBased.UserChannelShow
import channel0.data.repositories.ChannelRepository
import channel0.data.repositories.ChannelShowRepository
import channel0.data.repositories.UserChannelProgressRepository
import channel0.data.repositories.UserChannelShowRepository
import channel0.exception.BadRequestException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service


/**
 * ChannelService is responsible for **channel-level orchestration**.
 *
 * It manages:
 *
 * - Retrieving available channels
 * - Managing which shows are enabled/disabled per user
 * - Maintaining channel playback state
 *
 * The service works together with:
 *
 * - UserService → lazily creates user-specific entities
 *
 * The playback hierarchy in the backend is:
 *
 * Channel → Show → Season → Episode → Segment
 *
 * ChannelService controls the **top-level scheduling layer**.
 */
@Service
class ChannelService(
    private val channelRepository: ChannelRepository,
    private val channelShowRepository: ChannelShowRepository,
    private val userService: UserService,
    private val userChannelShowRepository: UserChannelShowRepository,
    private val userChannelProgressRepository: UserChannelProgressRepository
) {

    /**
     * Returns all available channels.
     *
     * Channels themselves are global entities and not user-specific.
     */
    fun getChannels(): List<ChannelEntity> = channelRepository.findAll()


    /**
     * Returns all **enabled normal shows** for a specific channel and user.
     *
     * Only shows that are:
     * - enabled
     * - not clip shows
     * are returned.
     *
     * Used when building the user's current playable lineup.
     */
    fun getEnabledChannelShows(channelId: String, userId: Long ): List<UserChannelShow> {
        val userChannelShows =
            userChannelShowRepository.findByChannelIdAndUserIdAndClipFalseAndEnabledTrueOrderByPosition(channelId,userId)

        if (userChannelShows.isEmpty()) {
            throw BadRequestException("User has no enabled show")
        }

        return userChannelShows
    }


    /**
     * Returns **all channel shows for a user**, including disabled ones.
     *
     * This reads from the **user table instead of the global channel-show table**
     * because the system is designed to eventually support:
     *
     * - user-customized channels
     * - personalized ordering
     * - enabling/disabling shows
     */
    fun getChannelShows(channelId : String, userId :Long): List<UserChannelShow> {
        var userChannelShows =
            userChannelShowRepository.findByChannelIdAndUserIdOrderByPosition(channelId,userId)

        if(userChannelShows.isEmpty()){
            userChannelShows = userService.createUserChannelShow(channelShowRepository.findByIdChannelIdOrderByOrderIndexAsc(channelId),userId)
        }

        return userChannelShows
    }


    /**
     * Disables a show for a user within a channel.
     *
     * The show remains in the user's list but will no longer be considered
     * during playback scheduling.
     */
    @Transactional
    fun disableShow(channelId:String, showIds: List<String>, userId: Long) {
        val channelShows = mutableListOf<UserChannelShow>()
        for ( showId in showIds) {
            val channelShow =
                userChannelShowRepository.findByChannelIdAndShowIdAndUserId(channelId, showId, userId)
                    ?: throw BadRequestException("show not found")

            channelShow.enabled = false
            channelShows.add(channelShow)
        }
        userChannelShowRepository.saveAll(channelShows)
        val channelProgress = userChannelProgressRepository.findByChannelIdAndUserId(channelId, userId)?: return
        channelProgress.currentShowId = null // set convenient lookup to null in case of the scenario where current show has been disabled
        userChannelProgressRepository.save(channelProgress)
    }


    /**
     * Enables a previously disabled show for a user.
     */
    @Transactional
    fun enableShow(channelId:String, showIds: List<String>, userId: Long) {
        val channelShows = mutableListOf<UserChannelShow>()
        for ( showId in showIds) {
            val channelShow =
                userChannelShowRepository.findByChannelIdAndShowIdAndUserId(channelId, showId, userId)
                    ?: throw BadRequestException("show not found")

            channelShow.enabled = true
            channelShows.add(channelShow)
        }
        userChannelShowRepository.saveAll(channelShows)
    }


    /**
     * Returns the ID of the current normal show pointer for a channel.
     *
     * If the user does not yet have a channel progress record,
     * one will be created lazily.
     */
    fun getCurrentShowId(channelId: String, userId: Long): String?{
        val userChannelProgress =
            userChannelProgressRepository.findByChannelIdAndUserId(channelId,userId)
                ?: userService.createUserChannelProgress(channelId,userId)

        return userChannelProgress.currentShowId
    }


    /**
     * Returns the ID of the current clip show pointer.
     *
     * Clip shows act as **interstitial content** between main shows.
     */
    fun getCurrentClipShowId(channelId: String, userId: Long): String?{
        val userChannelProgress =
            userChannelProgressRepository.findByChannelIdAndUserId(channelId,userId)
                ?: userService.createUserChannelProgress(channelId,userId)

        return userChannelProgress.currentClipShowId
    }

}
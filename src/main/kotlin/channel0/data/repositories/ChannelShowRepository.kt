package channel0.data.repositories

import channel0.data.entities.global.ChannelShowEntity
import channel0.data.entities.global.ChannelShowId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ChannelShowRepository : JpaRepository<ChannelShowEntity, ChannelShowId> {

    // getAllChannelShows(channelId)
    fun findByIdChannelIdOrderByOrderIndexAsc(channelId: String): List<ChannelShowEntity>

    fun countByIdChannelId(channelId: String): Long

    // getAllNormalChannelShows
    fun findByIdChannelIdAndClipFalseOrderByOrderIndexAsc(channelId: String): List<ChannelShowEntity>

    //get clip channelShows
    fun findByIdChannelIdAndClipTrueOrderByOrderIndexAsc(channelId: String): List<ChannelShowEntity>

    @Query("""
    select c.name
    from ChannelShowEntity c
    where c.id.showId = :showId
""")
    fun findNameByShowId(showId: String): String?


    // getChannelShow(channelId, showId)
    fun findByIdChannelIdAndIdShowId(channelId: String, showId: String): ChannelShowEntity?

    // get ChannelShow using Show id
    fun findByIdShowId(showId:String) : ChannelShowEntity?

    fun findByIdShowIdIn(showIds: List<String>): List<ChannelShowEntity>

    fun findFirstByIdChannelIdAndClipFalseOrderByOrderIndexAsc(channelId: String): ChannelShowEntity?

    // Clip channel shows (clip = true)
    fun findFirstByIdChannelIdAndClipTrueOrderByOrderIndexAsc(channelId: String): ChannelShowEntity?

    @Query("""
        SELECT cs.id.showId 
        FROM ChannelShowEntity cs 
        WHERE cs.id.channelId = :channelId AND cs.clip = false
        ORDER BY cs.orderIndex
    """)
    fun findNormalShowIdsByChannel(@Param("channelId") channelId: String): List<String>

    // Clip channel shows (clip = true)
    @Query("""
        SELECT cs.id.showId 
        FROM ChannelShowEntity cs 
        WHERE cs.id.channelId = :channelId AND cs.clip = true
        ORDER BY cs.orderIndex
    """)
    fun findClipShowIdsByChannel(@Param("channelId") channelId: String): List<String>


}

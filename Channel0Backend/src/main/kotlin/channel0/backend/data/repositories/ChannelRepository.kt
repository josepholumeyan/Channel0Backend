package channel0.backend.data.repositories

import channel0.backend.data.entities.global.ChannelEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ChannelRepository : JpaRepository<ChannelEntity, String> {
    // getAllChannels()
    override fun findAll(): List<ChannelEntity>

    // getChannelById() -> findById(id): Optional<Channel>
    // Optional convenience:
    fun findByIdEquals(id: String): ChannelEntity

    // getChannelByName()
    fun findByName(name: String): ChannelEntity?

    @Query("""
        SELECT c.name
        FROM ChannelEntity c 
        WHERE c.id = :channelId 
    """)
    fun findChannelNameById(@Param("channelId") channelId: String):String
    
}

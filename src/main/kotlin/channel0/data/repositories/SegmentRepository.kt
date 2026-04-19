package channel0.data.repositories

import channel0.data.entities.global.SegmentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface SegmentRepository : JpaRepository<SegmentEntity, Long> {
    // getSegmentsForEpisode(episodeId)
    fun findByEpisodeId(episodeId: String): List<SegmentEntity>

    @Query("SELECT s FROM SegmentEntity s WHERE s.episode.id = :episodeId")
    fun findSegmentsByEpisodeId(episodeId: String): List<SegmentEntity>

    @Query("SELECT s.id FROM SegmentEntity s WHERE s.episode.id = :episodeId")
    fun findSegmentIdsByEpisodeId(episodeId: String): List<Long>

    // getSegmentsForShow(showId)
    fun findByShowId(showId: String): List<SegmentEntity>

}

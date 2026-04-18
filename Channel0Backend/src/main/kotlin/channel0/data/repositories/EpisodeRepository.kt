package channel0.data.repositories

import channel0.data.entities.global.EpisodeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface EpisodeRepository : JpaRepository<EpisodeEntity, String> {
    // getEpisodesForSeason(seasonId)
    fun findBySeasonIdOrderByEpisodeNumberAsc(seasonId: String): List<EpisodeEntity>

    // getEpisodeById(id)
    // findById(id) from JpaRepository returns Optional<Episode>

    @Query("SELECT e FROM EpisodeEntity e WHERE e.season.id = :seasonId")
    fun findEpisodesBySeasonId(seasonId: String): List<EpisodeEntity>

    @Query("SELECT e.id FROM EpisodeEntity e WHERE e.season.id = :seasonId AND e.enabled = true")
    fun findEpisodeIdsBySeasonId(seasonId: String): List<String>

    @Query("SELECT e FROM EpisodeEntity e WHERE e.enabled = false")
    fun getDisabledEpisodes(): List<EpisodeEntity>

    @Modifying
    @Query("UPDATE EpisodeEntity e set e.enabled = false WHERE e.id = :episodeId")
    fun disableEpisode(@Param("episodeId")episodeId:String):Int

    // getEpisodesForShow(showId)
    fun findByShowIdOrderByEpisodeNumberAsc(showId: String): List<EpisodeEntity>

}

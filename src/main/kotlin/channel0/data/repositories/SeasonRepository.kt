package channel0.data.repositories

import channel0.data.entities.global.SeasonEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface SeasonRepository : JpaRepository<SeasonEntity, String> {
    // getSeasonById(id) -> findById
    // getSeasonsForShow(showId)
    fun findByShowIdOrderBySeasonNumberAsc(showId: String): List<SeasonEntity>

    @Query("SELECT s.id FROM SeasonEntity s WHERE s.show.id = :showId")
    fun findSeasonIdsByShowId(showId: String): List<String>

    @Query("SELECT s FROM SeasonEntity s WHERE s.show.id = :showId")
    fun findSeasonsByShowId(showId: String): List<SeasonEntity>

    @Query("""
        SELECT s.id 
        FROM SeasonEntity s 
        WHERE s.show.id = :showId 
        ORDER BY s.seasonNumber ASC
    """)
    fun findFirstSeasonIdByShowId(@Param("showId") showId: String): String?

}

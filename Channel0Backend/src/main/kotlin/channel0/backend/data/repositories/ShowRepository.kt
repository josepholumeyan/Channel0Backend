package channel0.backend.data.repositories

import channel0.backend.data.entities.global.ShowEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ShowRepository : JpaRepository<ShowEntity, String> {
    // getAllShows()
    override fun findAll(): List<ShowEntity>

//    get All ClipShows
    fun findAllByClipTrue(): List<ShowEntity>

    // get All shows
    fun findAllByClipFalse(): List<ShowEntity>

    @Query("SELECT s.id FROM ShowEntity s WHERE s.clip = true")
    fun findClipShowIds(): List<String>

    @Query("SELECT s.id FROM ShowEntity s WHERE s.clip = false")
    fun findNormalShowIds(): List<String>

    // getShowByName
    fun findByName(name: String): ShowEntity?

    // getShowsByIds
    fun findByIdIn(ids: List<String>): List<ShowEntity>

    @Query("""
        SELECT DISTINCT s FROM ShowEntity s
        LEFT JOIN FETCH s.seasons se
        LEFT JOIN FETCH se.episodes ep
        LEFT JOIN FETCH ep.segments
        WHERE s.id = :id
    """)
    fun fetchFullGraph(@Param("id") id: String): ShowEntity?

}
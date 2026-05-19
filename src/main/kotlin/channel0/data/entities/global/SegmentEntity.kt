package channel0.data.entities.global

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "segments",
    indexes = [
        Index(name = "idx_segment_episode", columnList = "episode_id"),
        Index(name = "idx_segment_show", columnList = "showId")
    ]
    )
data class SegmentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    val id: Long = 0,
    val showId: String,
    val videoId: String,
    val fallBackVideoId: String?,
    val orderIndex : Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id")
    val episode: EpisodeEntity

)
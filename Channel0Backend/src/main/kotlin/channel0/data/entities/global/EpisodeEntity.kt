package channel0.data.entities.global

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "episodes")
data class EpisodeEntity(
    @Id
    val id: String,
    val episodeNumber: Int,
    var enabled: Boolean  = true,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id")
    val show: ShowEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id")
    val season: SeasonEntity,

    @OneToMany(
        mappedBy = "episode",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val segments: MutableList<SegmentEntity> = mutableListOf()

)
package channel0.backend.data.entities.global

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "seasons")
data class SeasonEntity(
    @Id
    val id: String,
    val seasonNumber: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id")
    val show: ShowEntity,

    @OneToMany(
        mappedBy = "season",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val episodes: MutableList<EpisodeEntity> = mutableListOf()

)
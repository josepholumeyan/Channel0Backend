package channel0.data.entities.global

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "shows")
data class ShowEntity (
    @Id
    val id: String,
    val name: String,

    @OneToMany(
        mappedBy = "show",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val seasons: MutableList<SeasonEntity> = mutableListOf(),
    val clip: Boolean = false

)
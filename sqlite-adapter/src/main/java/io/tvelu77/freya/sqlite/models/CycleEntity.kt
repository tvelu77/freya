package io.tvelu77.freya.sqlite.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.tvelu77.freya.domain.models.Cycle
import java.time.LocalDate

@Entity
data class CycleEntity(
    @PrimaryKey val id: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val duration: Int
)

fun Cycle.toEntity(): CycleEntity = CycleEntity(id, startDate, endDate, duration)
fun CycleEntity.toDomain(): Cycle = Cycle(id, startDate, endDate, duration)
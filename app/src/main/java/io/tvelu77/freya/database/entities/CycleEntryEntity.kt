package io.tvelu77.freya.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.tvelu77.freya.domain.models.CycleEntry
import java.time.LocalDate

@Entity(tableName = "cycle_entries")
data class CycleEntryEntity(
  @PrimaryKey val id: Long,
  val startDate: LocalDate,
  val endDate: LocalDate?,
  val cycleLengthDays: Int,
  val notes: String
) {
  fun toDomain() = CycleEntry(
    id,
    startDate,
    endDate,
    cycleLengthDays,
    notes
  )

  companion object {
    fun fromDomain(entry: CycleEntry) = CycleEntryEntity(
      entry.id,
      entry.startDate,
      entry.endDate,
      entry.cycleLengthDays,
      entry.notes
    )
  }
}

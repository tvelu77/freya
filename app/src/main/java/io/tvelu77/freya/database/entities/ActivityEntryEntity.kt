package io.tvelu77.freya.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.tvelu77.freya.domain.models.ActivityEntry
import io.tvelu77.freya.domain.models.SportType
import java.time.LocalDate

@Entity(tableName = "activity_entries")
data class ActivityEntryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val date: LocalDate,
  val sportType: String,
  val durationMin: Int,
  val kcalBurned: Int
) {
  fun toDomain() = ActivityEntry(
    id = id,
    date = date,
    sportType = SportType.valueOf(sportType),
    durationMin = durationMin,
    kcalBurned = kcalBurned
  )

  companion object {
    fun fromDomain(entry: ActivityEntry) = ActivityEntryEntity(
      id = entry.id,
      date = entry.date,
      sportType = entry.sportType.name,
      durationMin = entry.durationMin,
      kcalBurned = entry.kcalBurned
    )
  }
}

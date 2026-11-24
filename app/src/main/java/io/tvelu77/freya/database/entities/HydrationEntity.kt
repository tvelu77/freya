package io.tvelu77.freya.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.tvelu77.freya.domain.models.DailyHydration
import java.time.LocalDate

@Entity(tableName = "hydration")
data class HydrationEntity(
  @PrimaryKey val date: LocalDate,
  val waterMl: Int
) {
  fun toDomain() = DailyHydration(date = date, waterMl = waterMl)

  companion object {
    fun fromDomain(hydration: DailyHydration) = HydrationEntity(
      date = hydration.date,
      waterMl = hydration.waterMl
    )
  }
}

package io.tvelu77.freya.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.tvelu77.freya.domain.models.DailyActivity
import java.time.LocalDate

@Entity(tableName = "daily_activity")
data class DailyActivityEntity(
  @PrimaryKey val date: LocalDate,
  val steps: Int,
  val sleepHours: Float
) {
  fun toDomain() = DailyActivity(date = date, steps = steps, sleepHours = sleepHours)

  companion object {
    fun fromDomain(activity: DailyActivity) = DailyActivityEntity(
      date = activity.date,
      steps = activity.steps,
      sleepHours = activity.sleepHours
    )
  }
}

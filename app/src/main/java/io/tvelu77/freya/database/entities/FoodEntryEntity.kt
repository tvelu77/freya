package io.tvelu77.freya.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.tvelu77.freya.domain.models.FoodEntry
import io.tvelu77.freya.domain.models.MealType
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "food_entries")
data class FoodEntryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val date: LocalDate,
  val timeHour: Int,
  val timeMinute: Int,
  val foodName: String,
  val quantity: Float,
  val calories: Int,
  val proteins: Float,
  val carbs: Float,
  val fats: Float,
  val mealType: String
) {
  fun toDomain() = FoodEntry(
    id,
    date,
    LocalTime.of(timeHour, timeMinute),
    foodName,
    quantity,
    calories,
    proteins,
    carbs,
    fats,
    MealType.valueOf(mealType)
  )

  companion object {
    fun fromDomain(entry: FoodEntry) = FoodEntryEntity(
      entry.id,
      entry.date,
      entry.time.hour,
      entry.time.minute,
      entry.foodName,
      entry.quantity,
      entry.calories,
      entry.proteins,
      entry.carbs,
      entry.fats,
      entry.mealType.name
    )
  }
}

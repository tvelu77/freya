package io.tvelu77.freya.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import io.tvelu77.freya.domain.models.FoodCategory
import io.tvelu77.freya.domain.models.FoodItem

@Entity(
  tableName = "food_items",
  indices = [Index(value = ["name"], unique = true)]
)
data class FoodItemEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val caloriesPer100g: Int,
  val proteinsPer100g: Float,
  val carbsPer100g: Float,
  val fatsPer100g: Float,
  val category: String,
  val defaultServingG: Float,
  val isQuick: Boolean
) {
  fun toDomain() = FoodItem(
    id = id,
    name = name,
    caloriesPer100g = caloriesPer100g,
    proteinsPer100g = proteinsPer100g,
    carbsPer100g = carbsPer100g,
    fatsPer100g = fatsPer100g,
    category = FoodCategory.valueOf(category),
    defaultServingG = defaultServingG,
    isQuick = isQuick
  )

  companion object {
    fun fromDomain(item: FoodItem) = FoodItemEntity(
      id = item.id,
      name = item.name,
      caloriesPer100g = item.caloriesPer100g,
      proteinsPer100g = item.proteinsPer100g,
      carbsPer100g = item.carbsPer100g,
      fatsPer100g = item.fatsPer100g,
      category = item.category.name,
      defaultServingG = item.defaultServingG,
      isQuick = item.isQuick
    )
  }
}

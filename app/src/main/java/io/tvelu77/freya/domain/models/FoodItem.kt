package io.tvelu77.freya.domain.models

private const val GRAMS_PER_100 = 100f

data class FoodItem(
  val id: Long = 0,
  val name: String,
  val caloriesPer100g: Int,
  val proteinsPer100g: Float,
  val carbsPer100g: Float,
  val fatsPer100g: Float,
  val category: FoodCategory,
  val defaultServingG: Float = DEFAULT_SERVING_G,
  val isQuick: Boolean = false
) {

  fun caloriesFor(quantityG: Float): Int =
    (caloriesPer100g * quantityG / GRAMS_PER_100).toInt()

  fun proteinsFor(quantityG: Float): Float =
    proteinsPer100g * quantityG / GRAMS_PER_100

  fun carbsFor(quantityG: Float): Float =
    carbsPer100g * quantityG / GRAMS_PER_100

  fun fatsFor(quantityG: Float): Float =
    fatsPer100g * quantityG / GRAMS_PER_100

  companion object {
    const val DEFAULT_SERVING_G = 100f
  }
}

package io.tvelu77.freya.domain.models

data class QuickFood(
  val name: String,
  val calories: Int,
  val proteins: Float,
  val carbs: Float,
  val fats: Float,
  val defaultQuantity: Float = 100f
)

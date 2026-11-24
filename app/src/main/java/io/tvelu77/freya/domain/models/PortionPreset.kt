package io.tvelu77.freya.domain.models

enum class PortionPreset {
  PORTION,
  BOWL,
  PLATE,
  GLASS;

  val displayName: String get() = when (this) {
    PORTION -> "1 portion"
    BOWL    -> "1 bol"
    PLATE   -> "1 assiette"
    GLASS   -> "1 verre"
  }

  fun gramsFor(category: FoodCategory): Float = when (this) {
    PORTION -> when (category) {
      FoodCategory.NUT, FoodCategory.SWEET, FoodCategory.FAT -> 30f
      FoodCategory.DRINK -> 250f
      else -> 150f
    }
    BOWL -> when (category) {
      FoodCategory.VEGETABLE -> 200f
      FoodCategory.NUT, FoodCategory.SWEET -> 40f
      FoodCategory.FAT -> 15f
      else -> 250f
    }
    PLATE -> when (category) {
      FoodCategory.VEGETABLE -> 250f
      FoodCategory.PROTEIN -> 150f
      FoodCategory.NUT, FoodCategory.SWEET -> 40f
      FoodCategory.FAT -> 15f
      FoodCategory.DRINK -> 250f
      else -> 200f
    }
    GLASS -> when (category) {
      FoodCategory.DRINK, FoodCategory.DAIRY -> 250f
      else -> 200f
    }
  }
}

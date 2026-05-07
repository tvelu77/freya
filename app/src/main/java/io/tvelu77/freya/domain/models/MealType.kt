package io.tvelu77.freya.domain.models

enum class MealType {
  BREAKFAST, LUNCH, DINNER, SNACK;

  val displayName: String get() = when (this) {
    BREAKFAST -> "Petit-déjeuner"
    LUNCH -> "Déjeuner"
    DINNER -> "Dîner"
    SNACK -> "Collation"
  }
}
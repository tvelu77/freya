package io.tvelu77.freya.domain.models

enum class FatigueLevel {
  LOW,
  MEDIUM,
  HIGH;

  val displayName: String get() = when (this) {
    LOW    -> "Faible"
    MEDIUM -> "Modérée"
    HIGH   -> "Élevée"
  }

  val kcalAdjustment: Int get() = when (this) {
    LOW    -> 0
    MEDIUM -> 100
    HIGH   -> 200
  }
}

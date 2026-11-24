package io.tvelu77.freya.domain.models

enum class Goal {
  MAINTENANCE,
  GAIN,
  LOSS;

  val displayName: String get() = when (this) {
    MAINTENANCE -> "Maintien"
    GAIN        -> "Prise de poids"
    LOSS        -> "Perte de poids"
  }

  val calorieDelta: Int get() = when (this) {
    MAINTENANCE -> 0
    GAIN        -> 200
    LOSS        -> -200
  }
}

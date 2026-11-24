package io.tvelu77.freya.domain.models

enum class SportType {
  RUNNING,
  WALKING,
  CYCLING,
  YOGA,
  SWIMMING,
  STRENGTH;

  val displayName: String get() = when (this) {
    RUNNING  -> "Course à pied"
    WALKING  -> "Marche"
    CYCLING  -> "Vélo"
    YOGA     -> "Yoga"
    SWIMMING -> "Natation"
    STRENGTH -> "Musculation"
  }

  val met: Float get() = when (this) {
    RUNNING  -> 9.8f
    WALKING  -> 3.5f
    CYCLING  -> 7.5f
    YOGA     -> 3.0f
    SWIMMING -> 7.0f
    STRENGTH -> 6.0f
  }
}

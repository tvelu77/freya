package io.tvelu77.freya.domain.models

enum class PhaseType {
  MENSTRUAL,
  FOLLICULAR,
  OVULATORY,
  LUTEAL;

  val displayName: String get() = when (this) {
    MENSTRUAL -> "Phase menstruelle"
    FOLLICULAR -> "Phase folliculaire"
    OVULATORY -> "Phase ovulatoire"
    LUTEAL -> "Phase lutéale"
  }

}
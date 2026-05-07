package io.tvelu77.freya.domain.models

data class TCAAlert(
  val triggered: Boolean,
  val consecutiveLowDays: Int,
  val message: String
)

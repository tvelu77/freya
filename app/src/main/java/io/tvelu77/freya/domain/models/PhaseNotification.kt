package io.tvelu77.freya.domain.models

import java.time.LocalDateTime

data class PhaseNotification(
  val title: String,
  val message: String,
  val triggerAt: LocalDateTime
)

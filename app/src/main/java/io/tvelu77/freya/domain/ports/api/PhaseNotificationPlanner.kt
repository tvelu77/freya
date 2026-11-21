package io.tvelu77.freya.domain.ports.api

import io.tvelu77.freya.domain.models.PhaseInfo
import io.tvelu77.freya.domain.models.PhaseNotification
import java.time.LocalDate
import java.time.LocalDateTime

interface PhaseNotificationPlanner {

  fun plan(
    phaseInfo: PhaseInfo?,
    nextPeriodStart: LocalDate?,
    now: LocalDateTime = LocalDateTime.now()
  ): List<PhaseNotification>

}

package io.tvelu77.freya.domain.ports.api

import io.tvelu77.freya.domain.models.PhaseNotification
import java.time.LocalDateTime

interface HydrationReminderPlanner {

  fun plan(now: LocalDateTime = LocalDateTime.now()): List<PhaseNotification>

}

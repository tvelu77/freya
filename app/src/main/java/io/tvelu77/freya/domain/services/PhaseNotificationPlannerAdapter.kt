package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.PhaseInfo
import io.tvelu77.freya.domain.models.PhaseNotification
import io.tvelu77.freya.domain.ports.api.PhaseNotificationPlanner
import jakarta.inject.Inject
import java.time.LocalDate
import java.time.LocalDateTime

class PhaseNotificationPlannerAdapter @Inject constructor() : PhaseNotificationPlanner {

  override fun plan(
    phaseInfo: PhaseInfo?,
    nextPeriodStart: LocalDate?,
    now: LocalDateTime
  ): List<PhaseNotification> {
    val notifications = mutableListOf<PhaseNotification>()
    phaseInfo?.let { phase ->
      val triggerAt = phase.startDate.atTime(PHASE_NOTIFICATION_HOUR, 0)
      if (!triggerAt.isBefore(now)) {
        notifications.add(
          PhaseNotification(
            title = "${phase.phase.displayName} 🌸",
            message = phase.nutritionAdvice.tipMessage,
            triggerAt = triggerAt
          )
        )
      }
    }
    nextPeriodStart?.let { next ->
      val triggerAt = next.minusDays(1).atTime(PERIOD_REMINDER_HOUR, 0)
      if (!triggerAt.isBefore(now)) {
        notifications.add(
          PhaseNotification(
            title = "Rappel doux 🌷",
            message = "Tes règles devraient arriver demain. Pense à prendre soin de toi 🌸",
            triggerAt = triggerAt
          )
        )
      }
    }
    return notifications
  }

  companion object {
    private const val PHASE_NOTIFICATION_HOUR = 9
    private const val PERIOD_REMINDER_HOUR = 18
  }
}

package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.PhaseNotification
import io.tvelu77.freya.domain.ports.api.HydrationReminderPlanner
import jakarta.inject.Inject
import java.time.LocalDateTime

class HydrationReminderPlannerAdapter @Inject constructor() : HydrationReminderPlanner {

  override fun plan(now: LocalDateTime): List<PhaseNotification> =
    REMINDERS
      .map { (hour, message) ->
        PhaseNotification(
          title = "Rappel doux 💧",
          message = message,
          triggerAt = now.toLocalDate().atTime(hour, 0)
        )
      }
      .filter { !it.triggerAt.isBefore(now) }

  companion object {
    private const val MORNING_HOUR = 11
    private const val EVENING_HOUR = 17
    private val REMINDERS = listOf(
      MORNING_HOUR to "Pense à boire un verre d'eau 💧",
      EVENING_HOUR to "Un petit verre d'eau avant le soir 🌙"
    )
  }
}

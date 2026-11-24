package io.tvelu77.freya.domain.ports.spi

import io.tvelu77.freya.domain.models.PhaseNotification

class FakeNotificationScheduler : NotificationScheduler {

  val scheduled = mutableListOf<List<PhaseNotification>>()
  val hydrationScheduled = mutableListOf<List<PhaseNotification>>()
  var cancelCount = 0
  var cancelHydrationCount = 0

  override fun schedule(notifications: List<PhaseNotification>) {
    scheduled.add(notifications)
  }

  override fun scheduleHydrationReminders(notifications: List<PhaseNotification>) {
    hydrationScheduled.add(notifications)
  }

  override fun cancelAll() {
    cancelCount++
  }

  override fun cancelHydrationReminders() {
    cancelHydrationCount++
  }
}

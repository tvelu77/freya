package io.tvelu77.freya.domain.ports.spi

import io.tvelu77.freya.domain.models.PhaseNotification

interface NotificationScheduler {

  fun schedule(notifications: List<PhaseNotification>)

  fun scheduleHydrationReminders(notifications: List<PhaseNotification>)

  fun cancelAll()

  fun cancelHydrationReminders()

}

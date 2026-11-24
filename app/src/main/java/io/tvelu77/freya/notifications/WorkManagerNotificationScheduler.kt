package io.tvelu77.freya.notifications

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.qualifiers.ApplicationContext
import io.tvelu77.freya.domain.models.PhaseNotification
import io.tvelu77.freya.domain.ports.spi.NotificationScheduler
import jakarta.inject.Inject
import java.time.Duration
import java.time.LocalDateTime

class WorkManagerNotificationScheduler @Inject constructor(
  @ApplicationContext private val context: Context
) : NotificationScheduler {

  override fun schedule(notifications: List<PhaseNotification>) {
    WorkManager.getInstance(context).cancelAllWorkByTag(NOTIFICATION_TAG)
    enqueueAll(notifications, NOTIFICATION_TAG)
  }

  override fun scheduleHydrationReminders(notifications: List<PhaseNotification>) {
    cancelHydrationReminders()
    enqueueAll(notifications, HYDRATION_REMINDER_TAG)
  }

  override fun cancelAll() {
    val workManager = WorkManager.getInstance(context)
    workManager.cancelAllWorkByTag(NOTIFICATION_TAG)
    workManager.cancelAllWorkByTag(HYDRATION_REMINDER_TAG)
  }

  override fun cancelHydrationReminders() {
    WorkManager.getInstance(context).cancelAllWorkByTag(HYDRATION_REMINDER_TAG)
  }

  private fun enqueueAll(notifications: List<PhaseNotification>, tag: String) {
    val workManager = WorkManager.getInstance(context)
    notifications.forEach { notification ->
      val delay = Duration.between(LocalDateTime.now(), notification.triggerAt)
        .coerceAtLeast(Duration.ZERO)
      val request = OneTimeWorkRequestBuilder<PhaseNotificationWorker>()
        .setInitialDelay(delay)
        .setInputData(
          workDataOf(
            "title" to notification.title,
            "message" to notification.message
          )
        )
        .addTag(tag)
        .build()
      workManager.enqueue(request)
    }
  }
}

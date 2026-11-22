package io.tvelu77.freya.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.tvelu77.freya.R

const val NOTIFICATION_CHANNEL_ID = "freya_gentle_reminders"
const val NOTIFICATION_TAG = "freya_phase_notifications"

class PhaseNotificationWorker(
  context: Context,
  params: WorkerParameters
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
      ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) !=
      PackageManager.PERMISSION_GRANTED
    ) {
      return Result.success()
    }
    val title = inputData.getString("title") ?: return Result.success()
    val message = inputData.getString("message") ?: return Result.success()
    createChannel()
    val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setContentTitle(title)
      .setContentText(message)
      .setStyle(NotificationCompat.BigTextStyle().bigText(message))
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setAutoCancel(true)
      .build()
    NotificationManagerCompat.from(applicationContext)
      .notify(System.currentTimeMillis().toInt(), notification)
    return Result.success()
  }

  private fun createChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        NOTIFICATION_CHANNEL_ID,
        applicationContext.getString(R.string.notif_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT
      )
      channel.description = applicationContext.getString(R.string.notif_channel_description)
      val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      manager.createNotificationChannel(channel)
    }
  }
}

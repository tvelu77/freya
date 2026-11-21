package io.tvelu77.freya

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp
import io.tvelu77.freya.notifications.NOTIFICATION_CHANNEL_ID

@HiltAndroidApp
class FreyaApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        NOTIFICATION_CHANNEL_ID,
        "Rappels doux",
        NotificationManager.IMPORTANCE_DEFAULT
      )
      channel.description = "Changements de phase et rappels bienveillants"
      getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
  }
}

package io.tvelu77.freya.notifications

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.tvelu77.freya.domain.ports.spi.NotificationScheduler
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

  @Provides
  @Singleton
  fun provideNotificationScheduler(
    @ApplicationContext context: Context
  ): NotificationScheduler = WorkManagerNotificationScheduler(context)
}

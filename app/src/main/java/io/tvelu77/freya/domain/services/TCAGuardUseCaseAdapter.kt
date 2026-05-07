package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.TCAAlert
import io.tvelu77.freya.domain.ports.api.TCAGuardUseCase
import io.tvelu77.freya.domain.ports.spi.FoodRepository
import jakarta.inject.Inject
import java.time.LocalDate

class TCAGuardUseCaseAdapter @Inject constructor(
  private val repository: FoodRepository
): TCAGuardUseCase {

  companion object {
    private const val LOW_CALORIE_THRESHOLD = 1200
    private const val ALERT_AFTER_DAYS = 2
  }

  override suspend fun checkLastDays(
    days: Int,
    today: LocalDate
  ): TCAAlert {
    var consecutiveLowDays = 0
    for (i in 0 until days) {
      val date = today.minusDays(i.toLong())
      val calories = repository.getTotalCaloriesForDate(date)
      if (calories in 1 until LOW_CALORIE_THRESHOLD) {
        consecutiveLowDays++
      } else {
        break
      }
    }
    val triggered = consecutiveLowDays >= ALERT_AFTER_DAYS
    return TCAAlert(
      triggered = triggered,
      consecutiveLowDays = consecutiveLowDays,
      message = if (triggered)
        "Ton corps semble manquer d'énergie depuis $consecutiveLowDays jours. " +
                "Pense à consulter un professionnel de santé si besoin"
      else ""
    )
  }

}
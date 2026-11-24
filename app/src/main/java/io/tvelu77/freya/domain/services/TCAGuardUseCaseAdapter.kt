package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.EnergyTarget
import io.tvelu77.freya.domain.models.TCAAlert
import io.tvelu77.freya.domain.ports.api.TCAGuardUseCase
import io.tvelu77.freya.domain.ports.spi.FoodRepository
import io.tvelu77.freya.domain.ports.spi.UserProfileRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class TCAGuardUseCaseAdapter @Inject constructor(
  private val repository: FoodRepository,
  private val profileRepository: UserProfileRepository
) : TCAGuardUseCase {

  companion object {
    const val LOW_INTAKE_THRESHOLD_KCAL = EnergyTarget.SAFETY_FLOOR
    const val ALERT_AFTER_CONSECUTIVE_DAYS = 3
    private const val ALERT_MESSAGE =
      "Ces derniers jours, tes repas semblent plus légers que d'habitude. " +
        "Ton corps a peut-être besoin de plus d'énergie — " +
        "tu peux en parler à un professionnel de santé si tu le souhaites 💛"
    private const val ALERT_CTA_LABEL = "Voir les ressources d'aide"
  }

  override suspend fun checkLastDays(
    days: Int,
    today: LocalDate
  ): TCAAlert {
    val friendlyMode = profileRepository.getProfile().first().tcaFriendlyMode
    var consecutiveLowDays = 0
    for (i in 0 until days.coerceAtLeast(1)) {
      val date = today.minusDays(i.toLong())
      val entries = repository.getFoodEntriesForDate(date).first()
      val calories = entries.sumOf { it.calories }
      val low = calories in 1 until LOW_INTAKE_THRESHOLD_KCAL ||
        (friendlyMode && entries.isEmpty())
      if (low) {
        consecutiveLowDays++
      } else {
        break
      }
    }
    val triggered = consecutiveLowDays >= ALERT_AFTER_CONSECUTIVE_DAYS
    return TCAAlert(
      triggered = triggered,
      consecutiveLowDays = consecutiveLowDays,
      message = if (triggered) ALERT_MESSAGE else "",
      ctaLabel = if (triggered) ALERT_CTA_LABEL else ""
    )
  }

}

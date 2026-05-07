package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.UserProfile
import io.tvelu77.freya.domain.ports.api.GetUserProfileUseCase
import io.tvelu77.freya.domain.ports.spi.UserProfileRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetUserProfileUseCaseAdapter @Inject constructor(
  private val repository: UserProfileRepository
): GetUserProfileUseCase {

  override fun execute(): Flow<UserProfile> = repository.getProfile()

}
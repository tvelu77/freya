package io.tvelu77.freya.domain.ports.api

import io.tvelu77.freya.domain.models.UserProfile
import kotlinx.coroutines.flow.Flow

interface GetUserProfileUseCase {

  fun execute(): Flow<UserProfile>

}
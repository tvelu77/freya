package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.FoodItem
import io.tvelu77.freya.domain.ports.api.SearchFoodItemsUseCase
import io.tvelu77.freya.domain.ports.spi.FoodItemRepository
import jakarta.inject.Inject

class SearchFoodItemsUseCaseAdapter @Inject constructor(
  private val repository: FoodItemRepository
) : SearchFoodItemsUseCase {

  override suspend fun search(query: String): List<FoodItem> {
    val normalized = query.trim()
    if (normalized.length < MIN_QUERY_LENGTH) return emptyList()
    return repository.searchFoodItems(normalized, DEFAULT_SEARCH_LIMIT)
  }

  override suspend fun quickFoods(): List<FoodItem> =
    repository.getQuickFoods()

  companion object {
    const val MIN_QUERY_LENGTH = 2
    const val DEFAULT_SEARCH_LIMIT = 8
  }
}

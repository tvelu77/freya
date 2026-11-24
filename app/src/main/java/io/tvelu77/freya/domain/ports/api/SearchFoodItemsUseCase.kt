package io.tvelu77.freya.domain.ports.api

import io.tvelu77.freya.domain.models.FoodItem

interface SearchFoodItemsUseCase {

  suspend fun search(query: String): List<FoodItem>

  suspend fun quickFoods(): List<FoodItem>
}

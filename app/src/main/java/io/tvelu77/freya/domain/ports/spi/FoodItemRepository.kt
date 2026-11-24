package io.tvelu77.freya.domain.ports.spi

import io.tvelu77.freya.domain.models.FoodItem

interface FoodItemRepository {

  suspend fun searchFoodItems(query: String, limit: Int): List<FoodItem>

  suspend fun getQuickFoods(): List<FoodItem>
}

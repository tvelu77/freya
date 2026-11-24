package io.tvelu77.freya.domain.ports.spi

import io.tvelu77.freya.domain.models.FoodItem

class FakeFoodItemRepository : FoodItemRepository {

  val items = mutableListOf<FoodItem>()
  var lastQuery: String? = null
  var lastLimit: Int = -1

  override suspend fun searchFoodItems(query: String, limit: Int): List<FoodItem> {
    lastQuery = query
    lastLimit = limit
    return items.filter { it.name.contains(query, ignoreCase = true) }.take(limit)
  }

  override suspend fun getQuickFoods(): List<FoodItem> =
    items.filter { it.isQuick }
}

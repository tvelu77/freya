package io.tvelu77.freya.database.repositories

import io.tvelu77.freya.database.dao.FoodItemDao
import io.tvelu77.freya.database.seed.FoodItemSeedData
import io.tvelu77.freya.domain.models.FoodItem
import io.tvelu77.freya.domain.ports.spi.FoodItemRepository
import jakarta.inject.Inject

class RoomFoodItemRepository @Inject constructor(
  private val dao: FoodItemDao
) : FoodItemRepository {

  override suspend fun searchFoodItems(query: String, limit: Int): List<FoodItem> {
    ensureSeeded()
    return dao.search(query, limit).map { it.toDomain() }
  }

  override suspend fun getQuickFoods(): List<FoodItem> {
    ensureSeeded()
    return dao.getQuickFoods().map { it.toDomain() }
  }

  private suspend fun ensureSeeded() {
    if (dao.count() == 0) dao.insertAll(FoodItemSeedData.items)
  }
}

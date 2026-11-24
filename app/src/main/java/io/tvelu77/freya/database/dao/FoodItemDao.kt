package io.tvelu77.freya.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.tvelu77.freya.database.entities.FoodItemEntity

@Dao
interface FoodItemDao {

  @Query(
    "SELECT * FROM food_items " +
      "WHERE name LIKE '%' || :query || '%' COLLATE NOCASE " +
      "ORDER BY name LIMIT :limit"
  )
  suspend fun search(query: String, limit: Int): List<FoodItemEntity>

  @Query("SELECT * FROM food_items WHERE isQuick = 1 ORDER BY name")
  suspend fun getQuickFoods(): List<FoodItemEntity>

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertAll(items: List<FoodItemEntity>)

  @Query("SELECT COUNT(*) FROM food_items")
  suspend fun count(): Int
}

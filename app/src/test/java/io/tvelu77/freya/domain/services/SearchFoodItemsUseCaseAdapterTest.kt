package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.FoodCategory
import io.tvelu77.freya.domain.models.FoodItem
import io.tvelu77.freya.domain.ports.spi.FakeFoodItemRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchFoodItemsUseCaseAdapterTest {

  private val repository = FakeFoodItemRepository()
  private val adapter = SearchFoodItemsUseCaseAdapter(repository)

  private fun item(name: String, quick: Boolean = false) = FoodItem(
    id = 0,
    name = name,
    caloriesPer100g = 100,
    proteinsPer100g = 10f,
    carbsPer100g = 10f,
    fatsPer100g = 5f,
    category = FoodCategory.OTHER,
    isQuick = quick
  )

  @Test
  fun `search with a blank query returns empty without hitting the repository`() = runTest {
    assertTrue(adapter.search("   ").isEmpty())
    assertNull(repository.lastQuery)
  }

  @Test
  fun `search with a single character returns empty without hitting the repository`() = runTest {
    assertTrue(adapter.search("r").isEmpty())
    assertNull(repository.lastQuery)
  }

  @Test
  fun `search trims the query and delegates it to the repository`() = runTest {
    repository.items.addAll(listOf(item("Riz blanc cuit"), item("Riz complet cuit"), item("Poulet rôti")))

    val results = adapter.search("  riz ")

    assertEquals("riz", repository.lastQuery)
    assertEquals(SearchFoodItemsUseCaseAdapter.DEFAULT_SEARCH_LIMIT, repository.lastLimit)
    assertEquals(listOf("Riz blanc cuit", "Riz complet cuit"), results.map { it.name })
  }

  @Test
  fun `search keeps the domain fields of the matched items`() = runTest {
    repository.items.add(
      FoodItem(
        id = 7,
        name = "Yaourt nature",
        caloriesPer100g = 61,
        proteinsPer100g = 3.5f,
        carbsPer100g = 4.7f,
        fatsPer100g = 3.3f,
        category = FoodCategory.DAIRY,
        defaultServingG = 125f,
        isQuick = true
      )
    )

    val result = adapter.search("yaourt").single()

    assertEquals(7L, result.id)
    assertEquals(61, result.caloriesPer100g)
    assertEquals(FoodCategory.DAIRY, result.category)
    assertEquals(125f, result.defaultServingG)
    assertTrue(result.isQuick)
  }

  @Test
  fun `quickFoods returns only the items flagged as quick`() = runTest {
    repository.items.addAll(listOf(item("Banane", quick = true), item("Poulet rôti")))

    val quick = adapter.quickFoods()

    assertEquals(listOf("Banane"), quick.map { it.name })
  }
}

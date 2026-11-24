package io.tvelu77.freya.domain.models

import org.junit.Assert.assertEquals
import org.junit.Test

class FoodItemTest {

  private val rice = FoodItem(
    id = 1,
    name = "Riz blanc cuit",
    caloriesPer100g = 130,
    proteinsPer100g = 2.7f,
    carbsPer100g = 28f,
    fatsPer100g = 0.3f,
    category = FoodCategory.CEREAL
  )

  @Test
  fun `caloriesFor scales linearly with the quantity in grams`() {
    val cases = listOf(
      0f to 0,
      50f to 65,
      100f to 130,
      150f to 195,
      250f to 325
    )

    cases.forEach { (quantity, expected) ->
      assertEquals("quantity=$quantity", expected, rice.caloriesFor(quantity))
    }
  }

  @Test
  fun `macrosFor scale per 100g with the quantity in grams`() {
    assertEquals(1.35f, rice.proteinsFor(50f), 0.001f)
    assertEquals(14f, rice.carbsFor(50f), 0.001f)
    assertEquals(0.15f, rice.fatsFor(50f), 0.001f)
    assertEquals(2.7f, rice.proteinsFor(100f), 0.001f)
    assertEquals(0f, rice.proteinsFor(0f), 0.001f)
  }

  @Test
  fun `default serving is 100 grams`() {
    assertEquals(100f, rice.defaultServingG)
    assertEquals(FoodItem.DEFAULT_SERVING_G, rice.defaultServingG)
  }

  @Test
  fun `food categories have a french display name`() {
    FoodCategory.entries.forEach { category ->
      assert(category.displayName.isNotBlank())
    }
    assertEquals("Légumes", FoodCategory.VEGETABLE.displayName)
    assertEquals("Produits laitiers", FoodCategory.DAIRY.displayName)
  }
}

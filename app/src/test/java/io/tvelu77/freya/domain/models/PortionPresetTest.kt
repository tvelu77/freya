package io.tvelu77.freya.domain.models

import org.junit.Assert.assertEquals
import org.junit.Test

class PortionPresetTest {

  @Test
  fun `each preset has a french label`() {
    assertEquals("1 portion", PortionPreset.PORTION.displayName)
    assertEquals("1 bol", PortionPreset.BOWL.displayName)
    assertEquals("1 assiette", PortionPreset.PLATE.displayName)
    assertEquals("1 verre", PortionPreset.GLASS.displayName)
  }

  @Test
  fun `portion maps to grams per food category`() {
    val cases = listOf(
      FoodCategory.CEREAL to 150f,
      FoodCategory.PROTEIN to 150f,
      FoodCategory.VEGETABLE to 150f,
      FoodCategory.DAIRY to 150f,
      FoodCategory.FRUIT to 150f,
      FoodCategory.NUT to 30f,
      FoodCategory.SWEET to 30f,
      FoodCategory.FAT to 30f,
      FoodCategory.DRINK to 250f
    )

    cases.forEach { (category, expected) ->
      assertEquals("$category", expected, PortionPreset.PORTION.gramsFor(category))
    }
  }

  @Test
  fun `bowl maps to grams per food category`() {
    assertEquals(250f, PortionPreset.BOWL.gramsFor(FoodCategory.CEREAL))
    assertEquals(250f, PortionPreset.BOWL.gramsFor(FoodCategory.LEGUME))
    assertEquals(200f, PortionPreset.BOWL.gramsFor(FoodCategory.VEGETABLE))
    assertEquals(40f, PortionPreset.BOWL.gramsFor(FoodCategory.NUT))
    assertEquals(15f, PortionPreset.BOWL.gramsFor(FoodCategory.FAT))
  }

  @Test
  fun `plate maps to grams per food category`() {
    assertEquals(200f, PortionPreset.PLATE.gramsFor(FoodCategory.CEREAL))
    assertEquals(250f, PortionPreset.PLATE.gramsFor(FoodCategory.VEGETABLE))
    assertEquals(150f, PortionPreset.PLATE.gramsFor(FoodCategory.PROTEIN))
    assertEquals(250f, PortionPreset.PLATE.gramsFor(FoodCategory.DRINK))
    assertEquals(200f, PortionPreset.PLATE.gramsFor(FoodCategory.FRUIT))
  }

  @Test
  fun `glass maps to grams per food category`() {
    assertEquals(250f, PortionPreset.GLASS.gramsFor(FoodCategory.DRINK))
    assertEquals(250f, PortionPreset.GLASS.gramsFor(FoodCategory.DAIRY))
    assertEquals(200f, PortionPreset.GLASS.gramsFor(FoodCategory.OTHER))
  }
}

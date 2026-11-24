package io.tvelu77.freya.domain.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NutritionAdviceTest {

  @Test
  fun `each phase has key nutrients, concrete food examples and a bienveillant tip`() {
    PhaseType.entries.forEach { phase ->
      val advice = NutritionAdvice.forPhase(phase)

      assertEquals(phase, advice.phaseType)
      assertTrue("$phase nutrients", advice.keyNutrients.isNotEmpty())
      assertTrue("$phase foods", advice.recommendedFoods.isNotEmpty())
      assertTrue("$phase tip", advice.tipMessage.isNotBlank())
    }
  }

  @Test
  fun `menstrual advice targets iron and magnesium with concrete foods`() {
    val advice = NutritionAdvice.forPhase(PhaseType.MENSTRUAL)

    assertTrue(advice.keyNutrients.contains("Fer"))
    assertTrue(advice.keyNutrients.contains("Magnésium"))
    assertTrue(advice.recommendedFoods.contains("Lentilles cuites"))
    assertTrue(advice.recommendedFoods.contains("Chocolat noir 70%"))
  }

  @Test
  fun `follicular advice targets proteins and fibres with concrete foods`() {
    val advice = NutritionAdvice.forPhase(PhaseType.FOLLICULAR)

    assertTrue(advice.keyNutrients.contains("Protéines"))
    assertTrue(advice.keyNutrients.contains("Fibres"))
    assertTrue(advice.recommendedFoods.contains("Œuf entier"))
    assertTrue(advice.recommendedFoods.contains("Quinoa cuit"))
  }

  @Test
  fun `ovulatory advice targets anti-inflammatory foods`() {
    val advice = NutritionAdvice.forPhase(PhaseType.OVULATORY)

    assertTrue(advice.keyNutrients.contains("Oméga-3"))
    assertTrue(advice.recommendedFoods.contains("Saumon grillé"))
    assertTrue(advice.recommendedFoods.contains("Myrtilles"))
  }

  @Test
  fun `luteal advice targets complex carbs and says nothing is forbidden`() {
    val advice = NutritionAdvice.forPhase(PhaseType.LUTEAL)

    assertTrue(advice.keyNutrients.contains("Glucides complexes"))
    assertTrue(advice.recommendedFoods.contains("Riz complet cuit"))
    assertTrue(advice.recommendedFoods.contains("Patate douce cuite"))
    assertTrue(advice.tipMessage.contains("interdit"))
  }

  @Test
  fun `recommended foods exist in the seeded food database names`() {
    val seededNames = setOf(
      "Poulet rôti", "Saumon grillé", "Thon en boîte", "Œuf entier",
      "Riz blanc cuit", "Riz complet cuit", "Pâtes cuites", "Quinoa cuit", "Pain complet",
      "Lentilles cuites", "Épinards cuits", "Brocoli cuit", "Patate douce cuite",
      "Carotte", "Tomate", "Concombre", "Avocat", "Banane", "Pomme", "Myrtilles",
      "Yaourt nature", "Lait demi-écrémé", "Fromage blanc 0%",
      "Amandes", "Noix", "Chocolat noir 70%", "Huile d'olive", "Beurre"
    )

    PhaseType.entries.forEach { phase ->
      NutritionAdvice.forPhase(phase).recommendedFoods.forEach { food ->
        assertTrue("$phase recommends unknown food: $food", food in seededNames)
      }
    }
  }
}

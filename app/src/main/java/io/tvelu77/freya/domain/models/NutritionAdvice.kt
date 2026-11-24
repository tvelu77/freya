package io.tvelu77.freya.domain.models

data class NutritionAdvice(
  val phaseType: PhaseType,
  val calorieSurplus: Int,
  val keyNutrients: List<String>,
  val recommendedFoods: List<String>,
  val tipMessage: String
) {

  companion object {
    fun forPhase(phase: PhaseType): NutritionAdvice = when (phase) {
      PhaseType.MENSTRUAL -> NutritionAdvice(
        phase,
        0,
        listOf("Fer", "Magnésium", "Vitamine C"),
        listOf("Lentilles cuites", "Épinards cuits", "Chocolat noir 70%", "Banane"),
        "Ton corps a besoin de reconfort et de nutriments réparateurs. Aucun aliment n'est interdit 🌿"
      )
      PhaseType.FOLLICULAR -> NutritionAdvice(
        phase,
        -50,
        listOf("Protéines", "Fibres", "Zinc"),
        listOf("Œuf entier", "Quinoa cuit", "Brocoli cuit", "Poulet rôti"),
        "Énergie en hausse — c'est le bon moment pour des repas légers et nutritifs."
      )
      PhaseType.OVULATORY -> NutritionAdvice(
        phase,
        -50,
        listOf("Antioxydants", "Oméga-3", "Vitamine E"),
        listOf("Saumon grillé", "Myrtilles", "Noix", "Avocat"),
        "Privilégie les aliments anti-inflammatoires pour soutenir l'ovulation."
      )
      PhaseType.LUTEAL -> NutritionAdvice(
        phase,
        150,
        listOf("Glucides complexes", "Calcium", "Vitamine B6"),
        listOf("Patate douce cuite", "Riz complet cuit", "Lait demi-écrémé", "Poulet rôti"),
        "Les fringales sont normales — choisis des glucides complexes pour les apaiser. Aucun aliment n'est interdit 🌿"
      )
    }
  }

}

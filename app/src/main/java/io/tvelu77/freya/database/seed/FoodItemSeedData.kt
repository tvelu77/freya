package io.tvelu77.freya.database.seed

import io.tvelu77.freya.database.entities.FoodItemEntity
import io.tvelu77.freya.domain.models.FoodCategory
import io.tvelu77.freya.domain.models.FoodItem

object FoodItemSeedData {

  val items: List<FoodItemEntity> = listOf(
    food("Poulet rôti", 165, 31f, 0f, 3.6f, FoodCategory.PROTEIN, quick = true),
    food("Saumon grillé", 208, 20f, 0f, 13f, FoodCategory.PROTEIN),
    food("Thon en boîte", 116, 26f, 0f, 1f, FoodCategory.PROTEIN),
    food("Œuf entier", 155, 13f, 1.1f, 11f, FoodCategory.PROTEIN, servingG = 50f, quick = true),
    food("Riz blanc cuit", 130, 2.7f, 28f, 0.3f, FoodCategory.CEREAL),
    food("Riz complet cuit", 112, 2.6f, 23f, 0.9f, FoodCategory.CEREAL),
    food("Pâtes cuites", 158, 5.8f, 31f, 0.9f, FoodCategory.CEREAL),
    food("Quinoa cuit", 120, 4.4f, 22f, 1.9f, FoodCategory.CEREAL),
    food("Pain complet", 247, 9f, 41f, 3.4f, FoodCategory.CEREAL, quick = true),
    food("Lentilles cuites", 116, 9f, 20f, 0.4f, FoodCategory.LEGUME),
    food("Épinards cuits", 23, 2.9f, 3.8f, 0.4f, FoodCategory.VEGETABLE),
    food("Brocoli cuit", 35, 2.4f, 7f, 0.4f, FoodCategory.VEGETABLE),
    food("Patate douce cuite", 86, 1.6f, 20f, 0.1f, FoodCategory.VEGETABLE),
    food("Carotte", 41, 0.9f, 10f, 0.2f, FoodCategory.VEGETABLE),
    food("Tomate", 18, 0.9f, 3.9f, 0.2f, FoodCategory.VEGETABLE),
    food("Concombre", 16, 0.7f, 3.6f, 0.1f, FoodCategory.VEGETABLE),
    food("Avocat", 160, 2f, 9f, 15f, FoodCategory.FRUIT),
    food("Banane", 89, 1.1f, 23f, 0.3f, FoodCategory.FRUIT, quick = true),
    food("Pomme", 52, 0.3f, 14f, 0.2f, FoodCategory.FRUIT),
    food("Myrtilles", 57, 0.7f, 14f, 0.3f, FoodCategory.FRUIT),
    food("Yaourt nature", 61, 3.5f, 4.7f, 3.3f, FoodCategory.DAIRY, quick = true),
    food("Lait demi-écrémé", 46, 3.2f, 4.8f, 1.5f, FoodCategory.DAIRY),
    food("Fromage blanc 0%", 45, 8f, 4f, 0.2f, FoodCategory.DAIRY),
    food("Amandes", 579, 21f, 22f, 50f, FoodCategory.NUT),
    food("Noix", 654, 15f, 14f, 65f, FoodCategory.NUT),
    food("Chocolat noir 70%", 598, 7.8f, 46f, 43f, FoodCategory.SWEET),
    food("Huile d'olive", 884, 0f, 0f, 100f, FoodCategory.FAT, servingG = 10f),
    food("Beurre", 717, 0.8f, 0.6f, 81f, FoodCategory.FAT, servingG = 10f)
  )

  private fun food(
    name: String,
    caloriesPer100g: Int,
    proteins: Float,
    carbs: Float,
    fats: Float,
    category: FoodCategory,
    servingG: Float = FoodItem.DEFAULT_SERVING_G,
    quick: Boolean = false
  ) = FoodItemEntity(
    name = name,
    caloriesPer100g = caloriesPer100g,
    proteinsPer100g = proteins,
    carbsPer100g = carbs,
    fatsPer100g = fats,
    category = category.name,
    defaultServingG = servingG,
    isQuick = quick
  )
}

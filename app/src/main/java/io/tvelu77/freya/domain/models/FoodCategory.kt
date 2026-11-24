package io.tvelu77.freya.domain.models

enum class FoodCategory {
  PROTEIN,
  CEREAL,
  LEGUME,
  VEGETABLE,
  FRUIT,
  DAIRY,
  NUT,
  FAT,
  SWEET,
  DRINK,
  OTHER;

  val displayName: String get() = when (this) {
    PROTEIN   -> "Protéines"
    CEREAL    -> "Céréales"
    LEGUME    -> "Légumineuses"
    VEGETABLE -> "Légumes"
    FRUIT     -> "Fruits"
    DAIRY     -> "Produits laitiers"
    NUT       -> "Fruits à coque"
    FAT       -> "Matières grasses"
    SWEET     -> "Sucré"
    DRINK     -> "Boissons"
    OTHER     -> "Autre"
  }
}

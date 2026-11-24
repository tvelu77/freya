package io.tvelu77.freya.domain.models

data class ActivityAdvice(
  val phaseType: PhaseType,
  val title: String,
  val message: String
) {

  companion object {
    fun forPhase(phase: PhaseType): ActivityAdvice = when (phase) {
      PhaseType.MENSTRUAL -> ActivityAdvice(
        phase,
        "Tout en douceur 🌸",
        "Pendant tes règles, un rythme lent est parfait — écoute ton corps et repose-toi si tu en as besoin."
      )
      PhaseType.FOLLICULAR -> ActivityAdvice(
        phase,
        "Ton énergie remonte 🌱",
        "Tu te sens souvent plus en forme en ce moment — bouger un peu plus aujourd'hui peut être agréable, si tu en as envie."
      )
      PhaseType.OVULATORY -> ActivityAdvice(
        phase,
        "Tu as de l'énergie ✨",
        "En phase ovulatoire, une intensité un peu plus soutenue est souvent bien vécue — profite-en si ça te fait envie."
      )
      PhaseType.LUTEAL -> ActivityAdvice(
        phase,
        "La douceur avant tout 🌙",
        "En phase lutéale, un mouvement doux et confortable est tout à fait bien — il n'y a aucune obligation."
      )
    }
  }

}

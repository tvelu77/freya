package io.tvelu77.freya.models

enum class Phase(val title: String, val emoji: String, val advice: String) {
    MENSTRUAL("Menstruelle", "🌸", "Prenez soin de vous, reposez-vous et hydratez-vous bien. Évitez les efforts intenses."),
    FOLLICULAR("Folliculaire", "🌱", "C’est une période d’énergie ! Profitez-en pour être active et planifier des projets."),
    OVULATION("Ovulatoire", "🌺", "Votre énergie et votre libido sont au maximum. Idéal pour les activités sociales et créatives."),
    LUTEAL("Lutéale", "🍂", "Écoutez votre corps, gérez le stress et privilégiez une alimentation équilibrée."),
    UNKNOWN("Inconnu", "", "N'hésitez pas à entrer vos dernières règles pour avoir plus d'informations sur le calendrier.");
}
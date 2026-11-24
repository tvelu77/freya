# Freya 🌸

Freya is a benevolent nutrition & menstrual-cycle tracking app, thought of by and for
menstruating women (adolescents & adults). The app UI is in **French**; code and identifiers
are in English.

It connects nutrition, hormonal cycle, physical activity and mental health — with a
**non-restrictive, anti-disordered-eating by design** approach: qualitative indicators over
culpabilizing numbers, an optional no-kcal mode, supportive alerts and professional
resources. No food is ever "forbidden", no streaks, no red.

## Features (modules)

1. **Cycle tracking** — manual period entry, automatic phase prediction (menstrual /
   follicular / ovulatory / luteal), calendar, history & statistics, gentle phase-change
   notifications (WorkManager).
2. **Smart food log** — local food database (Room), portions in grams with serving presets,
   per-meal macros, daily summary, phase-based nutrition advice, soft target range, and a
   TCA-friendly mode that hides every kcal figure.
3. **Activity & hydration** — sport entries with kcal-burned estimate, daily steps + sleep +
   self-reported fatigue, phase-based intensity advice, water intake with a dynamic daily
   goal and gentle reminders.
4. **Daily energy need** — Mifflin-St Jeor BMR from the profile × activity factor, adjusted
   by phase, sleep and fatigue; shown as a soft estimate, never a target to hit.
5. **Health score** — 0–100 qualitative score (nutrition, hydration, movement, body
   signals), green/yellow/orange only, with encouraging messages.
6. **TCA prevention** — multi-day low-intake alert with a supportive message, professional
   resources screen (France), and customizable settings (notifications, hydration reminders,
   kcal visibility, score number).

## Tech stack

Native Android app in **Kotlin / Jetpack Compose** (Material 3, Nunito type), **Hilt + KSP**,
**Room** + **DataStore** (all data stays on device — no trackers, no ads, no network calls),
**WorkManager** for reminders, Navigation-Compose. `minSdk 26`, `compileSdk 37`, JDK 17.

## Architecture

Hexagonal (ports & adapters) — dependencies point inward; `domain/` has zero
Android/framework imports:

```
presentation/     Compose screens, ViewModels, NavGraph (depends only on ports/api)
domain/ports/api  inbound ports (use-case interfaces)
domain/services   primary adapters (pure Kotlin logic)
domain/ports/spi  outbound ports (repository interfaces)
domain/models     pure domain models
database/         Room entities/DAOs/repositories, DataStore, Hilt wiring
```

See [AGENTS.md](AGENTS.md) for the working rules (TDD with JUnit4 + MockK, clean code,
FR strings in `res/values/strings.xml`) and [docs/architecture-audit.md](docs/architecture-audit.md)
for the audit against the cahier des charges.

## Build & test

```bash
./gradlew :app:compileDebugKotlin :app:testDebugUnitTest   # compile + unit tests
./gradlew :app:assembleDebug                                # APK
```

CI (GitHub Actions) runs compile + unit tests on every PR and push to `main`.

## Values

Respect for the female body, science & education, fight against eating disorders, autonomy
and understanding of oneself.

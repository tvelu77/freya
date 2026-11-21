# Architecture audit — Freya vs. cahier des charges

Kickoff document for the app re-do (issue #1). For each of the 6 modules of the cahier des
charges: what exists today (with file paths), what is missing, and a keep / harden / rebuild
decision. Backlog order at the bottom.

## 0. Foundation — what the codebase looks like today

Android app, Kotlin 2.3 / Jetpack Compose (BOM 2026.05), Hilt + KSP, Room 2.8, DataStore,
Navigation-Compose, adaptive navigation suite. `minSdk 26`, `compileSdk 37`, JDK 17 toolchain.

The hexagonal skeleton is already in place and correct — **keep it**:

```
presentation/    Compose screens + ViewModels + NavGraph (MainActivity gates onboarding)
domain/ports/api inbound ports: TrackCycleUseCase, LogFoodUseCase, PhaseCalculator,
                 GetNutritionAdviceUseCase, GetHealthScoreUseCase, TCAGuardUseCase,
                 GetUserProfileUseCase, UpdateUserProfileUseCase
domain/services  adapters implementing the api ports (pure Kotlin, java.time only)
domain/ports/spi outbound ports: CycleRepository, FoodRepository, UserProfileRepository
domain/models    pure domain models (no Android imports)
database/        Room (FreyaDatabase v1: cycle_entries, food_entries, destructive migration),
                 DataStore user profile, repositories/DAOs/entities, Hilt wiring
                 (DatabaseModule, RepositoryModule, UseCaseModule)
```

Dependencies point inward; `domain/` has no framework imports. Tests: JUnit4 + MockK exist
but only cover `PhaseCalculatorAdapter` and `TrackCycleUseCaseAdapter`.

### Foundation gaps
- No CI (`.github/` does not exist) → issue #3.
- Duplicate file `domain/services/TCAGiardUseCaseAdapter.kt` (typo copy of
  `TCAGuardUseCaseAdapter.kt`) → issue #2.
- Missing unit tests for HealthScore / TCAGuard / LogFood / NutritionAdvice adapters → issue #4.
- `res/values/strings.xml` contains only `app_name`: all UI copy is hardcoded in Composables
  (violates the AGENTS.md rule "FR strings in strings.xml") → fold into issue #24.
- Debug `buildType` has `isMinifyEnabled = true` + `isDebuggable = false`, which fights local
  debugging → fix alongside #3.

## 1. Module 1 — Cycle tracking: KEEP + HARDEN

Exists:
- Manual period entry (start/end) — `StartCycleDialog`, `EndCycleDialog`,
  `TrackCycleUseCaseAdapter.startCycle/endPeriod` (auto-closes a previous ongoing cycle,
  computes real cycle length coerced to 21–45).
- Phase prediction for the 4 phases — `PhaseCalculatorAdapter` (phase boundaries as fractions
  of cycle length) + `CurrentPhaseSection`, `PhaseChip`, `PhaseLegendSection`.
- Calendar + history + next-cycle predictions — `CycleCalendar`, `CycleHistorySection`,
  `NextCyclesSection`, DAOs in `CycleDao`.

Gaps:
- `PhaseCalculatorAdapter.dayInCycle` uses a hardcoded `% 28` modulo while `phaseForDay` uses
  the real `cycleLengthDays` — inconsistent for non-28-day cycles → issue #6.
- No validation (end ≥ start, no overlap) and no edit/delete of past cycles in the UI, although
  the DAO supports both → issue #5.
- No aggregate stats (average length, variability) → issue #7.
- No notifications at all: no WorkManager dependency, no `POST_NOTIFICATIONS` in the manifest →
  issue #8.

## 2. Module 2 — Food tracking: HARDEN

Exists:
- Meal logging per `MealType` with quantity scaling (`ratio = quantity / defaultQuantity`),
  delete, daily totals — `LogFoodUseCaseAdapter`, `FoodViewModel`, `AddFoodSheet`,
  `MealSection`, `NutritionSummaryCard`, `FoodEntryRow`.
- Phase-based advice with nutrients + concrete foods + tip — `NutritionAdvice.forPhase`,
  `NutritionAdviceCard`, `GetNutritionAdviceUseCaseAdapter` (target = base + phase surplus,
  floor 1200).
- TCA-friendly flag — `UserProfile.tcaFriendlyMode`, `FoodUiState.showCalories =
  !tcaFriendlyMode`.

Gaps:
- The "food database" is a hardcoded list of 29 `QuickFood` in `FoodViewModel`'s companion
  object — presentation-layer constant, not searchable/persisted, and entries store precomputed
  kcal instead of per-100g values → issue #9 (Room `food_items` table, seeded).
- No gram/serving presets in `AddFoodSheet` → issue #10.
- Daily macro totals are computed in the ViewModel, not in the domain → fold into #11.
- No goal (maintien / prise / perte): `UserProfile` has no goal field; onboarding `TargetPage`
  only asks a calories slider → issue #14 must add the field.
- Calorie visibility: persisted `tcaFriendlyMode` exists, but the in-screen toggle
  (`onToggleCaloriesVisibility`) is transient and not persisted → issue #13.

## 3. Module 3 — Activity & hydration: BUILD (nothing exists)

- No activity/steps/sleep storage, no sport entries, no kcal-burned calculation → issues #15, #16.
- No water tracking, no dynamic goal, no reminders → issue #17.
- Note: `HealthScore.compute` already accepts `waterMl` / `stepsCount`, but `HomeViewModel`
  calls `getScoreForDay(today)` with defaults (0 / 0), so hydration and activity scores are
  always 0 today — wiring comes with #15–#17 and #19.

## 4. Module 4 — Daily energy adjustment: REBUILD the target calc

- Today "target" = `baseCalories` (user-entered) + phase surplus, floored at 1200
  (`GetNutritionAdviceUseCaseAdapter.getCalorieTarget`). It is not a real energy-need estimate:
  no BMR (profile lacks age/height/weight), no activity factor, no fatigue input → issue #18.

## 5. Module 5 — Health score: HARDEN

- `HealthScore.compute` exists: weights 0.5 nutrition / 0.3 hydration / 0.2 activity, levels
  GOOD ≥ 70 / MEDIUM ≥ 40 / LOW, FR encouraging messages, clamped 0–100.
- Colors are already soft (sage / amber / orange, no red) in `ui/theme/Color.kt` and
  `HealthScoreCard` (animated arc).
- Gaps: inputs are placeholder defaults (see module 3), no "respect des signaux corporels"
  component, nutrition score is purely ratio-to-target (can guilt) → issues #19, #20.

## 6. Module 6 — TCA prevention: HARDEN

- `TCAGuardUseCaseAdapter` exists: alerts after 2 consecutive days below 1200 kcal, with a
  supportive FR message; surfaced on Home via `HomeViewModel.tcaAlert`.
- Gaps: duplicate typo file (issue #2); threshold hardcoded and equal to the calorie floor —
  should become a soft, documented, qualitative signal → issue #21; no professional resources
  screen → issue #22; settings are only global `tcaFriendlyMode` / `notificationsEnabled`, no
  per-feature toggles, no score-number toggle → issue #23.

## Design & UX

Nunito rounded fonts, pastel palette (rose/sage/amber/lavender), dark mode, adaptive nav suite
with 4 tabs — all present. Remaining: empty-state illustrations, full FR `strings.xml`
migration, copy review → issue #24.

## Decisions summary

| Module | Decision | Issues |
|---|---|---|
| Foundation | harden (CI, tests, dedupe, strings) | #2, #3, #4, #24 |
| 1 Cycle | keep + harden | #5, #6, #7, #8 |
| 2 Food | harden (DB to Room, totals to domain, goal field) | #9–#14 |
| 3 Activity & hydration | build new | #15, #16, #17 |
| 4 Energy | rebuild target into real daily need | #18 |
| 5 Health score | harden computation + inputs | #19, #20 |
| 6 TCA prevention | harden (dedupe, soft alert, resources, settings) | #21, #22, #23 |

## Backlog order

1. #1 (this doc) → #2, #3, #4 in parallel (foundation)
2. Module 1: #5 → #6 → #7 → #8
3. Module 2: #9 → #10 → #11 → #12 → #13 → #14
4. Modules 3–4: #15 → #16 → #17 → #18
5. Modules 5–6: #19 → #20 → #21 → #22 → #23
6. Design polish: #24

Rule: one issue = one small PR, tests green (`./gradlew :app:testDebugUnitTest`), TDD per AGENTS.md.

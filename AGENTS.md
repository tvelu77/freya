# AGENTS.md — Freya

Freya is a benevolent nutrition & menstrual-cycle tracking app for menstruating women
(adolescents & adults), built as a native Android app in **Kotlin / Jetpack Compose**.

Core values from the cahier des charges (non-negotiable):

- **Bienveillance first**: soft UI, encouraging French copy, no guilt, no streaks, no punitive UI.
- **Anti-TCA by design**: qualitative indicators over culpabilizing numbers, optional no-kcal mode,
  supportive alerts, professional resources. No food is ever "forbidden".
- **Science & education**: cycle phases, macro/micronutrient advice, energy needs — explained simply.

The feature backlog lives in GitHub issues #1–#24 (baby steps, one per module of the cahier des charges:
cycle, food, activity & hydration, energy adjustment, health score, TCA prevention, design).

## Architecture — hexagonal (ports & adapters)

Dependencies point **inward**. The domain knows nothing about Android, Room, or Compose.

```
presentation/                 UI adapters: Compose screens, ViewModels, NavGraph
  │  depends only on
  ▼
domain/ports/api/             inbound ports: use-case interfaces (PhaseCalculator, TrackCycleUseCase, ...)
domain/services/              primary adapters: classes implementing the api ports (pure Kotlin logic)
domain/ports/spi/             outbound ports: repository interfaces (CycleRepository, FoodRepository, ...)
domain/models/                pure domain models (CycleEntry, FoodEntry, HealthScore, PhaseType, ...)
  ▲  implemented by
database/                     secondary adapters: Room entities/DAOs/repositories, DataStore, Hilt modules
                              (config/, UseCaseModule, RepositoryModule, DatabaseModule)
```

Rules:

- `domain/` must have **zero** Android/framework imports (only `java.time`, `jakarta.inject` for `@Inject`).
- Presentation talks to `ports/api` interfaces only, never to `database/` or `services/` directly.
- New feature = new port in `ports/api` + adapter in `services` + SPI in `ports/spi` if persistence is needed +
  Room/DataStore implementation in `database` + wiring in `database/config` (Hilt).
- ViewModels expose immutable `StateFlow<UiState>`; Composables stay thin and dumb.
- Navigation: sealed `Screen` + `NavHost` in `presentation/navigation/NavGraph.kt`.

## TDD — test-driven development

We write tests **first**, then the minimal code to make them pass (red → green → refactor).

- Every new behavior in `domain/services` starts with a failing unit test in
  `app/src/test/java/io/tvelu77/freya/domain/services/`.
- Stack: **JUnit 4** + **MockK** (already declared in `app/build.gradle.kts`). Tests use backtick names,
  e.g. ``fun `getCurrentPhase returns MENSTRUAL on day 3`()`` — see `PhaseCalculatorAdapterTest.kt`.
- Table-driven tests for pure functions (phase calculation, health score, energy need, kcal formulas).
- UI-affecting logic lives in ViewModels so it is testable; keep Composables untestable-presentational.
- A PR is not done until `./gradlew :app:testDebugUnitTest` is green (CI enforces this, see issue #3).

## Clean code

- **No comments in code** — choose names that explain intent.
- Small functions, single responsibility, early returns, no nested ifs.
- Immutable `data class`es for models and UI state; `sealed class` / `sealed interface` for phases, score
  levels, routes, one-shot events.
- No magic numbers: named constants (e.g. `DEFAULT_CYCLE_LENGTH_DAYS = 28`).
- 2-space indentation, Kotlin idioms (`let`/`apply`/`run` where they read better), no `!!`.
- Code and identifiers in English; **user-facing strings in French**, centralized in `res/values/strings.xml`.
- Keep modules honest: fix the demonstrated cause, not the symptom; smallest correct change per PR.

## UI / UX rules (from the cahier des charges)

- Rounded typography: Nunito (already in `res/font/`), soft pastel palette in `ui/theme/Color.kt`.
- Health score and alerts: **green / yellow / orange only — never red**.
- Copy examples: 🟢 « Tu as bien nourri ton corps aujourd'hui », 🟠 « Ton corps a peut-être besoin de plus
  d'énergie ». Never judgmental, never a streak counter.
- When `hideCalories` is enabled (TCA-friendly mode, issue #13), no kcal figure may appear anywhere.
- Empty states get soft, inclusive illustration placeholders.

## Build & verify

```bash
./gradlew :app:compileDebugKotlin :app:testDebugUnitTest   # compile + unit tests
./gradlew :app:assembleDebug                                # APK
```

JDK 17 toolchain (configured in `app/build.gradle.kts`). minSdk 26, target/compileSdk 37.

## Workflow — baby steps

- Work from the GitHub issues (#1–#24): **one issue = one small PR**, smallest correct change.
- Branch naming: `vibe/<short-slug>-420a40` (or the targeted PR head branch when updating an existing PR).
- Conventional commits, matching git history: `feat:`, `fix:`, `test:`, `refactor:`, `build:`, `chore:`
  (optionally with scope, e.g. `feat(cycle): ...`).
- Open PRs as **draft** until ready; CI (compile + unit tests) must stay green on every push.
- Update a targeted PR in place instead of opening duplicates; never force-push without approval.

## Safety

- Never commit secrets, API keys, keystores, or `.env` values — check `git diff` before committing.
- Do not weaken auth, expose the app publicly, or add network calls to external services without discussion.

---
name: figma-screen-v2-migration
description: Implement a redesigned screen from Figma into a V2 module while preserving the legacy screen and logic compatibility. Use when user provides a figma.com link and asks to rebuild an existing screen as V2, with modular composables and updated navigation/DI wiring.
---

# Figma Screen V2 Migration

Use this skill when the user wants to:
- Keep an existing screen as fallback (`Screen.kt`, `ViewModel.kt`)
- Build a new Figma-aligned version in a `v2` submodule
- Reuse and extend existing ViewModel logic
- Split UI into small composables/files (not one giant screen file)

## Inputs to collect

Before coding, confirm these inputs:
- Figma URL (with `fileKey` and `node-id`)
- Existing screen file path (`.../screens/<feature>/<Feature>Screen.kt`)
- Existing ViewModel path (`.../screens/<feature>/<Feature>ScreenViewModel.kt`)
- V2 naming preference (default: `<Feature>ScreenV2`, `<Feature>ScreenViewModelV2`)
- Navigation target (should app route use V2 immediately?)

If any are missing, ask concise questions.

## Required workflow

1. **Analyze existing feature first**
   - Read old screen + ViewModel + related detail/dialog components.
   - Identify current data flow, reload triggers, and API methods.
   - Keep old files untouched unless user explicitly asks otherwise.

2. **Analyze Figma using MCP**
   - Use `get_design_context` with parsed `fileKey` + `nodeId`.
   - Treat generated React/Tailwind as reference only.
   - Adapt to project tokens, typography, components, and navigation shell.

3. **Create V2 module structure**
   - Place V2 in `.../screens/<feature>/v2/`.
   - Create multiple files (recommended minimum):
     - `FeatureScreenV2.kt` (entry + composition root)
     - `FeatureScreenViewModelV2.kt` (state/events/business logic)
     - `FeatureScreenContentV2.kt` (layout composition)
     - `FeatureScreenPreview.kt` (optional previews)
     - Subfolders for reusable blocks:
       - `title_row/`, `summary/`, `period_chips/`, `transaction_card/`, `empty_state/`, etc.
   - Avoid giant single composable files unless user requests it.

4. **Logic migration rules**
   - Reuse existing API and formatting helpers whenever possible.
   - Preserve old behavior unless Figma/requirements require changes.
   - Add V2-only state fields cleanly (e.g., selected period chips, derived aggregates).
   - Keep reload flows explicit (e.g., after edit/delete, reload with current filter).

5. **Wire app and DI safely**
   - Register `ViewModelV2` in DI (`Koin.kt` or equivalent).
   - Update navigation route to render `ScreenV2` only when user wants activation.
   - Keep old `Screen` and `ViewModel` registrations if fallback is required.

6. **Validation**
   - Run module compile (`./gradlew :composeApp:compileDebugKotlinAndroid` or project equivalent).
   - Run lints for changed files.
   - Fix issues introduced by changes.

## Composition and file-splitting standards

- Keep each composable file focused on one UI block.
- Prefer parameterized stateless composables in leaf components.
- Keep business logic in ViewModel/events, not inside UI rendering blocks.
- Share utility formatters in `screens/utils` or feature `v2/*Formatting.kt` files.
- Use design tokens (`MaterialTheme.colorScheme`, project typography, resources).

## Naming conventions

- `FeatureScreenV2`
- `FeatureScreenViewModelV2`
- `FeatureScreenStateV2`
- `FeatureScreenContentV2`
- Reusable blocks with descriptive names:
  - `FeatureTitleRow.kt`
  - `FeatureSummaryCard.kt`
  - `FeaturePeriodChipsRow.kt`
  - `FeatureTransactionCard.kt`

## Non-negotiables

- Do not delete legacy files unless explicitly requested.
- Do not silently break old route fallback paths.
- Do not copy raw Tailwind/React output into Compose.
- Do not leave V2 as one monolithic composable.

## Delivery checklist

- [ ] Legacy screen/viewmodel preserved
- [ ] V2 files created in `v2` submodule
- [ ] UI split into multiple composable files
- [ ] ViewModelV2 contains migrated/extended logic
- [ ] Navigation points to V2 (if requested)
- [ ] DI includes V2 ViewModel
- [ ] Build compiles
- [ ] Short summary of file map + behavior changes provided

## Additional resource

- See [examples.md](examples.md) for a concrete migration pattern.

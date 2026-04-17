# Examples

## Example request patterns

- "Here is a Figma link and old `SalesScreen.kt`, create `sales/v2` and keep old screen."
- "Migrate this module to V2 from Figma, split into small composable files."
- "Implement this redesign as `ScreenV2` + `ViewModelV2` and wire route to V2."

## Example output structure (Compose feature)

```text
composeApp/src/commonMain/kotlin/com/lasso/lassoapp/screens/sales/v2/
├── SalesScreenV2.kt
├── SalesScreenViewModelV2.kt
├── SalesScreenContentV2.kt
├── SalesScreenPreview.kt
├── title_row/
│   └── SalesScreenTitleRow.kt
├── summary/
│   └── SalesSummaryCard.kt
├── period_chips/
│   └── SalesPeriodChipsRow.kt
├── payment_breakdown/
│   ├── PaymentMiniCard.kt
│   └── SalesPaymentBreakdownRow.kt
├── transaction_card/
│   ├── OutlinedActionButton.kt
│   └── SalesTransactionCard.kt
└── empty_state/
    └── EmptySalesBody.kt
```

## Example migration decisions

1. Preserve old files:
   - Keep `screens/sales/SalesScreen.kt`
   - Keep `screens/sales/SalesScreenViewModel.kt`

2. Add V2:
   - New state/events in `SalesScreenViewModelV2`
   - New composable tree in `screens/sales/v2/`

3. Wiring:
   - `Koin.kt`: add `factoryOf(::SalesScreenViewModelV2)`
   - `App.kt`: route `SalesDestination` to `SalesScreenV2`

4. Behavior parity:
   - Keep detail bottom sheet integration
   - Keep delete/edit reload behavior
   - Add new Figma-specific sections (summary cards/chips/etc.)

## Example Figma processing steps

1. Parse URL:
   - `https://www.figma.com/design/<fileKey>/<name>?node-id=1-3085...`
   - `fileKey=<fileKey>`, `nodeId=1:3085`

2. Call MCP:
   - `get_design_context(fileKey, nodeId, clientLanguages="kotlin", clientFrameworks="compose-multiplatform")`

3. Convert:
   - Map visual regions to modular composables
   - Map colors/typography to project theme
   - Map interactions to ViewModel events

# Accessible layout conventions

The accessibility refactor preserves the existing colors, typography, and minimum control sizes.
Controls grow with their content; system text scaling is never clamped.

## Shared layouts

- `AdaptiveEqualPair` gives both children equal width and stacks them when their minimum widths
  cannot fit. `AdaptiveActionRow` reserves the action's required width and gives content the rest.
  Callers supply content requirements, not calculated weight ratios.
- `LassoDialogFrame` owns the stable window viewport, keyboard insets, card bounds, and outside
  dismissal. Its full-window host requires a backdrop tap handler and a handler consuming blank
  card taps. Keep those handlers together in the frame.
- `DialogContentScaffold` owns navigation, spacing, and header/body/action slots. Header and actions
  stay fixed when they leave at least 96 dp for content. Otherwise the header, then actions, join
  the scrolling body. Natural measurements are independent of the chosen viewport to prevent
  oscillating sizes. Movable content and focus restoration retain field state during relocation.
- POS uses the grid's native sticky header with a stable item key. It switches to a normal grid
  header when space is insufficient, keeping search inside the same lazy container. Ventas scrolls
  its title, summary, filters, and history together in the same list.

## Deliberate custom measurements

`FittedAmountText` is currency-only. It caches fitting against content, typography, width, density,
and font resolver, searches the existing 1-sp size steps, and wraps at the minimum rather than
truncating digits. Retain this common implementation until native autosizing has been verified on
both Android and iOS.

The compact cart measures its actual heading and newest two-line row with `SubcomposeLayout`.
These probes are not placed and clear their accessibility semantics. Their natural sizes determine
the compact viewport; the checkout bar is reserved before the cart receives its maximum height.
One-line names are used only when the full preview cannot fit. The editor shows the full name.

## Checkout request lifetime

The screen retains the checkout ViewModel independently of dialog visibility. Starting a sale
captures the item quantities/prices, employee, discount, and payments before launching the request.
Closing/reopening cannot reset an in-flight submission. Failures retain the payment draft for retry.
The screen consumes each completion once, even when the dialog is hidden, and removes only the
submitted units from the cart. New additions and differently priced edits are preserved.

`CheckoutRepository` is an internal test seam; backend payloads and database schemas are unchanged.

## Verification

Use JDK 17 with the current Gradle wrapper for test tasks. No global Java configuration is required.

```powershell
.\gradlew.bat :composeApp:testDebugUnitTest :composeApp:assembleDebug :composeApp:assembleRelease
.\gradlew.bat :composeApp:assembleDebugAndroidTest
```

`AccessibilityRegressionTest` and `CheckoutSubmissionTest` cover sizing policy, amount fitting,
payment initialization/validation, in-flight reopening, snapshots, retry, and cart reconciliation.
`AccessibilityLayoutTest` uses local presentation fixtures and fake callbacks, covering normal,
180%, and 200% text, narrow/short viewports, keyboard focus, headers, navigation, checkout, appointment,
cart, and sale/payment editing. It never registers a real sale or product.

Install the debug app and test APK on the intended emulator, then run only that device:

```powershell
adb -s emulator-5554 shell am instrument -w -e class com.lasso.lassoapp.AccessibilityLayoutTest com.lasso.lassoapp.test/androidx.test.runner.AndroidJUnitRunner
```

Reports and captures belong in ignored build output. iOS compilation and visual verification on
macOS remain required before cross-platform release; Android results do not establish iOS parity.

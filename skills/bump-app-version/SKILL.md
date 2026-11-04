---
name: bump-app-version
description: Bump this repository's LASSO app version and build number across Android, iOS, and the shared Settings label. Use for app release version changes.
---

# Bump the app version

Use the requested version and build number as a pair, such as `1.5.6 (17)`.
If only the version is supplied, increment the current synchronized build number
by one. If existing build numbers disagree, resolve the intended build number
before editing.

Update every location below in the same change. Paths are relative to the
repository root.

| File | Required values |
| --- | --- |
| `composeApp/build.gradle.kts` | `android.defaultConfig.versionName` = version; `versionCode` = integer build number |
| `composeApp/src/commonMain/kotlin/com/lasso/lassoapp/screens/configuration/ConfigurationScreen.kt` | Display label = `LASSO v<version> (<build>)` |
| `iosApp/iosApp.xcodeproj/project.pbxproj` | Every app-target `MARKETING_VERSION` = version; every `CURRENT_PROJECT_VERSION` = build number, including both Debug and Release |
| `iosApp/iosApp/Info.plist` | `CFBundleShortVersionString` = version; `CFBundleVersion` = build number, stored as plist strings |

Commit `47216bf6fbfc8a9cfbcaf7cdd57ec5b3195d3bc7` is the reference
for this four-file bump procedure. Inspect the current files rather than assuming
their old values or line numbers still match that commit.

## Verification

- Search tracked source and configuration for the previous version and for
  `versionCode`, `versionName`, `CURRENT_PROJECT_VERSION`, `MARKETING_VERSION`,
  `CFBundleShortVersionString`, `CFBundleVersion`, and `LASSO v`. Check any new
  app-version locations discovered by this search; leave dependency versions and
  historical release documentation alone.
- Confirm Android, the displayed label, both Xcode configurations, and the plist
  agree on the requested version and build. Parse the plist to confirm it remains
  valid XML.
- Run `git diff --check` and review the diff for unrelated edits. A version-only
  change needs consistency checks; a full app build is only needed if executable
  logic or build behavior also changes.

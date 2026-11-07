# Lasso Compose Multiplatform

## Desktop (Windows, macOS, Linux)

The desktop app uses the shared login, persisted session, calendar ViewModel, API,
appointment forms, calendar blocks, event cards, overlap placement and drag snapping.
Its shell lives in `composeApp/src/desktopMain`: a month/team sidebar, day and
three-day views, pinned column headers, scrollbars, and direct mouse dragging.
Only login and calendar are exposed in this first version. Sign in with an existing
Lasso account; the app uses the same backend as the mobile app.

Use JDK 17 (the repository's Gradle 8.13 wrapper does not support running on JDK 24):

```powershell
$env:JAVA_HOME = 'C:\Users\Diego\.jdks\ms-17.0.15' # or your installed JDK 17
.\gradlew.bat :composeApp:run
```

Run shared calendar and desktop mouse interaction tests:

```powershell
.\gradlew.bat :composeApp:desktopTest
```

Build an app image with `:composeApp:createDistributable`. Native installers use
`:composeApp:packageDistributionForCurrentOS` and must be built on their target OS
(Windows MSI packaging also needs WiX). Session data is stored in the user's
`LOCALAPPDATA/Lasso` on Windows, `Library/Application Support/Lasso` on macOS,
or `$XDG_DATA_HOME/lasso` (default `~/.local/share/lasso`) on Linux.

Dragging currently changes time within the same day and employee, matching mobile.
Calendar times follow the device's system timezone, also matching mobile.

Window styling uses the light `#F8F8FA` background on Windows 11 and macOS.
Windows uses native DWM caption colors. macOS uses a transparent native title bar
over the app background with a light appearance and reserved space for the title
and traffic-light controls. Verify native macOS appearance on a Mac; Windows
builds cannot exercise AppKit rendering or produce the macOS distribution.

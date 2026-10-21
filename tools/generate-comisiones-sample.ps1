param([string]$GradleUserHome = $(if ($env:GRADLE_USER_HOME) { $env:GRADLE_USER_HOME } else { Join-Path $env:USERPROFILE '.gradle' }))
$ErrorActionPreference = 'Stop'
Push-Location (Split-Path $PSScriptRoot -Parent)
try {
    $cache = Join-Path $GradleUserHome 'caches/modules-2/files-2.1'
    function Find-Jar([string]$artifact, [string]$version) {
        $result = Get-ChildItem (Join-Path $cache "$artifact/$version") -Recurse -Filter '*.jar' |
            Where-Object { $_.Name -notmatch '-(sources|javadoc|all)\.jar$' } | Select-Object -First 1
        if (!$result) { throw "Missing cached Kotlin dependency: $artifact $version. Build the app first." }
        return $result.FullName
    }
    $stdlib = Find-Jar 'org.jetbrains.kotlin/kotlin-stdlib' '2.3.0'
    $compilerClasspath = @(
        (Find-Jar 'org.jetbrains.kotlin/kotlin-compiler-embeddable' '2.3.0'),
        $stdlib,
        (Find-Jar 'org.jetbrains.kotlin/kotlin-script-runtime' '2.3.0'),
        (Find-Jar 'org.jetbrains.kotlin/kotlin-reflect' '1.6.10'),
        (Find-Jar 'org.jetbrains.kotlinx/kotlinx-coroutines-core-jvm' '1.10.2'),
        (Find-Jar 'org.jetbrains/annotations' '13.0')
    ) -join ';'
    New-Item -ItemType Directory -Force tmp/pdfs | Out-Null
    $sourceRoot = 'composeApp/src/commonMain/kotlin/com/lasso/lassoapp/utils'
    & java -cp $compilerClasspath org.jetbrains.kotlin.cli.jvm.K2JVMCompiler -no-stdlib -no-reflect `
        -classpath $stdlib -d tmp/pdfs/sample.jar `
        "$sourceRoot/CommissionPdfDocument.kt" "$sourceRoot/ComisionesPdfRenderer.kt" tools/ComisionesPdfSample.kt
    if ($LASTEXITCODE -ne 0) { throw 'Sample compilation failed.' }
    & java -cp "tmp/pdfs/sample.jar;$stdlib" com.lasso.lassoapp.utils.ComisionesPdfSampleKt
    if ($LASTEXITCODE -ne 0) { throw 'Sample generation failed.' }
} finally { Pop-Location }

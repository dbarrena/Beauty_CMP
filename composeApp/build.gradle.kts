import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    jvm("desktop")
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.ktor.client.okhttp)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2")
                implementation("net.java.dev.jna:jna:5.6.0")
            }
        }
        val desktopTest by getting {
            dependencies {
                implementation(compose.desktop.uiTestJUnit4)
            }
        }
        androidMain.dependencies {
            implementation(libs.androidx.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
        }
        androidInstrumentedTest.dependencies {
            implementation("androidx.compose.ui:ui-test-junit4:1.9.4")
            implementation("androidx.test:runner:1.7.0")
            implementation("androidx.test.espresso:espresso-core:3.7.0")
            implementation("androidx.test.ext:junit:1.3.0")
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.animation)
            implementation(libs.compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.jetbrains.kotlinx.datetime)
            implementation(libs.navigation.compose)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.material.icons.extended)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)

            implementation(libs.vico.multiplatform)
            implementation(libs.vico.multiplatform.m3)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "com.lasso.lassoapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.lasso.lassoapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 19
        versionName = "1.5.8"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.9.4")
    ksp(libs.androidx.room.compiler)
    add("kspDesktop", libs.androidx.room.compiler)
}

compose.desktop {
    application {
        mainClass = "com.lasso.lassoapp.desktop.MainKt"
        if (System.getProperty("os.name").startsWith("Mac", ignoreCase = true)) {
            // The macOS application menu name must be set before the JVM initializes AWT.
            jvmArgs += "-Xdock:name=Lasso"
        }
        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb)
            packageName = "Lasso"
            packageVersion = "1.5.8"
            // Compose also uses this icon for the Dock when launching the Gradle run task.
            macOS { iconFile.set(project.file("desktopIcons/lasso.icns")) }
            windows { iconFile.set(project.file("desktopIcons/lasso.ico")) }
            modules("java.sql", "jdk.unsupported", "jdk.crypto.ec")
        }
    }
}

if (System.getProperty("os.name").startsWith("Mac", ignoreCase = true)) {
    val openMacApp = tasks.register<Exec>("openMacApp") {
        dependsOn("createDistributable")
        // Use Launch Services rather than executing Contents/MacOS/Lasso directly.
        commandLine(
            "/usr/bin/open", "-n", "-W",
            layout.buildDirectory.dir("compose/binaries/main/app/Lasso.app").get().asFile.absolutePath,
        )
    }
    tasks.matching { it.name == "run" }.configureEach {
        // Dock tooltips can retain "java" for a bare JVM despite -Xdock:name.
        // Launch the native Lasso.app bundle so macOS uses its application identity.
        dependsOn(openMacApp)
        enabled = false
    }
}

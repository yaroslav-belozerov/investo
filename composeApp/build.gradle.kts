import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class) compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(), iosArm64(), iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class) wasmJs {
        moduleName = "composeApp"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            kotlin.srcDirs(
                layout.buildDirectory.dir("generated/kotlin/"),
                "build/generated/ksp/metadata/commonMain/kotlin"
            )
        }
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.ktor.client.okhttp)

            implementation("io.insert-koin:koin-android:4.0.0")
            implementation("org.jetbrains.compose.material3:material3:1.7.0")
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(compose.components.resources)

            val koinVersion = "4.0.0"
            implementation("io.insert-koin:koin-compose:$koinVersion")
            implementation("io.insert-koin:koin-compose-viewmodel:$koinVersion")
            implementation("io.insert-koin:koin-compose-viewmodel-navigation:$koinVersion")

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.3")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            implementation(libs.ktor.client.logging)
            implementation("com.squareup.okio:okio:3.10.2")

            implementation("com.russhwolf:multiplatform-settings:1.2.0")
            implementation("com.russhwolf:multiplatform-settings-coroutines:1.2.0")
            implementation("com.russhwolf:multiplatform-settings-no-arg:1.2.0")

            implementation(compose.material3AdaptiveNavigationSuite)
            implementation("org.jetbrains.compose.material3.adaptive:adaptive:1.0.0-alpha03")
            implementation("org.jetbrains.compose.material3.adaptive:adaptive-layout:1.0.0-alpha03")
            implementation("org.jetbrains.compose.material3.adaptive:adaptive-navigation:1.0.0-alpha03")

            val napierVersion = "2.7.1"
            implementation("io.github.aakira:napier:$napierVersion")

            implementation(libs.lyricist)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation("io.ktor:ktor-client-okhttp:3.0.3") {
                exclude(group="com.squareup.okio", module="okio")
            }

            implementation("org.jetbrains.compose.material3:material3-desktop:1.7.3")
            api("org.slf4j:slf4j-simple:2.0.7")
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        wasmJsMain.dependencies {
            implementation("org.jetbrains.compose.material3:material3-wasm-js:1.7.0")
        }
    }
}

android {
    namespace = "org.yaabelozerov.investo"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.yaabelozerov.investo"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    add("kspCommonMainMetadata", "cafe.adriel.lyricist:lyricist-processor:${libs.versions.lyricist.get()}")
}

compose.desktop {
    application {
        buildTypes.release.proguard {
            version.set("7.6.1")
            configurationFiles.from("proguard-rules.pro")
        }

        mainClass = "org.yaabelozerov.investo.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.yaabelozerov.investo"
            packageVersion = "1.0.0"
        }
    }
}

val buildConfigGenerator by tasks.registering(Sync::class) {
    val properties = Properties().apply {
        load(project.rootProject.file("local.properties").inputStream())
    }
    val debugBuildFlag: Provider<Boolean> =
        provider { properties.getProperty("debug") == "true" }
    val buildConfigFileContents: Provider<TextResource> = debugBuildFlag.map { debugBuild ->
        resources.text.fromString(
            """
              |package org.yaabelozerov.investo.config
              |
              |object BuildConfig {
              |  const val DEBUG_BUILD = $debugBuild
              |}
              |
            """.trimMargin()
        )
    }

    from(buildConfigFileContents) {
        rename { "BuildConfig.kt" }
        into("investo/composeapp/generated/config/")
    }

    into(layout.buildDirectory.dir("generated/kotlin/"))
}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().all {
    if(name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
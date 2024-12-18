import java.util.Properties

/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ozoneGenerator)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.contexts.cosmic"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.contexts.cosmic"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "DEFAULT_FEED", "\"at://did:plc:z72i7hdynmk6r22z27h6tvur/app.bsky.feed.generator/whats-hot\"")
        buildConfigField("String", "TENOR_API_KEY", getLocalProperty("TENOR_API_KEY") ?: "no_tenor_key")
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.incremental", "true")
        arg("room.expandProjection", "true")
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.koin.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.nav.suite)
    implementation(libs.androidx.navigation)
    implementation(libs.datastore.preferences)
    implementation(libs.accompanist.permissions)
    implementation(libs.splashscreen.android)
    implementation(libs.okhttp.logging)

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.extended.icons)
    implementation(libs.google.fonts)

    implementation(libs.bundles.ktor.common)
    implementation(libs.bundles.coil.common)
    implementation(libs.bundles.koin.common)
    implementation(libs.bundles.room.common)

    implementation(libs.media3.ui)
    implementation(libs.exoplayer)
    implementation(libs.exoplayer.hls)

    testImplementation(libs.koin.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.agent)
    testImplementation(libs.turbine)
    testImplementation(libs.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.chucker.debug)
    debugImplementation(libs.leakcanary)

    releaseImplementation(libs.chucker.noop)

    api(libs.ozone)
    lexicons(libs.ozone)
    ksp(libs.room.compiler)
}

tasks.withType<com.google.devtools.ksp.gradle.KspTaskJvm> {
    dependsOn("generateLexicons")
}

lexicons {
    namespace.set("com.contexts.cosmic")
    outputDirectory.set(project.layout.buildDirectory.dir("out"))
}

fun getLocalProperty(key: String): String? {
    val props = Properties()
    props.load(File(rootDir.absolutePath + "/local.properties").inputStream())
    val property = props.getProperty(key, "")
    if (property.isNullOrEmpty()) {
        throw GradleException("No value found for the key: $key")
    }
    return property
}

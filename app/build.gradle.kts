//plugins {
//    alias(libs.plugins.android.application)
//    alias(libs.plugins.kotlin.android)
//}
//
//android {
//    namespace = "com.example.cnicscanner"
//    compileSdk = 36
//
//    defaultConfig {
//        applicationId = "com.example.cnicscanner"
//        minSdk = 24
//        targetSdk = 36
//        versionCode = 1
//        versionName = "1.0"
//
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//    }
//
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    }
//    kotlinOptions {
//        jvmTarget = "11"
//    }
//    buildFeatures {
//        compose = true
//    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.5.10"
//    }
//}
//
//dependencies {
//
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.lifecycle.runtime.ktx)
//    implementation(libs.androidx.activity.compose)
//    implementation(platform(libs.androidx.compose.bom))
//    implementation(libs.androidx.ui)
//    implementation(libs.androidx.ui.graphics)
//    implementation(libs.androidx.ui.tooling.preview)
//    implementation(libs.androidx.material3)
//
//    // Camera and image processing
//    implementation(libs.androidx.camera.core)
//    implementation(libs.androidx.camera.camera2)
//    implementation(libs.androidx.camera.lifecycle)
//    implementation(libs.androidx.camera.view)
//    implementation(libs.androidx.camera.extensions)
//
//    // ML Kit for text detection (commented out to save space)
//    implementation(libs.text.recognition)
//    implementation(libs.object1.detection)
//
//    // Image processing
//    implementation(libs.androidx.exifinterface)
//
//    // Permissions
//    implementation(libs.accompanist.permissions)
//
//    // Image loading
//    implementation(libs.coil.compose)
//
//    // Math utilities for image processing
//    implementation(libs.kotlin.stdlib)
//    implementation(libs.play.services.mlkit.text.recognition.common)
//    implementation(libs.play.services.mlkit.text.recognition)
//    implementation(libs.vision.common)
//    // Play Services Tasks API
//    implementation(libs.play.services.tasks)
//
//    // Kotlin coroutines Play Services extension (for await())
//    implementation(libs.kotlinx.coroutines.play.services)
//
//    implementation(libs.play.services.mlkit.document.scanner)
//
//
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.ui.test.junit4)
//    debugImplementation(libs.androidx.ui.tooling)
//    debugImplementation(libs.androidx.ui.test.manifest)
//}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.cnicscanner"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.cnicscanner"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.ui)
    implementation(libs.androidx.material)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.play.services.mlkit.text.recognition.common)
    implementation(libs.play.services.mlkit.text.recognition)
    debugImplementation(libs.ui.tooling)
    implementation(libs.play.services.mlkit.document.scanner)
    implementation(libs.androidx.activity.compose)
    implementation(libs.coil.compose)
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    kotlin("kapt")
}

android {
    namespace = "com.mastercoding.mystoryappsubmissionawal"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mastercoding.mystoryappsubmissionawal"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn") + freeCompilerArgs
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.play.services.maps)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.coil.compose)
    implementation(libs.glide)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.core.testing)
    annotationProcessor(libs.compiler)
    //paging
    implementation (libs.androidx.paging.runtime)
    implementation (libs.androidx.paging.runtime.v310)
    implementation (libs.androidx.paging.common)
    //room
    implementation (libs.androidx.room.runtime)
    implementation (libs.androidx.room.ktx)
    //kapt ("androidx.room:room-compiler:2.5.0")
    //coroutines
    implementation (libs.kotlinx.coroutines.android)
    implementation (libs.kotlinx.coroutines.core)
    //dagger
    implementation (libs.androidx.hilt.lifecycle.viewmodel)
    implementation (libs.hilt.android)
  //  kapt("com.google.dagger:hilt-compiler:2.44.2")

    //testing
    // JUnit for unit testing
    testImplementation(libs.junit)

    // MockK for mocking dependencies
    testImplementation(libs.mockk)

    // Paging 3 testing
    testImplementation(libs.androidx.paging.common.ktx)
    testImplementation(libs.androidx.paging.runtime)

    // Coroutine testing utilities
    testImplementation(libs.kotlinx.coroutines.test)

    // LiveData testing utilities
    testImplementation(libs.androidx.core.testing)

    // Retrofit mock (if you're using Retrofit)
    testImplementation(libs.retrofit)
    testImplementation(libs.converter.gson)
    testImplementation(libs.mockwebserver)


    // Mockito (Optional, can be used instead of MockK)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    // Dagger Hilt for testing if used in the project
    testImplementation(libs.androidx.hilt.lifecycle.viewmodel)
    testImplementation(libs.hilt.android.testing)
    kaptTest(libs.hilt.compiler)
}
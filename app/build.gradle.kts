plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
}


android {
    namespace = "com.example.classschedule"
    compileSdk = 34



    defaultConfig {
        applicationId = "com.example.classschedule"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        val drivingApiBaseUrl = project.findProperty("DRIVING_API_BASE_URL") ?: ""
        val walkingApiBaseUrl = project.findProperty("WALKING_API_BASE_URL") ?: ""
        val cyclingApiBaseUrl = project.findProperty("CYCLING_API_BASE_URL") ?: ""
        val mapApiBaseUrl = project.findProperty("MAP_API_BASE_URL") ?: ""
        buildConfigField ("String", "DRIVING_API_BASE_URL", "\"${drivingApiBaseUrl}\"")
        buildConfigField ("String", "WALKING_API_BASE_URL", "\"${walkingApiBaseUrl}\"")
        buildConfigField ("String", "CYCLING_API_BASE_URL", "\"${cyclingApiBaseUrl}\"")
        buildConfigField ("String", "MAP_API_BASE_URL", "\"${mapApiBaseUrl}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true


        }
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
    }
    buildFeatures {
        compose = true
        buildConfig = true
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
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation("com.google.android.gms:play-services-location:18.1.0")
    implementation(libs.places)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.material)
    implementation(libs.ui)
    implementation(libs.accompanist.permissions)
    implementation(libs.protolite.well.known.types)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //MapLibre

    implementation(libs.android.plugin.annotation.v9)
    implementation(libs.android.sdk.v1101)

    //Retrofit
    implementation(libs.retrofit2.retrofit)
    implementation(libs.converter.gson)
    implementation ("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    //Room
    implementation(libs.androidx.room.runtime)
    ksp("androidx.room:room-compiler:${rootProject.extra["room_version"]}")
    implementation(libs.room.ktx)


    //Color Picker
    implementation("com.github.skydoves:colorpicker-compose:1.1.2")
}
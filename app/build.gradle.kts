plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
}

android {
    namespace = "com.example.pokeshop"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.pokeshop"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.animation.core.lint)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Permite crear formas circulares y definir colores
    implementation("androidx.compose.ui:ui-graphics")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Material Icons
    implementation("androidx.compose.material:material-icons-core:1.6.0")
    implementation("androidx.compose.material:material-icons-extended:1.6.0")

    //Coil para imagenes
    implementation("io.coil-kt:coil-compose:2.6.0")
    // Implementacion para abrir la galeria del telefono
    implementation("androidx.activity:activity-compose:1.9.0")

    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.2")

    // ==== AGREGADOS PARA REST ====

// Retrofit base

    implementation("com.squareup.retrofit2:retrofit:2.11.0") // <-- NUEVO

// Convertidor JSON con Gson

    implementation("com.squareup.retrofit2:converter-gson:2.11.0") // <-- NUEVO

// OkHttp y logging interceptor

    implementation("com.squareup.okhttp3:okhttp:4.12.0") // <-- NUEVO

    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // <-- NUEVO



//librerias de Test Locales

    testImplementation(libs.junit) //libreria junit

    testImplementation("io.mockk:mockk:1.13.12") //Mock para kotlin

    testImplementation("org.robolectric:robolectric:4.13") //simular pruebas en Android test locales

//test implementacion UI

    androidTestImplementation(libs.androidx.junit)

    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    androidTestImplementation(libs.androidx.compose.ui.test.manifest)

//librerias para el manejo de reglas de test

    androidTestImplementation("androidx.test:core-ktx:1.5.0")

    androidTestImplementation("androidx.test:rules:1.5.0")

//libreria splash screen

    implementation("androidx.core:core-splashscreen:1.0.1")
}
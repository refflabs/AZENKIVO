plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.azenkivo"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.azenkivo"
        minSdk = 29
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation("androidx.cardview:cardview:1.0.0")
    // Untuk lifecycleScope + delay() di SplashActivity (Modul P6 Tahap 3)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // Glide: memuat gambar dari URL ke ImageView (Modul P7 langkah 4.1)
    implementation(libs.glide)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}

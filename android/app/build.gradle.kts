plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.opennest.horizon"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.opennest.horizon"
        minSdk = 26
        targetSdk = 34
        versionCode = 10
        versionName = "1.5.0"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // ComponentActivity + OnBackPressedDispatcher for in-WebView back navigation.
    implementation("androidx.activity:activity-ktx:1.9.3")
}

plugins {
    alias(libs.plugins.android.application)

    // Google Services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.communitymedicineinventory"

    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.communitymedicineinventory"

        minSdk = 24
        targetSdk = 37

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    // AndroidX
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)

    // Material
    implementation(libs.material)

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // --------------------------------------------------
    // Firebase
    // --------------------------------------------------

    // Firebase Bill of Materials
    implementation(platform("com.google.firebase:firebase-bom:35.2.0"))

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    // Firebase Realtime Database
    implementation("com.google.firebase:firebase-database")

    // Optional: Firebase Analytics
    implementation("com.google.firebase:firebase-analytics")

    // Testing
    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
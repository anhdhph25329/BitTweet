plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Cài đặt Google Services
}

android {
    namespace = "fpt.anhdhph.bittweet"
    compileSdk = 35

    defaultConfig {
        applicationId = "fpt.anhdhph.bittweet"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Các thư viện cơ bản
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firebase BOM để đồng bộ các phiên bản Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.11.0"))

    // Các thư viện Firebase
    implementation("com.google.firebase:firebase-analytics") // Firebase Analytics
    implementation("com.google.firebase:firebase-firestore") // Firebase Firestore

    // Các thư viện khác
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.google.zxing:core:3.5.1")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
}

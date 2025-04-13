plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
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
        sourceCompatibility = JavaVersion.VERSION_17 // Hoặc VERSION_11 nếu cần
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation ("androidx.appcompat:appcompat:1.6.1")
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.11.0"))

    // Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth") // Cho chức năng xác thực
    implementation("com.google.firebase:firebase-storage") // Nếu cần lưu ảnh

    // Thư viện load ảnh (chọn 1 trong 2)
    implementation("com.squareup.picasso:picasso:2.8") // Đã có
    // Hoặc Glide (nếu muốn dùng Glide thay cho Picasso)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // AndroidX và Material Design
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // QR Code
    implementation("com.google.zxing:core:3.5.1")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    // ViewModel và LiveData (nếu cần)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    // Coroutines (cho xử lý bất đồng bộ)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
}

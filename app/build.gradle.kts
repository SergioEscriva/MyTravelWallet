plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "me.spenades.mytravelwallet"
    compileSdk = 34

    defaultConfig {
        applicationId = "me.spenades.mytravelwallet"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = rootProject.extra["defaultVersionName"] as String

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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.core.splashscreen)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
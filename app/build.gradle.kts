import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.navigation.safeargs)
}

android {
    namespace = "com.oudombun.chelfy"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.oudombun.chelfy"
        minSdk = 23
        targetSdk = 36
        versionCode = 3
        versionName = "1.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", "\"https://api.spoonacular.com\"")
        buildConfigField("String", "BASE_IMAGE_URL", "\"https://img.spoonacular.com/ingredients_100x100\"")
        buildConfigField("String", "API_KEY", "\"ff929de8ed504e9e8310a345fe2f2212\"")
        buildConfigField("String", "API_KEY2", "\"7a0a6c8528ba49418e6541d3a33c3584\"")
        buildConfigField("String", "API_KEY3", "\"32b9495b24d24ba095ebe8a1ea1b2e43\"")
    }

    val storePasswordValue = providers.gradleProperty("KEYSTORE_PASSWORD").orNull
    val keyPasswordValue = providers.gradleProperty("KEY_PASSWORD").orNull
    signingConfigs {
        if (storePasswordValue != null && keyPasswordValue != null) {
            getByName("debug") {
                storeFile = file("debug.jks")
                storePassword = storePasswordValue
                keyAlias = "debug"
                keyPassword = keyPasswordValue
            }
            create("release") {
                storeFile = file("release.jks")
                storePassword = storePasswordValue
                keyAlias = "release"
                keyPassword = keyPasswordValue
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.findByName("release")
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.material)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.coroutines.android)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.coil)
    implementation(libs.gson)
    implementation(libs.jsoup)
    implementation(libs.circleimageview)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso)
}

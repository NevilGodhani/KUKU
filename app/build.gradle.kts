
plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.kuku"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.kuku"
        minSdk = 28
        targetSdk = 34
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

    buildFeatures {
        viewBinding
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
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.car.ui.lib)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.viewpager2)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.database.v2030)
    implementation(libs.firebase.core)
    implementation (libs.firebase.database.v2000)
    implementation (libs.firebase.storage)
    implementation (libs.picasso)
    implementation(platform(libs.firebase.bom.v3320))
    implementation(libs.google.firebase.analytics)
    implementation (libs.firebase.database.v2021)
    implementation (libs.firebase.core.v2101)
    implementation (libs.picasso.v28)
    implementation (libs.firebase.auth.v2210)
    implementation (libs.play.services.auth)
}


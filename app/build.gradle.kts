plugins {
    alias(libs.plugins.b256.android.application)
    alias(libs.plugins.b256.android.compose)
    alias(libs.plugins.b256.flavors)
    alias(libs.plugins.b256.hilt)
    alias(libs.plugins.b256.android.test)
}

android {
    namespace = "br.com.b256.gnss"

    defaultConfig {
        applicationId = "br.com.b256.utm"
        versionCode = 47
        versionName = "1.0.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.material3)
    implementation(projects.presentation)
    implementation(projects.domain)
    implementation(projects.data)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.kotlinx.coroutines.core)
}

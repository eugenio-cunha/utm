plugins {
    alias(libs.plugins.b256.presentation)
    alias(libs.plugins.b256.android.compose)
    alias(libs.plugins.b256.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.b256.android.test)
}

android {
    namespace = "br.com.b256.presentation"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(projects.domain)
}

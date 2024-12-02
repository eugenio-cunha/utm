plugins {
    alias(libs.plugins.b256.android.library)
    alias(libs.plugins.b256.hilt)

    kotlin("plugin.serialization")
}

android {
    namespace = "br.com.b256.core.common"
}

dependencies {
    api(libs.kotlinx.datetime)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.serialization.json)

    implementation(libs.androidx.localbroadcastmanager)

    testImplementation(libs.turbine)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.compose.ui.test)
    testImplementation(libs.androidx.compose.ui.testManifest)
    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
}

plugins {
    alias(libs.plugins.b256.android.feature)
    alias(libs.plugins.b256.android.library.compose)
}

android {
    namespace = "br.com.b256.feature.settings"
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(projects.core.domain)
    implementation(projects.core.model)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
}

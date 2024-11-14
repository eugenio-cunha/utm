plugins {
    alias(libs.plugins.b256.android.library)
    alias(libs.plugins.b256.android.library.compose)
}

android {
    namespace = "br.com.b256.core.ui"
}

dependencies {
    api(libs.kotlinx.datetime)
    implementation(projects.core.designsystem)

    implementation(libs.androidx.camera)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.extension)
    implementation(libs.androidx.browser)
    implementation(libs.accompanist.permissions)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
}

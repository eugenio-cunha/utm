plugins {
    alias(libs.plugins.b256.android.library)
    alias(libs.plugins.b256.android.room)
    alias(libs.plugins.b256.hilt)
}

android {
    namespace = "br.com.b256.core.database"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.common)

    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}

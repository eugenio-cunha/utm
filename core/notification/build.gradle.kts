plugins {
    alias(libs.plugins.b256.android.library)
    alias(libs.plugins.b256.hilt)
}

android {
    namespace = "br.com.b256.core.notification"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.common)

    compileOnly(platform(libs.androidx.compose.bom))
}

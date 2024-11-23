plugins {
    alias(libs.plugins.b256.android.library)
    alias(libs.plugins.b256.hilt)
}

android {
    namespace = "br.com.b256.core.gps"
}

dependencies {
    implementation(projects.core.common)
    implementation(libs.androidx.localbroadcastmanager)
    implementation(libs.geo.coords)
}

plugins {
    alias(libs.plugins.b256.android.library)

    kotlin("plugin.serialization")
}

android {
    namespace = "br.com.core.b256.model"
}

dependencies {
    implementation(projects.core.common)
}

plugins {
    alias(libs.plugins.b256.android.library)
    alias(libs.plugins.b256.hilt)
}

android {
    namespace = "br.com.b256.core.datastore"
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(libs.androidx.dataStore)

    testImplementation(libs.kotlinx.coroutines.test)
}

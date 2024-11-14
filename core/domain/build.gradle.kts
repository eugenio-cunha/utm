plugins {
    alias(libs.plugins.b256.android.library)
    id("com.google.devtools.ksp")
}

android {
    namespace = "br.com.core.b256.domain"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.data)
    implementation(projects.core.model)

    implementation(libs.javax.inject)
    testImplementation(libs.kotlinx.coroutines.test)
}

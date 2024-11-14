plugins {
    alias(libs.plugins.b256.jvm.library)
}

dependencies {
    implementation(libs.kotlinx.datetime)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
}

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "br.com.b256.gnss.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.ksp.gradle.plugin)
    compileOnly(libs.room.gradle.plugin)
    compileOnly(libs.spotless.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = libs.plugins.b256.android.application.get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }

        register("androidLibrary") {
            id = libs.plugins.b256.android.library.get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }

        register("jvmLibrary") {
            id = libs.plugins.b256.jvm.library.get().pluginId
            implementationClass = "JvmLibraryConventionPlugin"
        }

        register("androidCompose") {
            id = libs.plugins.b256.android.compose.get().pluginId
            implementationClass = "AndroidComposeConventionPlugin"
        }

        register("androidRoom") {
            id = libs.plugins.b256.android.room.get().pluginId
            implementationClass = "AndroidRoomConventionPlugin"
        }

        register("androidHilt") {
            id = libs.plugins.b256.hilt.get().pluginId
            implementationClass = "HiltConventionPlugin"
        }

        register("androidPresentation") {
            id = libs.plugins.b256.presentation.get().pluginId
            implementationClass = "AndroidPresentationConventionPlugin"
        }

        register("androidFlavors") {
            id = libs.plugins.b256.flavors.get().pluginId
            implementationClass = "AndroidApplicationFlavorsConventionPlugin"
        }

        register("root") {
            id = libs.plugins.b256.root.get().pluginId
            implementationClass = "RootPlugin"
        }

        register("androidTest") {
            id = libs.plugins.b256.android.test.get().pluginId
            implementationClass = "AndroidTestConventionPlugin"
        }
    }
}

package br.com.b256.gnss.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * Acesso ao catálogo de versões `libs` (`gradle/libs.versions.toml`) a partir do código dos
 * convention plugins (`build-logic/convention`), onde o acessor type-safe gerado pelo Gradle
 * (`libs.androidx.xxx`) não está disponível.
 *
 * NOTE: kept in a named package on purpose. A top-level `Project.libs` extension
 * declared in the default package would leak into consuming modules' build
 * scripts (their compiled classes land on those scripts' compile classpath),
 * shadowing Gradle's own generated type-safe `libs` version-catalog accessor
 * there and breaking `libs.androidx.xxx` references in e.g. app/build.gradle.kts.
 */
val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

/**
 * Configuration shared by every Android module (app and library): compile/min SDK
 * and Java source/target compatibility. Kotlin compilation itself is handled by
 * AGP's built-in Kotlin support (no `org.jetbrains.kotlin.android` plugin is
 * applied), which derives its JVM target from [CommonExtension.getCompileOptions].
 */
/**
 * `compileSdk`/`minSdk` e compatibilidade Java, únicos ajustes que fazem sentido em qualquer
 * módulo Android (aplicação ou biblioteca) — chamado por [AndroidApplicationConventionPlugin] e
 * [AndroidLibraryConventionPlugin]. Para elevar o `minSdk`/`compileSdk` do projeto todo, altere
 * apenas aqui.
 */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension
) {
    commonExtension.apply {
        compileSdk = 37

        defaultConfig.minSdk = 29

        compileOptions.sourceCompatibility = JavaVersion.VERSION_11
        compileOptions.targetCompatibility = JavaVersion.VERSION_11
    }
}

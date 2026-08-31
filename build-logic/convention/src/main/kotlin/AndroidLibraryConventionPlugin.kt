import br.com.b256.gnss.buildlogic.configureKotlinAndroid
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

/**
 * Plugin de convenção para módulos `com.android.library` (id `b256.android.library`).
 *
 * Usado diretamente por `:data` e indiretamente por `:presentation`, que o aplica por baixo dos
 * panos em [AndroidPresentationConventionPlugin]. É a base para qualquer novo módulo Android que
 * não seja o `:app` (ex.: um futuro módulo `:core:ui` ou uma nova feature).
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                // Library modules have no targetSdk; only minSdk/compileSdk apply.

                // Prefixo de recursos derivado do path do módulo (ex.: ":data" -> "data_"), para
                // evitar colisão de nomes de recursos (strings, drawables, etc.) entre módulos.
                resourcePrefix =
                    path.split("""\W""".toRegex()).drop(1).distinct().joinToString(separator = "_")
                        .lowercase() + "_"
            }
        }
    }
}

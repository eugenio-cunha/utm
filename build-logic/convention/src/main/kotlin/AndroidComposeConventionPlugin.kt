import br.com.b256.gnss.buildlogic.libs
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Plugin de convenção para habilitar Jetpack Compose num módulo Android (id `b256.android.compose`).
 *
 * Aplica o plugin do compilador Compose e adiciona o BOM (`androidx-compose-bom`) como
 * `platform(...)`, para que os demais módulos com Compose só precisem declarar as libs de UI
 * (ex.: `androidx-compose-material3`) sem versão — ela vem do BOM. Aplicado em `:app` diretamente
 * e em `:presentation` via [AndroidPresentationConventionPlugin].
 */
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            extensions.configure<CommonExtension> {
                buildFeatures.compose = true
            }

            dependencies {
                val bom = libs.findLibrary("androidx-compose-bom").get()
                add("implementation", platform(bom))
                add("androidTestImplementation", platform(bom))
            }
        }
    }
}

import br.com.b256.gnss.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 * Plugin de convenção para o módulo de UI/apresentação (id `b256.presentation`, aplicado hoje só
 * em `presentation/build.gradle.kts`).
 *
 * Combina `b256.android.library` + `b256.hilt` e adiciona as dependências comuns a telas Compose
 * com `ViewModel` (lifecycle-compose, Hilt para `ViewModel`, Navigation3, Material3 icons
 * estendidos). Um novo módulo de feature com telas Compose deveria aplicar este plugin em vez de
 * remontar essas dependências na mão.
 */
class AndroidPresentationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "b256.android.library")
            apply(plugin = "b256.hilt")

            dependencies {
                "implementation"(libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
                "implementation"(libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
                "implementation"(libs.findLibrary("androidx.hilt.lifecycle.viewModelCompose").get())
                "implementation"(libs.findLibrary("androidx.material.icons.extended").get())
                "implementation"(libs.findLibrary("androidx.navigation3.runtime").get())
                "implementation"(libs.findLibrary("androidx.navigation3.ui").get())
                "implementation"(libs.findLibrary("androidx.compose.bom").get())
                "implementation"(libs.findLibrary("androidx.compose.material3").get())
                "implementation"(libs.findLibrary("androidx.tracing.ktx").get())
            }
        }
    }
}

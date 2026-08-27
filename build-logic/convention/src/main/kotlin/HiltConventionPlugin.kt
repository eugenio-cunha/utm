import br.com.b256.gnss.buildlogic.libs
import com.android.build.gradle.api.AndroidBasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 * Plugin de convenção para habilitar injeção de dependência com Hilt (id `b256.hilt`).
 *
 * Único plugin de convenção aplicável tanto a módulos Android quanto a módulos Kotlin/JVM puros
 * (ex.: `:domain`, para permitir `@Inject` em `usecases` sem depender do framework Android): ele
 * detecta o tipo de módulo em que está sendo aplicado, via [org.gradle.api.plugins.PluginManager.withPlugin],
 * e adiciona só as dependências pertinentes:
 * - Kotlin/JVM (`org.jetbrains.kotlin.jvm`) → `hilt-core` (Dagger puro, sem Android).
 * - Android ([AndroidBasePlugin]) → plugin `dagger.hilt.android.plugin` + `hilt-android`.
 *
 * O processador de anotações (`hilt-compiler`) via KSP é adicionado em ambos os casos.
 */
class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.devtools.ksp")

            dependencies {
                "ksp"(libs.findLibrary("hilt.compiler").get())
                "ksp"(libs.findLibrary("kotlin.metadata.jvm").get())
            }

            // Add support for Jvm Module, base on org.jetbrains.kotlin.jvm
            pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
                dependencies {
                    "implementation"(libs.findLibrary("hilt.core").get())
                }
            }

            /** Add support for Android modules, based on [AndroidBasePlugin] */
            pluginManager.withPlugin("com.android.base") {
                apply(plugin = "dagger.hilt.android.plugin")
                dependencies {
                    "implementation"(libs.findLibrary("hilt.android").get())
                }
            }
        }
    }
}

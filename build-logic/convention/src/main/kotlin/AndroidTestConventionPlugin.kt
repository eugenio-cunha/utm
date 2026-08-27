import br.com.b256.gnss.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Plugin de convenção que centraliza as dependências de teste do template (id `b256.android.test`).
 *
 * No NIA original, `AndroidTestConventionPlugin` aplica o plugin `com.android.test` da AGP, usado
 * apenas por um módulo dedicado e sem código próprio (ex.: um módulo de macrobenchmark). O gnss
 * não tem hoje esse tipo de módulo, então este plugin foi adaptado para um propósito mais útil ao
 * template: reunir num único lugar as dependências de teste (`testImplementation`/
 * `androidTestImplementation`) compartilhadas pelos módulos, em vez de repeti-las em cada
 * `build.gradle.kts`.
 *
 * Aplicável tanto a `:domain` (Kotlin/JVM puro) quanto aos módulos Android (`:data`,
 * `:presentation`, `:app`): a exemplo de [HiltConventionPlugin], usa
 * `pluginManager.withPlugin(...)` para adicionar cada grupo de dependência apenas quando o plugin
 * correspondente também estiver presente no módulo — assim um módulo JVM nunca recebe uma
 * dependência que exige o Android Gradle Plugin (ex.: `androidTestImplementation`, que só existe
 * em módulos Android).
 */
class AndroidTestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Comum a qualquer módulo Kotlin (JVM ou Android): JUnit4, coroutines-test (para
            // testar Flow/StateFlow), MockK (mocking) e Turbine (assertions em Flow).
            dependencies {
                "testImplementation"(libs.findLibrary("junit").get())
                "testImplementation"(libs.findLibrary("kotlin.test").get())
                "testImplementation"(libs.findLibrary("kotlinx.coroutines.test").get())
                "testImplementation"(libs.findLibrary("mockk").get())
                "testImplementation"(libs.findLibrary("turbine").get())
            }

            // Módulos Android: JUnit4 instrumentado, Espresso, MockK para instrumentação, e
            // Robolectric (+ androidx-test-core, para ApplicationProvider) para rodar testes que
            // dependem de Context/Android sem emulador.
            pluginManager.withPlugin("com.android.base") {
                dependencies {
                    "testImplementation"(libs.findLibrary("robolectric").get())
                    "testImplementation"(libs.findLibrary("androidx.test.core").get())
                    "androidTestImplementation"(libs.findLibrary("androidx.junit").get())
                    "androidTestImplementation"(libs.findLibrary("androidx.espresso.core").get())
                    "androidTestImplementation"(libs.findLibrary("mockk.android").get())
                    "androidTestImplementation"(libs.findLibrary("kotlinx.coroutines.test").get())
                }
            }

            // Módulos com Room (hoje só :data): banco Room em memória para testar DAOs.
            pluginManager.withPlugin("androidx.room") {
                dependencies {
                    "testImplementation"(libs.findLibrary("room.testing").get())
                    "androidTestImplementation"(libs.findLibrary("room.testing").get())
                }
            }

            // Módulos com Hilt-Android (:data, :presentation, :app): suporte a @HiltAndroidTest.
            pluginManager.withPlugin("dagger.hilt.android.plugin") {
                dependencies {
                    "androidTestImplementation"(libs.findLibrary("hilt.android.testing").get())
                }
            }

            // Módulos com Compose (:presentation, :app): ComposeTestRule para testes de UI.
            pluginManager.withPlugin("org.jetbrains.kotlin.plugin.compose") {
                dependencies {
                    val bom = libs.findLibrary("androidx.compose.bom").get()
                    "androidTestImplementation"(platform(bom))
                    "androidTestImplementation"(libs.findLibrary("androidx.compose.ui.test.junit4").get())
                    "debugImplementation"(libs.findLibrary("androidx.compose.ui.test.manifest").get())
                }
            }
        }
    }
}

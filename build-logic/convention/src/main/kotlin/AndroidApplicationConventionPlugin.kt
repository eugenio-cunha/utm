import br.com.b256.gnss.buildlogic.configureKotlinAndroid
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

/**
 * Plugin de convenção para o único módulo `com.android.application` do projeto (id
 * `b256.android.application`, aplicado em `app/build.gradle.kts`).
 *
 * Aplica o plugin do Android Gradle Plugin (AGP) e a configuração comum a todo módulo Android
 * (ver [configureKotlinAndroid]), além de fixar o `targetSdk`, que só faz sentido em módulos de
 * aplicação (módulos de biblioteca não declaram `targetSdk`).
 */
abstract class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 37
            }
        }
    }
}

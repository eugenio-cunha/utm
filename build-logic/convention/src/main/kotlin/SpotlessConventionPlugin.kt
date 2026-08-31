import br.com.b256.gnss.buildlogic.libs
import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Plugin de convenção que aplica formatação/lint automática de Kotlin via Spotless + ktlint.
 *
 * Diferente dos demais convention plugins deste módulo, este **não tem id próprio** nem é
 * `alias`ado em nenhum `build.gradle.kts` de módulo — [RootPlugin] o aplica diretamente (por
 * classe) em cada subprojeto, dentro do mesmo `target.subprojects { ... }` que já registra as
 * tasks `graphDump`/`graphUpdate`. Isso mantém a configuração de lint centralizada num único
 * lugar, sem precisar tocar no `build.gradle.kts` de `:app`, `:data`, `:domain` ou `:presentation`
 * nem criar um módulo dedicado só para isso.
 *
 * As regras de estilo (o que o ktlint aceita ou não) não ficam aqui: o step `ktlint(...)` do
 * Spotless lê o `.editorconfig` do projeto automaticamente — é ele quem já documenta, com
 * comentários, cada regra habilitada/desabilitada e por quê.
 */
class SpotlessConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.diffplug.spotless")

            // `target` aqui é o subprojeto sendo configurado, mas o VersionCatalogsExtension
            // ainda não está disponível nele neste ponto do ciclo de vida do Gradle — o
            // RootPlugin aplica este plugin de dentro de um `subprojects { }`, que roda antes do
            // próprio build.gradle.kts do subprojeto ser avaliado (e é essa avaliação que traz o
            // catálogo para dentro do projeto). A raiz (`rootProject`), por outro lado, já teve
            // seu catálogo resolvido nesse momento — é de lá que lemos a versão do ktlint.
            val ktlintVersion = rootProject.libs.findVersion("ktlint").get().requiredVersion

            extensions.configure<SpotlessExtension> {
                kotlin {
                    target("src/**/*.kt")
                    ktlint(ktlintVersion)
                    trimTrailingWhitespace()
                    endWithNewline()
                }
                kotlinGradle {
                    target("*.kts")
                    ktlint(ktlintVersion)
                }
            }
        }
    }
}

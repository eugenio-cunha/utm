import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.configuration.BuildFeatures
import util.configureGraphTasks
import javax.inject.Inject

/**
 * Plugin de convenção aplicado apenas na raiz do projeto (id `b256.root`, ver
 * `build.gradle.kts` da raiz).
 *
 * Registra as tasks `graphDump`/`graphUpdate` (ver [util.configureGraphTasks]) e aplica
 * [SpotlessConventionPlugin] em cada subprojeto, para gerar/atualizar os grafos de dependência de
 * módulos em Mermaid e para formatar/verificar o estilo do código Kotlin. Não configura nada além
 * disso — não é o lugar para lógica de build compartilhada entre módulos de app/library (isso fica
 * nos demais convention plugins deste módulo).
 */
abstract class RootPlugin : Plugin<Project> {
    @get:Inject abstract val buildFeatures: BuildFeatures

    override fun apply(target: Project) {
        require(target.path == ":")
        if (!buildFeatures.isIsolatedProjectsEnabled()) {
            target.subprojects {
                configureGraphTasks()
                pluginManager.apply(SpotlessConventionPlugin::class.java)
            }
        }
    }
}

/**
 * Com Isolated Projects habilitado, cada subprojeto só pode acessar seu próprio estado durante a
 * configuração — `target.subprojects { ... }` a partir da raiz violaria esse isolamento. Nesse
 * caso, a geração dos grafos é desabilitada em vez de quebrar o build.
 */
private fun BuildFeatures.isIsolatedProjectsEnabled(): Boolean {
    return isolatedProjects.active.getOrElse(false)
}

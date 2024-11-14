package br.com.b256.extension

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import java.io.ByteArrayOutputStream

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

/**
 * Obtém o número unico de versão da aplicação para loja a partir do número de commits da main
 *
 * @param diff Número para corrigir a diferença entre o version code da loja e o número de commits da branch main
 *
 * @return Retorna um inteiro do número unico de versão do aplicativo
 * */
@Suppress("TooGenericExceptionCaught", "SwallowedException")
fun Project.getVersionCode(diff: Int): Int {
    return try {
        gitCommitCount(this).toInt() + diff
    } catch (_: Exception) {
        1
    }
}

/**
 * Obtém a versão do aplicativo no formato 1.0.0
 *
 * @param mayor Versão Principal, representa mudanças de grande impacto que podem quebrar a compatibilidade
 * @param minor Versão Secundária, indica mudanças que adicionam novas funcionalidades de forma compatível com as versões anteriores
 * @param patch Correção de Erros, correções pequenas, ajustes de bugs, melhorias de desempenho ou pequenas alterações
 *
 * @return Retorna uma string da versão do aplicativo
 * */
@Suppress("TooGenericExceptionCaught", "SwallowedException")
fun Project.getVersionName(mayor: Int, minor: Int, patch: Int): String = "$mayor.$minor.$patch"

private fun gitCommitCount(project: Project): String = try {
    val os = ByteArrayOutputStream()
    project.exec {
        commandLine = "git rev-list --first-parent --count main".split(" ")
        standardOutput = os
    }

    String(os.toByteArray()).trim()
} catch (_: Exception) {
    "0"
}


import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Plugin de convenção que registra os product flavors do `:app` (id `b256.flavors`).
 *
 * Lê as dimensões/flavors declarados em [FlavorDimension]/[B256Flavor] e os registra no bloco
 * `android { productFlavors { ... } }`. Para adicionar um novo flavor (ex.: um ambiente `staging`)
 * ou uma nova dimensão, basta adicionar uma entrada nos enums abaixo — não é necessário tocar na
 * lógica deste plugin.
 */
class AndroidApplicationFlavorsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.configure<ApplicationExtension> {

                FlavorDimension.entries.forEach { flavorDimension ->
                    flavorDimensions += flavorDimension.name
                }

                productFlavors {
                    B256Flavor.entries.forEach { flavor ->
                        register(flavor.name) {
                            dimension = flavor.dimension.name

                            if (this is ApplicationExtension) {
                                if (flavor.applicationIdSuffix != null) {
                                    applicationIdSuffix = flavor.applicationIdSuffix
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dimensões de flavor disponíveis. Hoje existe apenas [contentType], separando build de
 * desenvolvimento da de produção; uma nova dimensão (ex.: `region`) entraria aqui.
 */
@Suppress("EnumEntryName")
enum class FlavorDimension {
    contentType,
}

/**
 * Flavors do `:app`, um por [dimension]. [applicationIdSuffix], quando presente, é anexado ao
 * `applicationId` do flavor (ex.: `develop` gera `br.com.b256.gnss.develop`), permitindo
 * instalar as duas variantes lado a lado no mesmo dispositivo.
 */
@Suppress("EnumEntryName")
enum class B256Flavor(
    val dimension: FlavorDimension,
    val applicationIdSuffix: String? = null,
) {
    develop(dimension = FlavorDimension.contentType, applicationIdSuffix = ".develop"),
    production(dimension = FlavorDimension.contentType),
}

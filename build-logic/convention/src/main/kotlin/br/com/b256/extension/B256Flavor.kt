package br.com.b256.extension

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor
import org.gradle.kotlin.dsl.invoke

@Suppress("EnumEntryName")
enum class FlavorDimension {
    contentType
}

// O conteúdo do aplicativo pode vir de dados estáticos locais, que são úteis para fins de
// demonstração ou de um servidor de backend de produção que fornece conteúdo real e atualizado.
// Esses duas versões de produtos refletem esse comportamento.
@Suppress("EnumEntryName")
enum class B256Flavor(val dimension: FlavorDimension, val applicationIdSuffix: String? = null) {
    develop(FlavorDimension.contentType, applicationIdSuffix = ".develop"),
    production(FlavorDimension.contentType)
}

fun configureFlavors(
    commonExtension: CommonExtension,
    flavorConfigurationBlock: ProductFlavor.(flavor: B256Flavor) -> Unit = {},
) {
    commonExtension.apply {
        FlavorDimension.entries.forEach { flavorDimension ->
            flavorDimensions += flavorDimension.name
        }

        productFlavors {
            B256Flavor.entries.forEach { flavor ->

                register(flavor.name) {
                    dimension = flavor.dimension.name
                    flavorConfigurationBlock(this, flavor)
                    if (commonExtension is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (flavor.applicationIdSuffix != null) {
                            applicationIdSuffix = flavor.applicationIdSuffix
                        }
                    }
                }
            }
        }
    }
}

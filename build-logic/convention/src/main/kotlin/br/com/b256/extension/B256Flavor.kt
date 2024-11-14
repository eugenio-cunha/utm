package br.com.b256.extension

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor

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
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: B256Flavor) -> Unit = {}
) {
    commonExtension.apply {
        flavorDimensions += FlavorDimension.contentType.name
        productFlavors {
            B256Flavor.values().forEach {
                create(it.name) {
                    dimension = it.dimension.name
                    flavorConfigurationBlock(this, it)
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (it.applicationIdSuffix != null) {
                            applicationIdSuffix = it.applicationIdSuffix
                        }
                    }
                }
            }
        }
    }
}

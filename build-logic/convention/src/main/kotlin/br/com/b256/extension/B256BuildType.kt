package br.com.b256.extension

/**
 * This is shared between :app and :benchmarks module to provide configurations type safety.
 */
enum class B256BuildType(val applicationIdSuffix: String? = null) {
    DEBUG(".debug"),
    RELEASE,
}

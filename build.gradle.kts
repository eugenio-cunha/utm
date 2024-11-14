/*
* Ao listar todos os plugins usados em todos os subprojetos no script de construção do projeto raiz, ele
* garante que o classpath do script de construção permaneça o mesmo para todos os projetos. Isso evita potenciais
* problemas com versões incompatíveis de dependências transitivas de plugins. Um subprojeto que aplica
* um plugin não listado terá esse plugin e suas dependências _anexadas_ ao classpath, não
* substituindo dependências pré-existentes.
*/
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.baselineprofile) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.secrets) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.module.graph) apply true
    alias(libs.plugins.kotlin.android) apply false // Plugin applied to allow module graph generation
}

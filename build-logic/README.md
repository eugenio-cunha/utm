# Plugins de Convenção

A pasta `build-logic` define plugins de convenção específicos do projeto, usados para manter uma única
fonte de verdade para configurações comuns de módulos.

Esta abordagem é fortemente baseada em
[https://developer.squareup.com/blog/herding-elephants/](https://developer.squareup.com/blog/herding-elephants/)
e
[https://github.com/jjohannes/idiomatic-gradle](https://github.com/jjohannes/idiomatic-gradle).

Ao configurar plugins de convenção em `build-logic`, podemos evitar configuração duplicada de script de compilação,
configurações confusas de `subprojeto`, sem as armadilhas do diretório `buildSrc`.

`build-logic` é uma compilação incluída, conforme configurada na raiz
[`settings.gradle.kts`](../settings.gradle.kts).

Dentro de `build-logic` há um módulo `convention`, que define um conjunto de plugins que todos os módulos normais
podem usar para se configurar.

`build-logic` também inclui um conjunto de arquivos `Kotlin` usados para compartilhar lógica entre os próprios plugins,
o que é mais útil para configurar componentes Android (bibliotecas vs aplicativos) com código
compartilhado.

Esses plugins são *aditivos* e *componíveis*, e tentam realizar apenas uma única responsabilidade.
Os módulos podem então escolher as configurações de que precisam.
Se houver lógica única para um módulo sem código compartilhado, é preferível defini-la diretamente
no `build.gradle` do módulo, em vez de criar um plugin de convenção com configuração
específica do módulo.

Lista atual de plugins de convenção:

- [`b256.android.application`](convention/src/main/kotlin/AndroidApplicationConventionPlugin.kt),
  [`b256.android.library`](convention/src/main/kotlin/AndroidLibraryConventionPlugin.kt),
  [`b256.android.test`](convention/src/main/kotlin/AndroidTestConventionPlugin.kt):
  Configura opções comuns do Android e do Kotlin.
- [`b256.android.application.compose`](convention/src/main/kotlin/AndroidApplicationComposeConventionPlugin.kt),
  [`b256.android.library.compose`](convention/src/main/kotlin/AndroidLibraryComposeConventionPlugin.kt):
  Configura as opções do Jetpack Compose

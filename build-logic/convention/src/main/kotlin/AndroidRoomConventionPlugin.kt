import br.com.b256.gnss.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import androidx.room.gradle.RoomExtension
import com.google.devtools.ksp.gradle.KspExtension

/**
 * Plugin de convenção para habilitar Room num módulo Android (id `b256.android.room`, aplicado
 * hoje só em `data/build.gradle.kts`).
 *
 * Configura o plugin oficial `androidx.room` + KSP (gerando o compilador em Kotlin, não Java) e
 * define o diretório de schemas (`<módulo>/schemas`) necessário para migrações automáticas do
 * Room. Se um novo módulo precisar de um banco Room próprio, basta aplicar `b256.android.room`
 * nele.
 */
class AndroidRoomConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "androidx.room")
            apply(plugin = "com.google.devtools.ksp")

            extensions.configure<KspExtension> {
                arg("room.generateKotlin", "true")
            }

            extensions.configure<RoomExtension> {
                // The schemas directory contains a schema file for each version of the Room database.
                // This is required to enable Room auto migrations.
                // See https://developer.android.com/reference/kotlin/androidx/room/AutoMigration.
                schemaDirectory("$projectDir/schemas")
            }

            dependencies {
                "implementation"(libs.findLibrary("room.runtime").get())
                "implementation"(libs.findLibrary("room.ktx").get())
                "ksp"(libs.findLibrary("room.compiler").get())
            }
        }
    }
}

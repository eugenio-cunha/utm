package br.com.b256.core.designsystem.extension

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Verifica se uma permissão específica foi concedida ao aplicativo.
 *
 * @param permission A permissão a ser verificada (exemplo: `Manifest.permission.CAMERA`).
 * @return `true` se a permissão foi concedida, `false` caso contrário.
 *
 * Exemplo de uso:
 * ```
 * if (context.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
 *     // Permissão concedida, pode prosseguir
 * } else {
 *     // Permissão negada, solicitar ao usuário
 * }
 * ```
 */
fun Context.checkPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED
}

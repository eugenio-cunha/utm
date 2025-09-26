package br.com.b256.core.ui.component

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import br.com.b256.core.designsystem.extension.checkPermission
import br.com.b256.core.ui.R
import java.io.File

/**
 * Classe que estende `FileProvider` para fornecer URIs para arquivos de imagem.
 *
 * Esta classe é usada para gerar URIs compartilháveis para arquivos de imagem armazenados
 * no diretório interno do aplicativo. Ela utiliza o `Context` do aplicativo e o nome do
 * arquivo para criar um URI único.
 */
private class AttachmentProvider : FileProvider(R.xml.file_paths) {
    companion object {
        /**
         *
         * Cria uma nova URI para um arquivo de imagem com um prefixo específico.
         *
         * Esta função cria um arquivo temporário no diretório de imagens do aplicativo e retorna
         * uma URI compartilhável para esse arquivo. O prefixo é usado para nomear o arquivo.
         *
         * @param context O contexto do aplicativo.
         * @param prefix O prefixo a ser usado no nome do arquivo.
         * @return Uma [Uri] para o arquivo de imagem criado.
         */
        fun newUri(context: Context, prefix: String): Uri {
            val directory = File(context.filesDir, "images").apply { mkdirs() }

            // Retorna um URI de conteúdo para um arquivo fornecido.
            return getUriForFile(
                context,
                "${context.packageName}.fileProvider",
                File.createTempFile("${prefix}_", ".jpg", directory),
            )
        }
    }
}

/**
 *
 * Interface que define um lançador genérico para iniciar uma operação.
 *
 * Esta interface é usada para encapsular a lógica de iniciar uma ação, como capturar uma imagem.
 *
 * @see rememberTakePictureFlow
 */
interface Launcher {

    /**
     * Inicia a operação associada a este lançador.
     *
     */
    fun launch()
}

/**
 * `rememberTakePictureFlow` é uma função composable que facilita a integração da câmera do dispositivo
 * e a captura da localização em um aplicativo Android usando o Jetpack Compose. Ela gerencia o processo
 * de solicitação de permissões da câmera e localização, e o lançamento da câmera para capturar uma imagem,
 * além de obter a última localização conhecida do dispositivo.
 *
 * @param context O contexto da aplicação. Por padrão, usa o contexto atual fornecido por `LocalContext.current`.
 * @param prefix O prefixo a ser usado no nome do arquivo da imagem. Por padrão, é "image".
 * @param onSuccess Uma função lambda que é chamada quando uma imagem é capturada com sucesso.
 *                      Ela recebe o URI da imagem capturada, a latitude e a longitude como parâmetros.
 * @param onFailure Uma função lambda que é chamada quando uma imagem não é capturada (Opcional).
 *                      Ela recebe um [Throwable] como parâmetro, indicando a causa da falha.
 * @param onPermissionDenied Uma função lambda que é chamada quando as permissões da câmera ou localização são negadas pelo usuário.
 *
 *  @return Um objeto Launcher que pode ser usado para iniciar o fluxo de captura de imagem e localização.
 */
@Composable
fun rememberTakePictureFlow(
    context: Context = LocalContext.current,
    prefix: String = "image",
    onSuccess: (Uri) -> Unit,
    onFailure: (String) -> Unit = {},
    onPermissionDenied: () -> Unit,
): Launcher {
    var pictureUri by remember { mutableStateOf<Uri?>(null) }

    // Iniciador que pode ser usado para iniciar a atividade da câmera.
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && pictureUri != null) {
                onSuccess(pictureUri!!)
            } else {
                onFailure("Ocorreu uma falha não esperada ao capturar a imagem.")
            }
        },
    )

    // Iniciador que pode ser usado para iniciar a atividade das permissões.
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                runCatching {
                    AttachmentProvider.newUri(context = context, prefix = prefix).also { uri ->
                        pictureUri = uri
                        takePictureLauncher.launch(uri)
                    }
                }.getOrElse { error ->
                    onFailure(error.message.orEmpty())
                }
            } else {
                onPermissionDenied()
            }
        },
    )

    /**
     *
     * Solicita permissões de câmera e localização, se necessário, ou inicia a captura da imagem.
     *
     * Esta função verifica se as permissões de câmera e localização foram concedidas.
     * Se a permissão da câmera não foi concedida, ela solicita a permissão da câmera.
     * Se a permissão de localização não foi concedida (e a da câmera sim), ela solicita a permissão de localização.
     * Se ambas as permissões foram concedidas, ela tenta criar uma nova URI para a imagem.
     * Em caso de sucesso na criação da URI, a captura da imagem é iniciada.
     * Em caso de falha na criação da URI, a função `onCaptureFailure` é chamada com o erro correspondente.
     *
     * @param prefix O prefixo a ser usado no nome do arquivo da imagem.
     */
    fun requestOrLaunch(prefix: String) {
        when {
            // Verifica a permissão de uso da câmera
            !context.checkPermission(Manifest.permission.CAMERA) -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }

            // Cria uma nova URI e lança a activity da câmera
            else -> runCatching {
                AttachmentProvider.newUri(context = context, prefix = prefix).also { uri ->
                    pictureUri = uri
                    takePictureLauncher.launch(uri)
                }
            }.getOrElse { error ->
                onFailure(error.message.orEmpty())
            }
        }
    }

    return object : Launcher {
        /**
         *
         * Inicia o fluxo de captura de imagem e localização.
         *
         * Esta função verifica as permissões de câmera e localização. Se ambas as permissões
         * forem concedidas, ela cria uma nova URI para a imagem e inicia a atividade da câmera.
         * Se alguma permissão não for concedida, ela solicita a permissão correspondente.
         */
        override fun launch() = requestOrLaunch(prefix = prefix)
    }
}

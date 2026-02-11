package br.com.b256.feature.utm

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.b256.core.designsystem.icon.B256Icons
import br.com.b256.core.designsystem.theme.PaddingHalf
import br.com.b256.core.gps.extension.UTM
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * Composable que representa a tela principal para exibição de coordenadas UTM.
 *
 * Esta tela é responsável por observar o estado da interface vindo do [UtmViewModel],
 * lidar com efeitos colaterais como o compartilhamento de coordenadas e gerenciar
 * solicitações de permissão de localização. Utiliza o [LocationPermissionEffect] para
 * garantir que as permissões necessárias sejam concedidas antes de iniciar o serviço
 * de localização. A interface real é delegada para uma função `UtmScreen` privada.
 *
 * @param modifier O modificador a ser aplicado à tela.
 * @param viewModel O [UtmViewModel] responsável por gerenciar o estado e a lógica da tela.
 *                  Injetado automaticamente via Hilt.
 */
@Composable
internal fun UtmScreen(
    modifier: Modifier = Modifier,
    viewModel: UtmViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is UtmUiEffect.Share -> share(context = context, effect.value)
            }
        }
    }

    LocationPermissionEffect {
        viewModel.dispatch(intent = UtmUiIntent.StartService)
    }

    UtmScreen(
        modifier = modifier,
        uiState = uiState,
        dispatch = viewModel::dispatch,
    )
}

/**
 * Ponto de entrada da tela UTM. Esta função gerencia o estado da interface, lida com efeitos colaterais
 * de compartilhamento, solicita permissões de localização e inicia o serviço de rastreamento.
 *
 * @param modifier [Modifier] a ser aplicado ao layout da tela.
 * @param viewModel O [UtmViewModel] que fornece o estado e gerencia a lógica de negócio.
 */
@Composable
private fun UtmScreen(
    modifier: Modifier = Modifier,
    uiState: UtmUiState,
    dispatch: (intent: UtmUiIntent) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    uiState.location?.UTM?.also {
                        dispatch(UtmUiIntent.Share(value = "${it.zone} ${it.easting} ${it.northing}"))
                    }
                },
            ) {
                Icon(
                    imageVector = B256Icons.Share,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                uiState.location?.UTM?.let {
                    Text(

                        text = it.zone,
                        style = MaterialTheme.typography.displayMedium,
                    )
                    Text(
                        text = it.easting,
                        style = MaterialTheme.typography.displayMedium,
                    )
                    Text(
                        text = it.northing,
                        style = MaterialTheme.typography.displayMedium,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                uiState.location?.let {
                    Column(
                        modifier = Modifier.padding(PaddingHalf),
                    ) {
                        Text(
                            text = stringResource(R.string.feature_utm_altitude),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.End,
                        )
                        Text(
                            text = stringResource(R.string.feature_utm_meters, it.altitude.toInt()),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.End,
                        )
                    }

                    VerticalDivider(
                        modifier = Modifier
                            .height(50.dp),
                    )

                    Column(
                        modifier = Modifier.padding(PaddingHalf),
                    ) {
                        Text(
                            text = stringResource(R.string.feature_utm_accuracy),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Start,
                        )
                        Text(
                            text = stringResource(R.string.feature_utm_meters, it.accuracy.toInt()),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Start,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Função Composable que lida com as solicitações de permissão de localização.
 *
 * Esta função verifica as permissões de localização necessárias (ACCESS_FINE_LOCATION,
 * ACCESS_COARSE_LOCATION e FOREGROUND_SERVICE_LOCATION para Android U e superior).
 * Se as permissões não forem concedidas, ela inicia uma solicitação de permissão.
 * Assim que todas as permissões forem concedidas, ela executa a lambda [onPermissionGranted].
 *
 * Esta função não faz nada se estiver em execução no modo de inspeção (por exemplo, no Android Studio Layout Editor).
 *
 * @param onPermissionGranted Uma função lambda a ser executada quando todas as permissões de localização necessárias forem concedidas.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionEffect(onPermissionGranted: () -> Unit) {
    if (LocalInspectionMode.current) return

    val permissions = buildList {
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
        }
    }

    val permissionState = rememberMultiplePermissionsState(
        permissions = permissions,
        onPermissionsResult = { result ->
            if (result.values.all { it }) {
                onPermissionGranted()
            }
        },
    )

    LaunchedEffect(permissionState) {
        if (permissionState.allPermissionsGranted) {
            onPermissionGranted()
        } else {
            permissionState.launchMultiplePermissionRequest()
        }
    }
}

/**
 * Compartilha o [value] informado usando uma Intent do Android.
 *
 * Esta função cria uma Intent com a ação `Intent.ACTION_SEND` e define o tipo como "text/plain".
 * O [value] é adicionado como um extra com a chave `Intent.EXTRA_TEXT`.
 * Em seguida, cria um seletor para a Intent e inicia a atividade.
 *
 * @param context O [Context] usado para iniciar a atividade.
 * @param value O valor [String] a ser compartilhado.
 */
private fun share(context: Context, value: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, value)
        type = "text/plain"
    }

    Intent.createChooser(sendIntent, null).also {
        context.startActivity(it)
    }
}

package br.com.b256.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Permission(
    permissions: List<String>,
    denied: @Composable (permission: MultiplePermissionsState) -> Unit,
    granted: @Composable () -> Unit,
) {
    val permission = rememberMultiplePermissionsState(permissions)
    if (permission.allPermissionsGranted) {
        granted()
    } else {
        denied(permission)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequest(
    permission: MultiplePermissionsState,
    bodyText: String,
    buttonText: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = bodyText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Button(
            onClick = {
                permission.launchMultiplePermissionRequest()
            },
            content = {
                Text(
                    text = buttonText,
                )
            }
        )
    }
}

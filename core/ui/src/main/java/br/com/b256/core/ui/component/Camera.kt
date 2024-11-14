package br.com.b256.core.ui.component

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.com.b256.core.designsystem.icon.B256Icons
import br.com.b256.core.ui.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private fun takePicture(
    context: Context,
    imageCapture: ImageCapture,
    onCaptureError: (String) -> Unit,
    onCaptureSuccess: (Bitmap) -> Unit,
) {
    imageCapture.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val bitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true,
                )

                onCaptureSuccess(bitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                onCaptureError(exception.message.orEmpty())
            }
        },
    )
}

@Composable
private fun CameraPreview(
    modifier: Modifier = Modifier,
    context: Context,
    onCaptureError: (String) -> Unit,
    onCaptureSuccess: (Bitmap) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture by remember {
        mutableStateOf(
            ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build(),
        )
    }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                val previewView = PreviewView(context).apply {
                    this.scaleType = PreviewView.ScaleType.FILL_CENTER
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                }

                // CameraX Preview UseCase
                val previewUseCase = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                coroutineScope.launch {
                    val cameraProvider: ProcessCameraProvider =
                        withContext(Dispatchers.IO) {
                            cameraProviderFuture.get()
                        }

                    try {
                        // Deve desvincular o useCase antes de vinculá-los novamente.
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, previewUseCase, imageCapture,
                        )
                    } catch (e: Exception) {
                        onCaptureError(e.message.orEmpty())
                    }
                }

                previewView
            },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            FloatingActionButton(
                shape = CircleShape,
                onClick = {
                    takePicture(
                        context = context,
                        imageCapture = imageCapture,
                        onCaptureError = onCaptureError,
                        onCaptureSuccess = onCaptureSuccess,
                    )
                },
                content = {},
            )
        }
    }
}

@Composable
private fun PicturePreview(
    modifier: Modifier = Modifier,
    bitmap: Bitmap,
    onDismiss: () -> Unit,
    onAccept: (Bitmap) -> Unit,
) {
    Box {
        Image(
            modifier = modifier
                .fillMaxSize(),
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            IconButton(
                onClick = onDismiss,
            ) {
                Icon(
                    imageVector = B256Icons.Dismiss,
                    contentDescription = null,
                )
            }

            IconButton(
                onClick = { onAccept(bitmap) },
            ) {
                Icon(
                    imageVector = B256Icons.Accept,
                    contentDescription = null,
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Camera(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    onCaptureSuccess: (Bitmap) -> Unit,
    onCaptureError: (String) -> Unit,
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    Permission(
        permissions = listOf(Manifest.permission.CAMERA),
        denied = {
            LaunchedEffect(key1 = Unit) {
                if (!it.allPermissionsGranted) {
                    it.launchMultiplePermissionRequest()
                }
            }

            PermissionRequest(
                permission = it,
                bodyText = stringResource(id = R.string.core_ui_camera_permission_body),
                buttonText = stringResource(id = R.string.core_ui_camera_permission_buton),
            )
        },
    ) {
        Box(
            modifier = modifier.fillMaxSize(),
        ) {
            if (bitmap == null) {
                CameraPreview(
                    context = context,
                    onCaptureError = onCaptureError,
                    onCaptureSuccess = { bitmap = it },
                )
            } else {
                PicturePreview(
                    bitmap = bitmap!!,
                    onDismiss = { bitmap = null },
                    onAccept = { onCaptureSuccess(it) },
                )
            }
        }
    }
}

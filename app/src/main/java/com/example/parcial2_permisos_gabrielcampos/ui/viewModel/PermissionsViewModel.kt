package com.example.parcial2_permisos_gabrielcampos.ui.viewModel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PermissionsViewModel(application: Application): AndroidViewModel(application) {
    // Estado de permisos
    val _cameraPermissionState = MutableStateFlow(false)
    val cameraPermissionState: StateFlow<Boolean> = _cameraPermissionState

    val _locationPermissionState = MutableStateFlow(false)
    val locationPermissionState: StateFlow<Boolean> = _locationPermissionState

    val _storagePermissionState = MutableStateFlow(false)
    val storagePermissionState: StateFlow<Boolean> = _storagePermissionState

    val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri: StateFlow<Uri?> = _photoUri

    // Función para actualizar el estado de los permisos
    fun updateCameraPermissionStatus(isGranted: Boolean) {
        _cameraPermissionState.value = isGranted
    }

    fun updateLocationPermissionStatus(isGranted: Boolean) {
        _locationPermissionState.value = isGranted
    }

    fun updateStoragePermissionStatus(isGranted: Boolean) {
        _storagePermissionState.value = isGranted
    }

    // Función para tomar una foto
    fun takePhoto(context: Context) {
        if (_cameraPermissionState.value) {
            val photoFile = createImageFile(context)
            photoFile?.let {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    it
                )
                _photoUri.value = uri
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, uri)
                }
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                context.startActivity(intent)
            }
        }
    }

    // Función para crear un archivo de imagen
    private fun createImageFile(context: Context): File? {
        return try {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        } catch (ex: IOException) {
            null
        }
    }

    // Función para leer un archivo desde el almacenamiento externo
    private fun readFileFromStorage(context: Context) {
        if (_storagePermissionState.value) {
            val externalStorageDir = Environment.getExternalStorageDirectory()
            val file = File(externalStorageDir, "example.txt")
            if (file.exists()) {
                val content = file.readText()
                println("Contenido del archivo: $content")
            } else {
                println("El archivo no existe")
            }
        }
    }

}

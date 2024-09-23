package com.example.parcial2_permisos_gabrielcampos.ui.views

import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.parcial2_permisos_gabrielcampos.ui.viewModel.PermissionsViewModel
import com.google.accompanist.permissions.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsScreen(
    viewModel: PermissionsViewModel,
    navController: NavController
) {
    val context = LocalContext.current

    // Acceder a las propiedades públicas del ViewModel
    val cameraPermissionGranted by viewModel.cameraPermissionState.collectAsState()
    val locationPermissionGranted by viewModel.locationPermissionState.collectAsState()
    val storagePermissionGranted by viewModel.storagePermissionState.collectAsState()
    val photoUri by viewModel.photoUri.collectAsState()

    // Actualizar el estado de permisos al iniciar la vista
    LaunchedEffect(Unit) {
        viewModel.updateCameraPermissionStatus(checkCameraPermission(context))
        viewModel.updateLocationPermissionStatus(checkLocationPermission(context))
        viewModel.updateStoragePermissionStatus(checkStoragePermission(context))
    }

    // UI para la pantalla de permisos
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Administración de Permisos",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp)
        )

        // Botones para solicitar permisos
        PermissionRequestButton(
            icon = Icons.Default.Camera,
            permissionName = "Solicitar Permiso de Cámara",
            isGranted = cameraPermissionGranted,
            onRequestPermission = { requestCameraPermission(context) {
                viewModel.updateCameraPermissionStatus(it)
            }}
        )

        PermissionRequestButton(
            icon = Icons.Default.LocationOn,
            permissionName = "Solicitar Permiso de Ubicación",
            isGranted = locationPermissionGranted,
            onRequestPermission = { requestLocationPermission(context) {
                viewModel.updateLocationPermissionStatus(it)
            }}
        )

        PermissionRequestButton(
            icon = Icons.Default.Storage,
            permissionName = "Solicitar Permiso de Almacenamiento",
            isGranted = storagePermissionGranted,
            onRequestPermission = { requestStoragePermission(context) {
                viewModel.updateStoragePermissionStatus(it)
            }}
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Acciones con Permisos",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )

        // Botones de acciones específicas
        ActionButton(
            icon = Icons.Default.Camera,
            text = "Tomar Foto",
            enabled = cameraPermissionGranted,
            onClick = { viewModel.takePhoto(context) }
        )

        ActionButton(
            icon = Icons.Default.LocationOn,
            text = "Acceder a Ubicación",
            enabled = locationPermissionGranted,
            onClick = { viewModel.accessLocation(context) }
        )

        ActionButton(
            icon = Icons.Default.Storage,
            text = "Leer Archivo",
            enabled = storagePermissionGranted,
            onClick = { viewModel.readFileFromStorage(context) }
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Estados de Permisos",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )

        // Mostrar el estado de cada permiso
        PermissionStatusItem(
            isGranted = cameraPermissionGranted,
            permissionName = "Cámara"
        )
        PermissionStatusItem(
            isGranted = locationPermissionGranted,
            permissionName = "Ubicación"
        )
        PermissionStatusItem(
            isGranted = storagePermissionGranted,
            permissionName = "Almacenamiento"
        )

        // Botón para ver la imagen tomada
        if (photoUri != null) {
            Button(
                onClick = {
                    navController.navigate("image")
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Ver Imagen Tomada")
            }
        }
    }
}

// Pantalla para mostrar la imagen
@Composable
fun ImageScreen(viewModel: PermissionsViewModel) {
    val photoUri: Uri? by viewModel.photoUri.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (photoUri != null) {
            // Mostrar la imagen usando Coil o cualquier otra biblioteca de carga de imágenes
            AsyncImage(
                model = photoUri,
                contentDescription = "Imagen Tomada",
                modifier = Modifier
                    .size(300.dp)
                    .background(Color.LightGray)
            )
        } else {
            Text("No se ha tomado ninguna foto.")
        }
    }
}

// Botón para solicitar permisos
@Composable
fun PermissionRequestButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    permissionName: String,
    isGranted: Boolean,
    onRequestPermission: () -> Unit
) {
    Button(
        onClick = onRequestPermission,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isGranted) Color.Gray else MaterialTheme.colorScheme.primary
        ),
        enabled = !isGranted // El botón se desactiva si el permiso ya fue concedido
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = permissionName, color = Color.White)
    }
}

// Botón de acción
@Composable
fun ActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.primary else Color.Gray
        )
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = Color.White)
    }
}

// Mostrar el estado de los permisos
@Composable
fun PermissionStatusItem(isGranted: Boolean, permissionName: String) {
    val backgroundColor = if (isGranted) Color(0xFFD4EDDA) else Color(0xFFF8D7DA)
    val textColor = if (isGranted) Color(0xFF155724) else Color(0xFF721C24)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$permissionName: ${if (isGranted) "Concedido" else "Denegado"}",
                style = MaterialTheme.typography.titleMedium,
                color = textColor
            )
            Icon(
                imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.Error,
                contentDescription = null,
                tint = textColor
            )
        }
    }
}

// Funciones para verificar y solicitar permisos
private fun checkStoragePermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        android.os.Environment.isExternalStorageManager()
    } else {
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }
}

private fun requestStoragePermission(context: Context, onResult: (Boolean) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.parse(String.format("package:%s", context.packageName))
            context.startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            context.startActivity(intent)
        }
    } else {
        ActivityCompat.requestPermissions(
            (context as ComponentActivity),
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )
    }
    onResult(checkStoragePermission(context))
}

private fun checkCameraPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.CAMERA
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
}

private fun requestCameraPermission(context: Context, onResult: (Boolean) -> Unit) {
    ActivityCompat.requestPermissions(
        (context as ComponentActivity),
        arrayOf(android.Manifest.permission.CAMERA),
        1
    )
    onResult(checkCameraPermission(context))
}

private fun checkLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
}

private fun requestLocationPermission(context: Context, onResult: (Boolean) -> Unit) {
    ActivityCompat.requestPermissions(
        (context as ComponentActivity),
        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
        1
    )
    onResult(checkLocationPermission(context))
}

// Funciones para realizar las acciones

// Tomar una foto
fun PermissionsViewModel.takePhoto(context: Context) {
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

// Crear un archivo para la foto
private fun createImageFile(context: Context): File? {
    return try {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    } catch (ex: IOException) {
        null
    }
}

// Acceder a la ubicación (se necesita Google Play Services)
fun PermissionsViewModel.accessLocation(context: Context) {
    if (_locationPermissionState.value) {
        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        // Mostrar la ubicación en un Toast
                        val message = "Lat: ${it.latitude}, Lon: ${it.longitude}"
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    } ?: run {
                        Toast.makeText(context, "No se pudo obtener la ubicación.", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al obtener la ubicación: ${it.message}", Toast.LENGTH_LONG).show()
                }
        } catch (e: SecurityException) {
            Toast.makeText(context, "Error de permisos: ${e.message}", Toast.LENGTH_LONG).show()
        }
    } else {
        Toast.makeText(context, "Permiso de ubicación no concedido.", Toast.LENGTH_LONG).show()
    }
}

// Leer un archivo del almacenamiento externo y mostrar su contenido en un Toast
fun PermissionsViewModel.readFileFromStorage(context: Context) {
    if (_storagePermissionState.value) {
        // Usar el directorio de almacenamiento externo principal
        val externalStorageDir = Environment.getExternalStorageDirectory()
        val file = File(externalStorageDir, "example.txt")

        // Verificar si el archivo existe
        if (file.exists()) {
            val content = file.readText()
            // Mostrar el contenido del archivo en un Toast
            Toast.makeText(context, content, Toast.LENGTH_LONG).show()
        } else {
            // Mostrar un Toast indicando que el archivo no existe
            Toast.makeText(context, "El archivo no existe en ${file.absolutePath}", Toast.LENGTH_LONG).show()
        }
    } else {
        // Mostrar un Toast indicando que no se tiene permiso
        Toast.makeText(context, "Permiso de almacenamiento no concedido.", Toast.LENGTH_LONG).show()
    }
}

@Preview(showBackground = true)
@Composable
fun PermissionsScreenPreview() {
    PermissionsScreen(viewModel = viewModel(), navController = rememberNavController())
}

package com.example.parcial2_permisos_gabrielcampos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.parcial2_permisos_gabrielcampos.ui.theme.Parcial2_Permisos_GabrielCamposTheme
import com.example.parcial2_permisos_gabrielcampos.ui.viewModel.PermissionsViewModel
import com.example.parcial2_permisos_gabrielcampos.ui.viewModel.PermissionsViewModelFactory
import com.example.parcial2_permisos_gabrielcampos.ui.views.ImageScreen
import com.example.parcial2_permisos_gabrielcampos.ui.views.PermissionsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Parcial2_Permisos_GabrielCamposTheme {
                val navController = rememberNavController()
                val viewModel = PermissionsViewModel(application)

                NavHost(navController = navController, startDestination = "permissions") {
                    composable("permissions") {
                        PermissionsScreen(
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                    composable("image") {
                        ImageScreen(
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Parcial2_Permisos_GabrielCamposTheme {
        Greeting("Android")
    }
}
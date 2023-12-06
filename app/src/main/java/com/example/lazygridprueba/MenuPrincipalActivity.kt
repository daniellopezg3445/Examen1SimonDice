package com.example.lazygridprueba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MenuPrincipalActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MenuPrincipalScreen()
        }
    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun MenuPrincipalScreen() {
        var nombre by remember { mutableStateOf("") }
        var tipoJuego by remember { mutableStateOf(4) }
        val keyboardController = LocalSoftwareKeyboardController.current

        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()  // Asegúrate de que la Columna ocupe toda la pantalla.
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center  // Alinea los elementos al centro verticalmente.
            ) {
                Text(
                    text = "SIMON\nDICE",
                    color = Color.Blue,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))  // Espacio adicional si es necesario.

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Ingresa tu nombre") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text("Elige el tipo de juego:")
                Row {
                    RadioButton(
                        selected = tipoJuego == 4,
                        onClick = { tipoJuego = 4 }
                    )
                    Text("4 Cuadros", modifier = Modifier.clickable { tipoJuego = 4 })

                    Spacer(modifier = Modifier.width(16.dp))

                    RadioButton(
                        selected = tipoJuego == 6,
                        onClick = { tipoJuego = 6 }
                    )
                    Text("6 Cuadros", modifier = Modifier.clickable { tipoJuego = 6 })
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    iniciarJuego(tipoJuego, nombre)
                }) {
                    Text("Empezar Juego")
                }
            }
        }
    }

    private fun iniciarJuego(tipoJuego: Int, nombre: String) {
        val intent = when (tipoJuego) {
            4 -> Intent(this, MainActivity::class.java)
            6 -> Intent(this, SixCuadrosActivity::class.java)
            else -> throw IllegalArgumentException("Tipo de juego no soportado")
        }
        intent.putExtra("NOMBRE", nombre)
        startActivity(intent)
    }
}

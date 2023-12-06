package com.example.lazygridprueba

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lazygridprueba.ui.theme.LazyGridPruebaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class SixCuadrosActivity : ComponentActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var pattern by mutableStateOf(listOf<Int>())
    private var currentPatternIndex by mutableStateOf(0)
    private var isDisplayingPattern by mutableStateOf(false)
    private var round by mutableStateOf(0)
    private var roundText by mutableStateOf("SIMON DICE")
    private var canUserInteract by mutableStateOf(true)
    private var activeUserInput by mutableStateOf<Int?>(null)
    private var score by mutableStateOf(0) // Variable para el puntaje
    private var playerName by mutableStateOf("") // Nombre del jugador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerName = intent.getStringExtra("NOMBRE") ?: "" // Recuperar el nombre del jugador del intent

        setContent {
            val userInput = remember { mutableStateOf(listOf<Int>()) }
            val canStartNewGame = remember { mutableStateOf(true) }

            LazyGridPruebaTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = roundText,
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Nuevo Text composable para mostrar en qué ronda va el juego
                        if (round > 0) {
                            Text(
                                text = "Ronda: $round".padEnd(30, ' ') + "Puntaje: $score",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onBackground
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(16.dp),
                            content = {
                                items(6) { i ->
                                    val color = when (i) {
                                        0 -> Color.Red
                                        1 -> Color.Green
                                        2 -> Color.Blue
                                        3 -> Color.Magenta
                                        4 -> Color.LightGray
                                        5 -> Color.Cyan
                                        else -> Color.Yellow
                                    }

                                    val isUserInputActive = activeUserInput == i

                                    Box(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(5.dp))
                                            .background(
                                                if (isDisplayingPattern && i == pattern[currentPatternIndex] || isUserInputActive) {
                                                    color
                                                } else {
                                                    color.copy(alpha = 0.30f)
                                                }
                                            )
                                            .clickable(enabled = canUserInteract) {
                                                onUserInput(i, userInput)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        // Aquí podrías poner contenido dentro de la caja si es necesario
                                    }
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                if (pattern.isEmpty()) {
                                    nextRound()
                                }
                                canStartNewGame.value = false  // Deshabilita el botón una vez que el juego comienza
                            },
                            enabled = canStartNewGame.value,  // El botón está habilitado basado en el estado de canStartNewGame
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = "Simon Dice")
                        }
                    }
                }
            }
        }
    }
    private fun saveScoreToFile(name: String, score: Int) { //guardarPuntuacion
        val data = "$name:$score\n"
        val fileOutput = openFileOutput("scores.txt", Context.MODE_APPEND)
        fileOutput.write(data.toByteArray())
        fileOutput.close()
    }

    private fun readScoresFromFile(): Map<String, Int> { //cargar al archivo
        val scores = mutableMapOf<String, Int>()
        try {
            val fileInput = openFileInput("scores.txt")
            fileInput.bufferedReader().forEachLine { line ->
                val (name, score) = line.split(":")
                scores[name] = score.toInt()
            }
            fileInput.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return scores
    }

    private fun onUserInput(input: Int, userInputState: MutableState<List<Int>>) {
        userInputState.value = userInputState.value + input
        checkUserInput(userInputState)
        illuminateButton(input)
    }

    private fun illuminateButton(buttonIndex: Int) {
        coroutineScope.launch {
            activeUserInput = buttonIndex
            delay(300) // Ilumina el botón por medio segundo
            activeUserInput = null // Vuelve al estado opaco
        }
    }

    private fun checkUserInput(input: MutableState<List<Int>>) {
        if (!isDisplayingPattern) {
            if (input.value.size > pattern.size || input.value.last() != pattern[input.value.size - 1]) {
                // Usuario se equivoca, muestra mensaje y guarda puntaje
                roundText = "¡PERDISTE!"
                canUserInteract = false
                coroutineScope.launch {
                    delay(3000) // Esperar 3 segundos
                    saveScoreToFile(playerName, score) // Guarda el puntaje en el archivo
                    showScores() // Inicia ScoreboardActivity
                }
            } else if (input.value.size == pattern.size) {
                showToast("¡Correcto!")
                score += pattern.size
                input.value = listOf()
                nextRound()
            }
        }
    }
    private fun showScores() {
        val intent = Intent(this, ScoreboardActivity::class.java)
        startActivity(intent)
        finish() // Finaliza MainActivity
    }

    private fun displayPattern() {
        coroutineScope.launch {
            roundText = "Prepárate..."
            canUserInteract = false
            for (i in 3 downTo 1) {
                roundText = i.toString()
                delay(1000)
            }
            roundText = "¡Go!"
            delay(500)
            isDisplayingPattern = true
            for (index in pattern.indices) {
                currentPatternIndex = index
                delay(600L)
                if (index < pattern.size - 1 && pattern[index] == pattern[index + 1]) {
                    isDisplayingPattern = false
                    delay(300L)
                    isDisplayingPattern = true
                }
            }
            delay(600L)
            isDisplayingPattern = false
            canUserInteract = true
            roundText = "Es tu turno $playerName"  // Se añade el nombre del jugador aquí
        }
    }

    private fun nextRound() {
        round++
        pattern = pattern + Random.nextInt(6)
        displayPattern()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}
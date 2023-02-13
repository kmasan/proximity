package com.b22706.proximity

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.b22706.proximity.ui.theme.ProximityTheme

class MainActivity : ComponentActivity() {
    lateinit var proximity: ProximitySensor

    var externalFilePath = ""
    var csvBoolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        proximity = ProximitySensor(this, null).apply { start() }
        externalFilePath = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()

        createSetContent()
    }

    private fun createSetContent(){
        setContent {
            var csvButtonText by remember { mutableStateOf("csv start") }

            ProximityTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        OnClickButton(text = csvButtonText) {
                            csvButtonText = when(csvBoolean){
                                true -> {
                                    proximity.queueBoolean = false
                                    proximity.csvWriter(externalFilePath, System.currentTimeMillis().toString())
                                    "csv start"
                                }
                                false ->{
                                    proximity.queueReset()
                                    proximity.queueBoolean = true
                                    "csv write"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Composable
fun OnClickButton(text: String, onClick: () -> Unit){
    Button(onClick = onClick
    ) {
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ProximityTheme {
        Greeting("Android")
    }
}
package com.example.pos

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.pos.ui.theme.PosTheme

@OptIn(ExperimentalMaterial3Api::class)
class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PosTheme {
                SettingsScreen(
                    onBackPressed = { finish() },
                    preferences = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBackPressed: () -> Unit, preferences: SharedPreferences) {
    // Load initial values from SharedPreferences
    var scheme by remember { mutableStateOf(TextFieldValue(preferences.getString("scheme", "http") ?: "http")) }
    var url by remember { mutableStateOf(TextFieldValue(preferences.getString("url", "www.example.com") ?: "www.example.com")) }
    var port by remember { mutableStateOf(TextFieldValue(preferences.getString("port", "80") ?: "80")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Configure Server Settings", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = scheme,
                        onValueChange = { scheme = it },
                        label = { Text("Scheme (http/https)") }
                    )
                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        label = { Text("URL") }
                    )
                    OutlinedTextField(
                        value = port,
                        onValueChange = { port = it },
                        label = { Text("Port") },
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            // Save values to SharedPreferences
                            preferences.edit()
                                .putString("scheme", scheme.text)
                                .putString("url", url.text)
                                .putString("port", port.text)
                                .apply()
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    )
}

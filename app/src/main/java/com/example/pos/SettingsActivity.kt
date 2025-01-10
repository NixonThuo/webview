package com.example.pos

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    var protocol by remember { mutableStateOf(TextFieldValue(preferences.getString("protocol", "http") ?: "http")) }
    var domainOrIp by remember { mutableStateOf(TextFieldValue(preferences.getString("domain_ip", "www.example.com") ?: "www.example.com")) }
    var port by remember { mutableStateOf(TextFieldValue(preferences.getString("port", "80") ?: "80")) }
    var pageReference by remember { mutableStateOf(TextFieldValue(preferences.getString("page_reference", "index") ?: "index")) }

    // Load values for the second section
    var protocol2 by remember { mutableStateOf(TextFieldValue(preferences.getString("protocol2", "http") ?: "http")) }
    var domainOrIp2 by remember { mutableStateOf(TextFieldValue(preferences.getString("domain_ip2", "www.example.com") ?: "www.example.com")) }
    var port2 by remember { mutableStateOf(TextFieldValue(preferences.getString("port2", "80") ?: "80")) }
    var pageReference2 by remember { mutableStateOf(TextFieldValue(preferences.getString("page_reference2", "index") ?: "index")) }
    var mobileNumber by remember { mutableStateOf(TextFieldValue(preferences.getString("mobile_number", "") ?: "")) }
    var userSerialNumber by remember { mutableStateOf(TextFieldValue(preferences.getString("user_serial_number", "") ?: "")) }

    // New field for payment mode
    var paymentMode by remember { mutableStateOf(TextFieldValue(preferences.getString("payment_mode", "MPESA") ?: "MPESA")) }

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
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Configure Backend Server Settings", style = MaterialTheme.typography.titleMedium)

                    OutlinedTextField(
                        value = protocol,
                        onValueChange = { protocol = it },
                        label = { Text("Protocol (http/https)") }
                    )
                    OutlinedTextField(
                        value = domainOrIp,
                        onValueChange = { domainOrIp = it },
                        label = { Text("Domain/IP") }
                    )
                    OutlinedTextField(
                        value = port,
                        onValueChange = { port = it },
                        label = { Text("Port") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = pageReference,
                        onValueChange = { pageReference = it },
                        label = { Text("Page Reference") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Configure Additional Backend Server Settings", style = MaterialTheme.typography.titleMedium)

                    OutlinedTextField(
                        value = protocol2,
                        onValueChange = { protocol2 = it },
                        label = { Text("Protocol (http/https)") }
                    )
                    OutlinedTextField(
                        value = domainOrIp2,
                        onValueChange = { domainOrIp2 = it },
                        label = { Text("Domain/IP") }
                    )
                    OutlinedTextField(
                        value = port2,
                        onValueChange = { port2 = it },
                        label = { Text("Port") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = pageReference2,
                        onValueChange = { pageReference2 = it },
                        label = { Text("Page Reference") }
                    )
                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = { mobileNumber = it },
                        label = { Text("Mobile Phone Number") }
                    )
                    OutlinedTextField(
                        value = userSerialNumber,
                        onValueChange = { userSerialNumber = it },
                        label = { Text("User Serial Number/ID") }
                    )

                    OutlinedTextField(
                        value = paymentMode,
                        onValueChange = { paymentMode = it },
                        label = { Text("Payment Mode") }
                    )

                    Button(
                        onClick = {
                            // Save values to SharedPreferences
                            preferences.edit()
                                .putString("protocol", protocol.text)
                                .putString("domain_ip", domainOrIp.text)
                                .putString("port", port.text)
                                .putString("page_reference", pageReference.text)
                                .putString("protocol2", protocol2.text)
                                .putString("domain_ip2", domainOrIp2.text)
                                .putString("port2", port2.text)
                                .putString("page_reference2", pageReference2.text)
                                .putString("mobile_number", mobileNumber.text)
                                .putString("user_serial_number", userSerialNumber.text)
                                .putString("payment_mode", paymentMode.text)
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


package com.example.pos.composables

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import com.example.pos.database.CallEntity
import com.example.pos.database.SmsDatabase
import com.example.pos.models.CallLogEntry
import com.example.pos.models.fetchCallLogs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallLogScreen(onBackPressed: () -> Unit, preferences: SharedPreferences) {
    val context = LocalContext.current
    var callLogs by remember { mutableStateOf(emptyList<CallEntity>()) }

    // Load call logs from the database
    LaunchedEffect(Unit) {
        val db = SmsDatabase.getDatabase(context)
        callLogs = db.callDao().getAllCalls()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Call Logs") },
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(callLogs) { log ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "Number: ${log.phoneNumber ?: "Unknown"}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Name: ${log.contactName}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Type: ${log.callType}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Date: ${log.timestamp}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}


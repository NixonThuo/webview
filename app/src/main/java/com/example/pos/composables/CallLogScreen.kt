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
import com.example.pos.models.CallLogEntry
import com.example.pos.models.fetchCallLogs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallLogScreen(onBackPressed: () -> Unit, preferences: SharedPreferences) {
    val context = LocalContext.current
    var callLogs by remember { mutableStateOf(emptyList<CallLogEntry>()) }
    println("entering call center")
    // Request permission and fetch logs
    RequestCallLogPermission {
        callLogs = fetchCallLogs(context)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calls") },
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
                                    "Name: ${log.address}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    "Number: ${log.number}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Date: ${log.date}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Duration: ${log.duration} sec",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Type: ${log.type}",
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

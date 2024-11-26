package com.example.pos.composables

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import com.example.pos.models.CallLogEntry
import com.example.pos.models.fetchCallLogs

@Composable
fun CallLogScreen() {
    val context = LocalContext.current
    var callLogs by remember { mutableStateOf(emptyList<CallLogEntry>()) }
    println("entering call center")
    // Request permission and fetch logs
    RequestCallLogPermission {
        callLogs = fetchCallLogs(context)
    }

    // Display call logs
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
                    Text("Number: ${log.number}", style = MaterialTheme.typography.bodyLarge)
                    Text("Date: ${log.date}", style = MaterialTheme.typography.bodyMedium)
                    Text("Duration: ${log.duration} sec", style = MaterialTheme.typography.bodyMedium)
                    Text("Type: ${log.type}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

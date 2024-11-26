package com.example.pos.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pos.models.SmsMessageEntry
import com.example.pos.models.fetchSmsMessages

@Composable
fun MessagesScreen() {
    val context = LocalContext.current
    var smsMessages by remember { mutableStateOf(emptyList<SmsMessageEntry>()) }

    // Request permission and fetch messages
    RequestSmsPermission {
        smsMessages = fetchSmsMessages(context)
    }

    // Display SMS messages
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(smsMessages) { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("From: ${message.address}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Message: ${message.body}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Date: ${message.date}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

package com.example.pos.composables

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pos.database.SmsDatabase  // Import your database class
import com.example.pos.database.SmsEntity       // Import your SMS entity class
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(onBackPressed: () -> Unit) {
    val context = LocalContext.current
    val smsDatabase = SmsDatabase.getDatabase(context)  // Access your Room database
    val smsDao = smsDatabase.smsDao()                  // Get the DAO
    var smsMessages by remember { mutableStateOf(emptyList<SmsEntity>()) }
    val coroutineScope = rememberCoroutineScope()

    // State to control the visibility of the dialog
    var showDialog by remember { mutableStateOf(false) }

    // Fetch messages from the database
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            smsMessages = smsDao.getAllSms()  // Retrieve SMS from the database
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Messages") },
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Message"
                )
            }
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
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
                                Text("From: ${message.sender}", style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Message: ${message.messageBody}", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Date: ${Date(message.timestamp)}", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Type: ${message.messageType}", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Priority: ${message.messagePriority}", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Synchronized: ${if (message.isSynchronized) "Yes" else "No"}", style = MaterialTheme.typography.bodyMedium)
                                if (message.isSynchronized) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Sync Date: ${Date(message.isSynchronizedDate)}", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }

            // Dialog for adding a new message
            if (showDialog) {
                AddMessageDialog(
                    onDismiss = { showDialog = false },
                    onAddMessage = { sender, messageBody, messageType, messagePriority ->
                        coroutineScope.launch {
                            val newMessage = SmsEntity(
                                id = 0,
                                sender = sender,
                                messageBody = messageBody,
                                timestamp = System.currentTimeMillis(),
                                isSynchronized = false,
                                isSynchronizedDate = 0L,
                                messageType = messageType,
                                messagePriority = messagePriority
                            )
                            smsDao.insert(newMessage)
                            smsMessages = smsDao.getAllSms()  // Refresh the message list
                        }
                        showDialog = false
                    }
                )
            }
        }
    )
}

@Composable
fun AddMessageDialog(onDismiss: () -> Unit, onAddMessage: (String, String, String, String) -> Unit) {
    var sender by remember { mutableStateOf("") }
    var messageBody by remember { mutableStateOf("") }
    var messageType by remember { mutableStateOf("") }
    var messagePriority by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Message") },
        text = {
            Column {
                OutlinedTextField(
                    value = sender,
                    onValueChange = { sender = it },
                    label = { Text("Sender") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = messageBody,
                    onValueChange = { messageBody = it },
                    label = { Text("Message Body") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = messageType,
                    onValueChange = { messageType = it },
                    label = { Text("Message Type") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = messagePriority,
                    onValueChange = { messagePriority = it },
                    label = { Text("Message Priority") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onAddMessage(sender, messageBody, messageType, messagePriority)
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}




package com.example.pos.composables

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pos.database.SmsDao
import com.example.pos.database.SmsDatabase  // Import your database class
import com.example.pos.database.SmsEntity       // Import your SMS entity class
import kotlinx.coroutines.launch
import java.util.Date
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import com.example.pos.MainActivity.Companion.REQUEST_PHONE_STATE_PERMISSION

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
    var showImportDialog by remember { mutableStateOf(false) }

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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    FloatingActionButton(
                        onClick = {
                            showImportDialog = true
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh, // Replace with your desired import icon
                            contentDescription = "Import"
                        )
                    }
                    FloatingActionButton(
                        onClick = { showDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Message"
                        )
                    }
                }
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

                // Dialog for importing messages
                if (showImportDialog) {
                    ImportDialog(
                        onDismiss = { showImportDialog = false },
                        onImport = { dateInput  ->
                            coroutineScope.launch {
                                importMessages(context, dateInput, smsDao)
                                smsMessages = smsDao.getAllSms()  // Refresh the message list after import
                            }
                            showImportDialog = false
                        }
                    )
                }
            }
        }
    )

}

@Composable
fun AddMessageDialog(onDismiss: () -> Unit, onAddMessage: (String, String, String, String) -> Unit) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    val paymentMode = sharedPreferences.getString("payment_mode", "Unknown") // Default to "Unknown" if not found
    var messageBody by remember { mutableStateOf("") }
    var messageType by remember { mutableStateOf("text") }
    var messagePriority by remember { mutableStateOf("High") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Message") },
        text = {
            Column {
                OutlinedTextField(
                    value = paymentMode ?: "Unknown", // Display paymentMode value
                    onValueChange = {},              // No-op since it's uneditable
                    label = { Text("Sender") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false                  // Make it uneditable
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
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = messagePriority,
                    onValueChange = { messagePriority = it },
                    label = { Text("Message Priority") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onAddMessage(paymentMode ?: "Unknown", messageBody, messageType, messagePriority)
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

@Composable
fun ImportDialog(onDismiss: () -> Unit, onImport: (String) -> Unit) {
    // Get the current date in the format "YYYY-MM-DD"
    val todayDate = remember {
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
    }
    var dateInput by remember { mutableStateOf(todayDate) } // Default to today's date

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import Messages") },
        text = {
            Column {
                OutlinedTextField(
                    value = dateInput,
                    onValueChange = { dateInput = it },
                    label = { Text("Enter Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onImport(dateInput)
            }) {
                Text("Import")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun importMessages(context: Context, date: String, smsDao: SmsDao) {
    val sharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    val paymentMode = sharedPreferences.getString("payment_mode", "Unknown")
    // Check for SMS permissions
    val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED

    if (!hasPermission) {
        // If permission is not granted, request it
        ActivityCompat.requestPermissions(
            (context as Activity), // Cast context to Activity to use requestPermissions
            arrayOf(Manifest.permission.READ_SMS),
            REQUEST_PHONE_STATE_PERMISSION
        )
        return // Exit the function; permission request is asynchronous
    }

    val contentResolver = context.contentResolver
    val uri = android.provider.Telephony.Sms.CONTENT_URI

    // Define the selection filter for SMS
    val selection = "${android.provider.Telephony.Sms.DATE} >= ?"
    val dateInMillis = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        .parse(date)?.time ?: 0L
    val selectionArgs = arrayOf(dateInMillis.toString())

    // Query SMS messages
    val cursor = contentResolver.query(
        uri,
        null, // Fetch all columns
        selection,
        selectionArgs,
        "${android.provider.Telephony.Sms.DATE} DESC" // Order by date descending
    )

    cursor?.use {
        val smsList = mutableListOf<SmsEntity>()
        val dateColumn = it.getColumnIndex(android.provider.Telephony.Sms.DATE)
        val bodyColumn = it.getColumnIndex(android.provider.Telephony.Sms.BODY)
        val addressColumn = it.getColumnIndex(android.provider.Telephony.Sms.ADDRESS)

        while (it.moveToNext()) {
            val timestamp = it.getLong(dateColumn)
            val body = it.getString(bodyColumn) ?: "No Content"
            val sender = it.getString(addressColumn) ?: "Unknown"

            println("fetch  body")
            println(body)

            // Check if the message body already exists in the database
            runBlocking {
                if(sender == paymentMode) {
                    val exists = smsDao.doesMessageBodyExist(body) > 0
                    if (!exists) {
                        // Create an SMS entity if it doesn't exist
                        val smsEntity = SmsEntity(
                            id = 0,
                            sender = sender,
                            messageBody = body,
                            timestamp = timestamp,
                            isSynchronized = false,
                            isSynchronizedDate = 0L,
                            messageType = "imported",
                            messagePriority = "Normal"
                        )
                        smsList.add(smsEntity)
                    }
                }
            }
        }

        // Perform the database operation inside a coroutine
        CoroutineScope(Dispatchers.IO).launch {
            smsDao.insertAll(smsList)
        }
    }
}








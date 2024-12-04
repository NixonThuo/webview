package com.example.pos.composables

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pos.database.SmsDatabase  // Import your database class
import com.example.pos.SmsEntity       // Import your SMS entity class
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(onBackPressed: () -> Unit) {
    val context = LocalContext.current
    val smsDatabase = SmsDatabase.getDatabase(context)  // Access your Room database
    val smsDao = smsDatabase.smsDao()                  // Get the DAO
    var smsMessages by remember { mutableStateOf(emptyList<SmsEntity>()) }
    val coroutineScope = rememberCoroutineScope()

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
                                Text("Date: ${message.timestamp}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    )
}


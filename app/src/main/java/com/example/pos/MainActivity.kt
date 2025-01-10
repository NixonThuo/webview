package com.example.pos

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.pos.database.CallDao
import com.example.pos.database.SmsDao
import com.example.pos.services.DataUploader
import com.example.pos.ui.theme.PosTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var callReceiver: CallBroadcastReceiver
    private lateinit var callDao: CallDao
    private lateinit var smsDao: SmsDao
    private lateinit var dataUploader: DataUploader

    // Activity Result Launcher for permission request
    private val requestSmsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Register CallBroadcastReceiver
        callReceiver = CallBroadcastReceiver()
        val intentFilter = IntentFilter().apply {
            addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED)
            addAction(Intent.ACTION_NEW_OUTGOING_CALL)
        }

        registerReceiver(callReceiver, intentFilter)

        val preferences = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        setContent {
            PosTheme {
                MainScreen(preferences)
            }
        }

        // Check for SMS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestSmsPermissionLauncher.launch(Manifest.permission.RECEIVE_SMS)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister CallBroadcastReceiver
        unregisterReceiver(callReceiver)
    }

    // Override the back pressed action to prevent app from closing
    override fun onBackPressed() {
        // Do nothing or show a message to the user
        // For example, you can show a Toast or Snackbar
        Toast.makeText(this, "Back button is disabled", Toast.LENGTH_SHORT).show()
        // Alternatively, you can just override this method without any action
        // to prevent the app from being closed without any notification.
    }

    companion object {
        const val REQUEST_PHONE_STATE_PERMISSION = 1
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(preferences: SharedPreferences) {
    val appName = stringResource(id = R.string.app_name)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var refreshWebView by remember { mutableStateOf(false) } // Trigger for WebView refresh

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                SideMenu() // Drawer content
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = {
                            Text(appName)
                        },
                        actions = {
                            IconButton(onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu")
                            }
                        }
                    )
                },
                bottomBar = {
                    BottonNavBar()
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            refreshWebView = !refreshWebView // Toggle the refresh state
                        },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh WebView")
                    }
                },
                content = { innerPadding ->
                    Row {
                        Column(
                            modifier = Modifier
                                .padding(innerPadding),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            WebViewScreen(preferences, refreshTrigger = refreshWebView)
                        }
                    }
                }
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(preferences: SharedPreferences, refreshTrigger: Boolean) {
    // Re-create the WebView whenever refreshTrigger changes
    val url = remember(preferences, refreshTrigger) {
        buildString {
            append(preferences.getString("scheme", "http"))
            append("://")
            append(preferences.getString("domain_ip", "www.misoo.co.ke"))
            val port = preferences.getString("port", "80")
            if (!port.isNullOrEmpty()) {
                append(":$port")
            }
            append("/DotPESA/index.jsp")
        }
    }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.setSupportZoom(true)
                loadUrl(url) // Load the URL on creation
            }
        },
        update = { webView ->
            webView.loadUrl(url) // Reload the URL on updates
        }
    )
}

@Composable
fun BottonNavBar() {
    val context = LocalContext.current  // Access the current context for Intents

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.primary,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(onClick = { /* Stay in current MainActivity */ }) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Web"
                )
            }
            IconButton(onClick = {
                val intent = Intent(context, CallsActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = "Calls"
                )
            }
            IconButton(onClick = {
                val intent = Intent(context, MessagesActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Filled.MailOutline,
                    contentDescription = "Messages"
                )
            }
        }
    }
}

@Composable
fun SideMenu() {
    val context = LocalContext.current // Access context for navigation

    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxHeight()
            .systemBarsPadding(),
        drawerContainerColor = MaterialTheme.colorScheme.primaryContainer,
        drawerContentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Text(
            text = "Menu",
            modifier = Modifier
                .padding(16.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(8.dp))
        NavigationDrawerItem(
            label = {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            selected = false,
            onClick = {
                context.startActivity(Intent(context, SettingsActivity::class.java))
            },
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            )
        )
    }
}

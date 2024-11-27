package com.example.pos

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.pos.ui.theme.PosTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val preferences = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        setContent {
            PosTheme {
                MainScreen(preferences)
            }
        }
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
            append(preferences.getString("url", "www.google.com"))
            val port = preferences.getString("port", "80")
            if (!port.isNullOrEmpty()) {
                append(":$port")
            }
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
                println(url)
                loadUrl(url) // Load the URL on creation
            }
        },
        update = { webView ->
            println(url)
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
            // Web Button - Stay in MainActivity
            IconButton(onClick = { /* Stay in current MainActivity */ }) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Web"
                )
            }
            // Calls Button - Open CallsActivity
            IconButton(onClick = {
                val intent = Intent(context, CallsActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = "Calls"
                )
            }
            // Messages Button - Open MessagesActivity
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
            .systemBarsPadding(), // Automatically handles insets
        drawerContainerColor = MaterialTheme.colorScheme.primaryContainer, // Set background color
        drawerContentColor = MaterialTheme.colorScheme.onPrimaryContainer // Set text/icon color
    ) {
        Text(
            text = "Menu",
            modifier = Modifier
                .padding(16.dp),
            style = MaterialTheme.typography.titleMedium, // Use typography style
            color = MaterialTheme.colorScheme.primary // Override color if needed
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f), // Divider with reduced opacity
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(8.dp)) // Add spacing between items
        NavigationDrawerItem(
            label = {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.bodyMedium // Use consistent typography
                )
            },
            selected = false,
            onClick = {
                context.startActivity(Intent(context, SettingsActivity::class.java))
            },
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer, // Color for selected state
            )
        )
    }
}




package com.example.pos

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.example.pos.composables.CallLogScreen
import com.example.pos.ui.theme.PosTheme

class CallsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            println("call log")
            PosTheme {
                CallLogScreen(
                    onBackPressed = { finish() },
                    preferences = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
                )
            }
        }
    }
}
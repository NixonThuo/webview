package com.example.pos

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.example.pos.composables.MessagesScreen
import com.example.pos.ui.theme.PosTheme

class MessagesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PosTheme {
                MessagesScreen(
                    onBackPressed = { finish() }
                )
            }
        }
    }
}
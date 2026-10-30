package com.duoc.agroconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.duoc.agroconnect.ui.AgroApp
import com.duoc.agroconnect.ui.theme.AgroConnectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgroConnectTheme {
                AgroApp()
            }
        }
    }
}

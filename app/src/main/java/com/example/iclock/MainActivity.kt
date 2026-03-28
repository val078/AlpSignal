package com.example.iclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.iclock.navigation.AppNavigation
import com.example.iclock.ui.theme.IClockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val navigateTo = intent.getStringExtra("navigate_to")
        setContent {
            IClockTheme {
                AppNavigation(
                    startDestination = if (navigateTo == "alert_detail") "alert_detail" else "home"
                )
            }
        }
    }
}
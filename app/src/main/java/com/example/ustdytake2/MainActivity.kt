package com.example.ustdytake2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ustdytake2.ui.UstdyPlannerApp
import com.example.ustdytake2.ui.theme.UstdyTake2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UstdyTake2Theme {
                UstdyPlannerApp()
            }
        }
    }
}

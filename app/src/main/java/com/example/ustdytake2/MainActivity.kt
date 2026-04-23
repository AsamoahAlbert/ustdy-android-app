package com.example.ustdytake2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ustdytake2.ui.UstdyPlannerApp
import com.example.ustdytake2.ui.theme.UstdyTake2Theme
import com.example.ustdytake2.viewmodel.AuthViewModel
import com.example.ustdytake2.viewmodel.ClassViewModel
import com.example.ustdytake2.viewmodel.GamificationViewModel
import com.example.ustdytake2.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val classViewModel: ClassViewModel by viewModels()
    private val taskViewModel: TaskViewModel by viewModels()
    private val gamificationViewModel: GamificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UstdyTake2Theme {
                UstdyPlannerApp(
                    authViewModel = authViewModel,
                    classViewModel = classViewModel,
                    taskViewModel = taskViewModel,
                    gamificationViewModel = gamificationViewModel
                )
            }
        }
    }
}

package com.example.ustdytake2.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ustdytake2.ui.theme.Cream100
import com.example.ustdytake2.ui.theme.Cream50
import com.example.ustdytake2.ui.theme.Sky500
import com.example.ustdytake2.ui.theme.UstdyTake2Theme
import com.example.ustdytake2.viewmodel.AuthState

@Composable
fun LoginScreen(
    authState: AuthState,
    onLogin: (String, String) -> Unit,
    onCreateAccount: (String, String) -> Unit,
    onContinueAsDemo: () -> Unit,
    modifier: Modifier = Modifier
) {
    var identifier by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var helperMessage by rememberSaveable { mutableStateOf("Use a school email or username to get started.") }
    val isValid = identifier.isNotBlank() && password.length >= 6
    val authMessage = when (authState) {
        is AuthState.Error -> authState.message
        AuthState.Loading -> "Signing in..."
        else -> helperMessage
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF163049), Color(0xFF274762), Cream50)
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to U-stdy!",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Plan classes, deadlines, quizzes, exams, and reminders from one calm workspace.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFD8E7F1)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Cream50)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Login / Create Account",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    OutlinedTextField(
                        value = identifier,
                        onValueChange = {
                            identifier = it
                            helperMessage = "Use a school email or username to get started."
                        },
                        label = { Text("Username or Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            helperMessage = "Passwords should be at least 6 characters."
                        },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        )
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {
                                if (isValid) onLogin(identifier.trim(), password)
                                else helperMessage = "Enter a valid username/email and a 6+ character password."
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Login")
                        }
                        Button(
                            onClick = {
                                if (isValid) onCreateAccount(identifier.trim(), password)
                                else helperMessage = "Create account needs a valid identifier and a 6+ character password."
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Create Account")
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = authMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (authState is AuthState.Error) {
                            MaterialTheme.colorScheme.error
                        } else {
                            Sky500
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = {
                            helperMessage = "Password resets are usually sent to your school email."
                        }) {
                            Text("Forgot Password")
                        }
                        TextButton(onClick = onContinueAsDemo) {
                            Text("Continue as Demo User")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Cream100, RoundedCornerShape(20.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "New users will be guided through a first-time setup after signing in.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    UstdyTake2Theme {
        LoginScreen(
            authState = AuthState.Idle,
            onLogin = { _, _ -> },
            onCreateAccount = { _, _ -> },
            onContinueAsDemo = {}
        )
    }
}

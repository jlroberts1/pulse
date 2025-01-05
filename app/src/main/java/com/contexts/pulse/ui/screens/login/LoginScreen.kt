/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contexts.pulse.R
import com.contexts.pulse.extensions.autofill
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

data class Star(
    val initialX: Float,
    val initialY: Float,
    val size: Float,
    val speed: Float,
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel(),
    onNavigateToHome: () -> Unit,
) {
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    Color(0x4D3635C7),
                                    Color.Transparent,
                                ),
                        ),
                    ),
        )

        if (uiState.success) {
            LaunchedEffect(Unit) {
                onNavigateToHome()
            }
        }

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card(
                modifier =
                    Modifier
                        .width(IntrinsicSize.Min)
                        .padding(16.dp),
            ) {
                Column(
                    modifier =
                        Modifier
                            .padding(24.dp)
                            .width(320.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {

                    if (uiState.loading) {
                        CircularProgressIndicator()
                    }

                    Image(
                        painter = painterResource(R.mipmap.ic_launcher_foreground),
                        "Pulse",
                    )

                    Text(
                        text = "Welcome to Pulse",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )

                    Text(
                        text = "Connect with the Bluesky community",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                    )

                    OutlinedTextField(
                        value = identifier,
                        onValueChange = { identifier = it },
                        label = { Text("Handle or Email") },
                        placeholder = { Text("@handle.bsky.social") },
                        modifier =
                            Modifier.fillMaxWidth()
                                .autofill(
                                    autofillTypes = listOf(AutofillType.EmailAddress),
                                    onFill = { identifier = it },
                                ),
                        singleLine = true,
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
                                unfocusedPlaceholderColor = Color.White.copy(alpha = 0.3f),
                                unfocusedTextColor = Color.White,
                            ),
                        keyboardOptions =
                            KeyboardOptions(
                                autoCorrectEnabled = false,
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next,
                            ),
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("App password") },
                        placeholder = { Text("abcd-1234-efgh-5678") },
                        modifier =
                            Modifier.fillMaxWidth()
                                .autofill(
                                    autofillTypes = listOf(AutofillType.Password),
                                    onFill = { password = it },
                                ),
                        singleLine = true,
                        visualTransformation =
                            if (isPasswordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done,
                            ),
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
                                unfocusedTextColor = Color.White,
                            ),
                        trailingIcon = {
                            IconButton(
                                onClick = { isPasswordVisible = !isPasswordVisible },
                            ) {
                                Icon(
                                    imageVector =
                                        if (isPasswordVisible) {
                                            Icons.Default.VisibilityOff
                                        } else {
                                            Icons.Default.Visibility
                                        },
                                    contentDescription =
                                        if (isPasswordVisible) {
                                            "Hide password"
                                        } else {
                                            "Show password"
                                        },
                                )
                            }
                        },
                    )

                    Text(
                        text = "You can generate an app password in your Bluesky account settings",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f),
                    )

                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.login(identifier, password)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Sign in")
                    }
                }
            }
        }
    }
}

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contexts.pulse.R
import com.contexts.pulse.extensions.autofill
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(onNavigateToHome: () -> Unit) {
    val viewModel: LoginViewModel = koinViewModel()
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier =
            Modifier
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (uiState.loading) {
            CircularProgressIndicator()
        }

        if (uiState.success) {
            LaunchedEffect(Unit) {
                onNavigateToHome()
            }
        }

        Image(
            painter = painterResource(R.mipmap.ic_launcher_monochrome),
            "Pulse",
        )

        Text(
            text = "Pulse",
            style = MaterialTheme.typography.titleLarge,
        )

        OutlinedTextField(
            value = identifier,
            onValueChange = { identifier = it },
            label = { Text("Handle or Email") },
            placeholder = { Text("username.bsky.social") },
            modifier =
                Modifier.fillMaxWidth()
                    .autofill(
                        autofillTypes = listOf(AutofillType.EmailAddress),
                        onFill = { identifier = it },
                    ),
            singleLine = true,
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

        Button(
            onClick = {
                scope.launch {
                    viewModel.login(identifier, password)
                }
            },
            colors =
                ButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    disabledContentColor = Color.White,
                    disabledContainerColor = Color.DarkGray,
                ),
            modifier = Modifier.fillMaxWidth(),
            enabled = identifier.isNotBlank() && password.isNotBlank() && !uiState.loading,
        ) {
            Text("Login")
        }
    }
}

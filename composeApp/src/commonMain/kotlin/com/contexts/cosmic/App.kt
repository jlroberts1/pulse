package com.contexts.cosmic

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.contexts.cosmic.ui.theme.CosmicTheme
import com.materialkolor.rememberDynamicMaterialThemeState
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    val isDarkTheme = isSystemInDarkTheme()
    val isAmoled by rememberSaveable { mutableStateOf(false) }
    val color = MaterialTheme.colorScheme.primary.toArgb()
    val seedColor by rememberSaveable { mutableStateOf(color) }

    val state =
        rememberDynamicMaterialThemeState(
            seedColor = Color(seedColor),
            isDark = isDarkTheme,
            isAmoled = isAmoled,
        )

    CosmicTheme(state) {
        KoinContext {
            Scaffold { innerPadding ->
                Surface(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainAppNavHost()
                }
            }
        }
    }
}

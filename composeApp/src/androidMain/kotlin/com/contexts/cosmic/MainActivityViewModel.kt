package com.contexts.cosmic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val mainViewModel: MainViewModel,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            mainViewModel.authState.map { it is AuthenticationState.Loading }
                .collect { _isLoading.value = it }
        }
    }
}

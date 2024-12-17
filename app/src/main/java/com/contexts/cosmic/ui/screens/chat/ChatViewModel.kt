/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chat.bsky.convo.ConvoView
import chat.bsky.convo.ListConvosQueryParams
import com.contexts.cosmic.data.network.client.onError
import com.contexts.cosmic.data.network.client.onSuccess
import com.contexts.cosmic.domain.repository.ChatRepository
import com.contexts.cosmic.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val chats: List<ConvoView> = emptyList(),
    val userDid: String? = null,
    val loading: Boolean = false,
    val error: String? = null,
)

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadConvos()
    }

    fun refreshConvos() {
        loadConvos()
    }

    private fun loadConvos() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            val current = preferencesRepository.getCurrentUserFlow().first()
            chatRepository.listConvos(listConvosQueryParams = ListConvosQueryParams()).onSuccess { response ->
                _uiState.update { it.copy(chats = response.convos, userDid = current, loading = false) }
            }.onError { error ->
                _uiState.update { it.copy(loading = false, error = error.message) }
            }
        }
    }
}

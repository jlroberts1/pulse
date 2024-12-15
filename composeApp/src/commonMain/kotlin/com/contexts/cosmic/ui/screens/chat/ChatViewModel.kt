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
import com.contexts.cosmic.data.network.httpclient.AuthManager
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.domain.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val chats: List<ConvoView> = emptyList(),
    val userDid: String? = null,
    val loading: Boolean = false,
    val error: String? = null,
)

class ChatViewModel(
    private val authManager: AuthManager,
    private val chatRepository: ChatRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadConvos()
    }

    fun loadConvos() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            val authState = authManager.getAuthState()
            val serviceDid = authState?.didDocument?.service?.firstOrNull()
            serviceDid?.serviceEndpoint?.let {
                when (
                    val response =
                        chatRepository.listConvos(
                            serviceDid.serviceEndpoint,
                            ListConvosQueryParams(),
                        )
                ) {
                    is Response.Success -> {
                        _uiState.update {
                            it.copy(
                                chats = response.data.convos,
                                userDid = authState.userDid,
                                loading = false,
                                error = null,
                            )
                        }
                    }

                    is Response.Error -> {
                        _uiState.update { it.copy(loading = false, error = response.error.message) }
                    }
                }
            } ?: _uiState.update { it.copy(loading = false, error = "Unable to get messages") }
        }
    }
}

/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.components

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlayerUiState(
    val loading: Boolean = false,
    val exoPlayer: ExoPlayer? = null,
)

class PlayerViewModel(
    private val exoPlayer: ExoPlayer,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState = _uiState.asStateFlow()

    private val playerListener =
        object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                handleError(error)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _uiState.update { it.copy(loading = playbackState == Player.STATE_BUFFERING) }
            }
        }

    init {
        exoPlayer.addListener(playerListener)
    }

    fun initializePlayer(videoUri: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true, exoPlayer = null) }
                exoPlayer.stop()
                exoPlayer.clearMediaItems()

                val mediaItem = MediaItem.fromUri(Uri.parse(videoUri))
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
                _uiState.update { it.copy(loading = false, exoPlayer = exoPlayer) }
            } catch (e: Exception) {
                Log.e("PlayerViewModel", "Error initializing the player")
            }
        }
    }

    private fun releasePlayer() {
        try {
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            exoPlayer.removeListener(playerListener)
            exoPlayer.release()
            _uiState.update { it.copy(exoPlayer = null) }
        } catch (e: Exception) {
            Log.e("PlayerViewModel", "Error releasing the player")
        }
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    private fun handleError(error: PlaybackException) {
        Log.e("PlayerViewModel", "Error playing media, ${error.message}")
    }
}

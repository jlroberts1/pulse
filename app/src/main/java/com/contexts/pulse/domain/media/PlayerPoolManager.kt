/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.media

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerPoolManager(
    private val context: Context,
    poolSize: Int = 3,
) {
    private val players =
        List(poolSize) {
            ExoPlayer.Builder(context)
                .setHandleAudioBecomingNoisy(true)
                .build().apply {
                    volume = 0f
                    repeatMode = Player.REPEAT_MODE_ONE
                }
        }

    private val availablePlayers = MutableStateFlow(players.toList())
    private val _noPlayersAvailable = MutableStateFlow(false)
    val noPlayersAvailable = _noPlayersAvailable.asStateFlow()

    fun getPlayer(): ExoPlayer? {
        return availablePlayers.value.firstOrNull()?.also { player ->
            availablePlayers.value -= player
            _noPlayersAvailable.value = availablePlayers.value.isEmpty()
        }
    }

    fun releasePlayer(player: ExoPlayer) {
        player.stop()
        player.clearMediaItems()
        availablePlayers.value += player
        _noPlayersAvailable.value = false
    }

    fun releaseAll() {
        players.forEach { player ->
            player.stop()
            player.clearMediaItems()
            player.release()
        }
        availablePlayers.value = emptyList()
        _noPlayersAvailable.value = true
    }
}

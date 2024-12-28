/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.modules

import androidx.lifecycle.SavedStateHandle
import androidx.media3.exoplayer.ExoPlayer
import androidx.work.WorkManager
import com.contexts.pulse.domain.repository.ActorRepository
import com.contexts.pulse.domain.repository.AuthenticateRepository
import com.contexts.pulse.domain.repository.ChatRepository
import com.contexts.pulse.domain.repository.FeedRepository
import com.contexts.pulse.domain.repository.NotificationsRepository
import com.contexts.pulse.domain.repository.PendingUploadRepository
import com.contexts.pulse.domain.repository.PostRepository
import com.contexts.pulse.domain.repository.PreferencesRepository
import com.contexts.pulse.domain.repository.ProfileRepository
import com.contexts.pulse.domain.repository.TenorRepository
import com.contexts.pulse.domain.repository.UserRepository
import com.contexts.pulse.ui.components.PlayerViewModel
import com.contexts.pulse.ui.screens.addpost.AddPostViewModel
import com.contexts.pulse.ui.screens.chat.ChatViewModel
import com.contexts.pulse.ui.screens.home.HomeViewModel
import com.contexts.pulse.ui.screens.login.LoginViewModel
import com.contexts.pulse.ui.screens.main.AppViewModel
import com.contexts.pulse.ui.screens.notifications.NotificationViewModel
import com.contexts.pulse.ui.screens.postview.PostViewModel
import com.contexts.pulse.ui.screens.profile.ProfileViewModel
import com.contexts.pulse.ui.screens.search.SearchViewModel
import com.contexts.pulse.ui.screens.settings.SettingsViewModel
import com.contexts.pulse.worker.UploadWorkerManager
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModel {
            AddPostViewModel(
                get<ActorRepository>(),
                get<TenorRepository>(),
                get<PreferencesRepository>(),
                get<PendingUploadRepository>(),
                get<UploadWorkerManager>(),
                get<PostRepository>(),
                get(),
            )
        }
        viewModel {
            AppViewModel(
                get<FeedRepository>(),
                get<ProfileRepository>(),
                get<PreferencesRepository>(),
                get<UserRepository>(),
                get<WorkManager>(),
            )
        }
        viewModel { ChatViewModel(get<ChatRepository>(), get<PreferencesRepository>()) }
        viewModel {
            HomeViewModel(
                get<FeedRepository>(),
                get<PreferencesRepository>(),
                get<UserRepository>(),
            )
        }
        viewModel {
            LoginViewModel(
                get<AuthenticateRepository>(),
            )
        }
        viewModel {
            NotificationViewModel(get<NotificationsRepository>(), get<PreferencesRepository>())
        }
        viewModel { ProfileViewModel(get<ProfileRepository>()) }
        viewModel { SearchViewModel(get<ActorRepository>(), get<FeedRepository>()) }
        viewModel { SettingsViewModel(get<PreferencesRepository>()) }
        viewModel { PlayerViewModel(get<ExoPlayer>()) }
        viewModel { PostViewModel(get<SavedStateHandle>(), get<PostRepository>()) }
    }

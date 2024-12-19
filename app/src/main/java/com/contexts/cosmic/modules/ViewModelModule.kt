/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.modules

import com.contexts.cosmic.ui.components.PlayerViewModel
import com.contexts.cosmic.ui.screens.addpost.AddPostViewModel
import com.contexts.cosmic.ui.screens.chat.ChatViewModel
import com.contexts.cosmic.ui.screens.home.HomeViewModel
import com.contexts.cosmic.ui.screens.login.LoginViewModel
import com.contexts.cosmic.ui.screens.main.AppViewModel
import com.contexts.cosmic.ui.screens.notifications.NotificationViewModel
import com.contexts.cosmic.ui.screens.profile.ProfileViewModel
import com.contexts.cosmic.ui.screens.search.SearchViewModel
import com.contexts.cosmic.ui.screens.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModel { AddPostViewModel(get(), get()) }
        viewModel { AppViewModel(get(), get(), get()) }
        viewModel { ChatViewModel(get(), get()) }
        viewModel { HomeViewModel(get(), get()) }
        viewModel { LoginViewModel(get()) }
        viewModel { NotificationViewModel(get(), get()) }
        viewModel { ProfileViewModel(get()) }
        viewModel { SearchViewModel(get(), get()) }
        viewModel { SettingsViewModel(get()) }
        viewModel { PlayerViewModel(get()) }
    }

/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.modules

import com.contexts.pulse.ui.components.PlayerViewModel
import com.contexts.pulse.ui.screens.addpost.AddPostViewModel
import com.contexts.pulse.ui.screens.chat.ChatViewModel
import com.contexts.pulse.ui.screens.home.HomeViewModel
import com.contexts.pulse.ui.screens.login.LoginViewModel
import com.contexts.pulse.ui.screens.main.AppViewModel
import com.contexts.pulse.ui.screens.notifications.NotificationViewModel
import com.contexts.pulse.ui.screens.profile.ProfileViewModel
import com.contexts.pulse.ui.screens.search.SearchViewModel
import com.contexts.pulse.ui.screens.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModel { AddPostViewModel(get(), get()) }
        viewModel { AppViewModel(get(), get(), get(), get()) }
        viewModel { ChatViewModel(get(), get()) }
        viewModel { HomeViewModel(get(), get()) }
        viewModel { LoginViewModel(get()) }
        viewModel { NotificationViewModel(get(), get()) }
        viewModel { ProfileViewModel(get()) }
        viewModel { SearchViewModel(get(), get()) }
        viewModel { SettingsViewModel(get()) }
        viewModel { PlayerViewModel(get()) }
    }

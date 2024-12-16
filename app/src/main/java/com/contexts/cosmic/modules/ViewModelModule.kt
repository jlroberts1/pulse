/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.modules

import com.contexts.cosmic.ui.screens.home.HomeViewModel
import com.contexts.cosmic.ui.screens.login.LoginViewModel
import com.contexts.cosmic.ui.screens.main.AppViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModel { AppViewModel(get()) }
        viewModel { LoginViewModel(get()) }
        viewModel { HomeViewModel(get()) }
    }

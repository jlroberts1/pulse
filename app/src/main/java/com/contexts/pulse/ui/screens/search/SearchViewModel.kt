/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.contexts.pulse.domain.repository.ActorRepository
import com.contexts.pulse.domain.repository.FeedRepository

class SearchViewModel(
    actorRepository: ActorRepository,
    feedRepository: FeedRepository,
) : ViewModel() {
    val suggestedAccounts =
        actorRepository.getSuggestions()
            .cachedIn(viewModelScope)

    val suggestedFeeds =
        feedRepository.getSuggestions()
            .cachedIn(viewModelScope)
}

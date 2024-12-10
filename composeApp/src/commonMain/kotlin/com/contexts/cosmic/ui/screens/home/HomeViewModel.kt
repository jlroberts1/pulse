package com.contexts.cosmic.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.domain.model.FeedViewPost
import com.contexts.cosmic.domain.repository.FeedRepository
import com.contexts.cosmic.exceptions.AppError
import com.contexts.cosmic.extensions.RequestResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val feedRepository: FeedRepository,
) : ViewModel() {
    private val _feed =
        MutableStateFlow<RequestResult<List<FeedViewPost>, AppError>>(RequestResult.Loading)
    val feed = _feed.asStateFlow()

    init {
        loadFeed()
    }

    private fun loadFeed() {
        viewModelScope.launch {
            _feed.value = RequestResult.Loading
            when (val response = feedRepository.getTimeline()) {
                is Response.Success -> {
                    _feed.value = RequestResult.Success(response.data.feed)
                }

                is Response.Error -> {
                    _feed.value = RequestResult.Error(response.error)
                }
            }
        }
    }
}

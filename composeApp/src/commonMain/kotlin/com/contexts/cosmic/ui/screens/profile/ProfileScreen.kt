package com.contexts.cosmic.ui.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.contexts.cosmic.domain.model.FeedViewPost
import com.contexts.cosmic.exceptions.AppError
import com.contexts.cosmic.extensions.RequestResult
import com.contexts.cosmic.ui.composables.ErrorView
import com.contexts.cosmic.ui.composables.FeedItem
import com.contexts.cosmic.ui.composables.Loading
import com.contexts.cosmic.ui.screens.profile.composables.Header
import com.contexts.cosmic.ui.screens.profile.composables.ProfileInfo
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen() {
    val viewModel: ProfileViewModel = koinViewModel()
    val profile = viewModel.profile.collectAsState(RequestResult.Loading)
    val feed = viewModel.feed.collectAsState(RequestResult.Loading)
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            item { Header(profile.value) }
            item { ProfileInfo(profile.value) }
            item { FeedView(feed.value) }
        }
    }
}

@Composable
fun FeedView(feed: RequestResult<List<FeedViewPost>, AppError>) {
    when (feed) {
        is RequestResult.Loading -> {
            Loading()
        }
        is RequestResult.Error -> {
            ErrorView(feed.error.message)
        }
        is RequestResult.Success -> {
            feed.data.forEach { post ->
                FeedItem(post)
            }
        }
    }
}

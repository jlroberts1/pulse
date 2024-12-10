package com.contexts.cosmic.ui.screens.home

import androidx.compose.foundation.layout.Column
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
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = koinViewModel()
    val feed = viewModel.feed.collectAsState(RequestResult.Loading)
    Column(modifier = Modifier.fillMaxSize()) {
        FeedView(feed.value)
    }
}

@Composable
fun FeedView(feed: RequestResult<List<FeedViewPost>, AppError>) {
    LazyColumn {
        when (feed) {
            is RequestResult.Loading -> {
                item { Loading() }
            }
            is RequestResult.Error -> {
                item { ErrorView(feed.error.message) }
            }
            is RequestResult.Success -> {
                items(feed.data.size) {
                    feed.data.forEach { post ->
                        FeedItem(post)
                    }
                }
            }
        }
    }
}

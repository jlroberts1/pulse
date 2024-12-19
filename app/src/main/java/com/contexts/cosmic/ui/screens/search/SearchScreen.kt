/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.bsky.actor.ProfileView
import com.contexts.cosmic.ui.composables.BorderedCircularAvatar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScreen() {
    val viewModel: SearchViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(modifier = Modifier.fillMaxSize()) {
        SearchBox(
            query = "",
            onQueryChange = {},
            onSearch = { },
        )
        LazyColumn {
            item { SuggestedAccountHeader() }
            item { SuggestedAccountCarousel(items = uiState.suggestedUsers) }
            item { SuggestedFeedHeader() }
            items(uiState.suggestedFeeds) { feed ->
                SuggestedFeedItem(
                    feed.avatar?.uri,
                    feed.displayName,
                    feed.creator.handle.handle,
                    feed.description,
                    feed.likeCount,
                )
            }
        }
    }
}

@Composable
fun SearchBox(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "Search",
    onSearch: () -> Unit = {},
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(16.dp),
        placeholder = {
            Text(
                text = hint,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(24.dp),
        keyboardOptions =
            KeyboardOptions(
                imeAction = ImeAction.Search,
            ),
        keyboardActions =
            KeyboardActions(
                onSearch = { onSearch() },
            ),
    )
}

@Composable
fun SuggestedFeedItem(
    avatar: String?,
    feedTitle: String,
    feedAuthor: String,
    feedDescription: String?,
    likeCount: Long?,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                BorderedCircularAvatar(
                    imageUri = avatar,
                    modifier = Modifier.size(48.dp),
                )

                Column(
                    modifier =
                        Modifier
                            .padding(start = 12.dp)
                            .weight(1f),
                ) {
                    Text(
                        text = feedTitle,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = feedAuthor,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            IconButton(
                onClick = {},
                modifier = Modifier.padding(start = 8.dp),
            ) {
                Icon(Icons.Filled.Add, "Add feed")
            }
        }

        feedDescription?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        likeCount?.let {
            Text(
                text = "Liked by $it users",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview
@Composable
fun SuggestedAccountHeader() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary,
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Account",
            )
            Text(
                text = "Suggested accounts",
                style = MaterialTheme.typography.headlineSmall,
            )
        }
    }
}

@Preview
@Composable
fun SuggestedFeedHeader() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary,
                imageVector = Icons.Default.DynamicFeed,
                contentDescription = "Feeds",
            )
            Text(
                text = "Suggested feeds",
                style = MaterialTheme.typography.headlineSmall,
            )
        }
    }
}

@Composable
fun SuggestedAccountCarousel(items: List<ProfileView>) {
    LazyRow(
        modifier =
            Modifier
                .height(240.dp)
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(items) { item ->
            ElevatedCard(
                modifier =
                    Modifier
                        .height(240.dp)
                        .width(240.dp),
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BorderedCircularAvatar(
                            imageUri = item.avatar?.uri,
                            size = 48.dp,
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            item.displayName?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            Text(
                                text = item.handle.handle,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp),
                            maxLines = 4,
                            text = item.description ?: "",
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Button(onClick = {}) {
                            Text("Follow")
                        }
                    }
                }
            }
        }
    }
}

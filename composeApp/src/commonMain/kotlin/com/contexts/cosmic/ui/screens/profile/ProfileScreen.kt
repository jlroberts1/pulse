package com.contexts.cosmic.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen() {

    val viewModel: ProfileViewModel = koinViewModel()

    val uiState = viewModel.uiState.collectAsState()
    val isLoading = uiState.value.isLoading
    val profile = uiState.value.profile

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading && profile == null) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
            )
        } else if (profile != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with avatar and banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        //.background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    profile.avatar?.let { avatarUrl ->
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Profile avatar",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                //.border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                .align(Alignment.BottomStart)
                                .offset(x = 16.dp, y = 40.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Profile info
                Spacer(modifier = Modifier.height(48.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = profile.displayName ?: profile.handle,
                        //style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "@${profile.handle}",
                        //style = MaterialTheme.typography.bodyMedium,
                        //color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    profile.description?.let { description ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = description,
                            //style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Column {
                            Text(
                                text = profile.postsCount.toString(),
                                //style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Posts",
                                //style = MaterialTheme.typography.bodyMedium,
                                //color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column {
                            Text(
                                text = profile.followersCount.toString(),
                                //style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Followers",
                                //style = MaterialTheme.typography.bodyMedium,
                               // color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column {
                            Text(
                                text = profile.followsCount.toString(),
                                //style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Following",
                                //style = MaterialTheme.typography.bodyMedium,
                                //color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Pull to refresh
        val pullRefreshState = rememberPullRefreshState(
            refreshing = isLoading,
            onRefresh = { viewModel.refresh() }
        )
        PullRefreshIndicator(
            refreshing = isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
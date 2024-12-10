package com.contexts.cosmic.ui.screens.profile.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.contexts.cosmic.domain.model.User
import com.contexts.cosmic.exceptions.AppError
import com.contexts.cosmic.extensions.RequestResult
import com.contexts.cosmic.ui.composables.Loading
import sh.calvin.autolinktext.AutoLinkText

@Composable
fun ProfileInfo(result: RequestResult<User, AppError>) {
    when (result) {
        is RequestResult.Loading -> {
            Loading()
        }

        is RequestResult.Error -> {}

        is RequestResult.Success -> {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
            ) {
                result.data.description?.let { description ->
                    Spacer(modifier = Modifier.height(8.dp))
                    AutoLinkText(
                        text = description,
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    Column {
                        Text(
                            text = result.data.postsCount.toString(),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "Posts",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Column {
                        Text(
                            text = result.data.followersCount.toString(),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "Followers",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Column {
                        Text(
                            text = result.data.followsCount.toString(),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "Following",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

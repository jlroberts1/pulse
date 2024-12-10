package com.contexts.cosmic.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contexts.cosmic.data.network.httpclient.AuthManager
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.domain.model.FeedViewPost
import com.contexts.cosmic.domain.repository.ProfileRepository
import com.contexts.cosmic.exceptions.AppError
import com.contexts.cosmic.extensions.RequestResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    authManager: AuthManager,
) : ViewModel() {
    private val authState =
        authManager.getAuthState().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            null,
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val profile =
        authManager.getAuthState().mapNotNull { it }.flatMapLatest { authState ->
            profileRepository.getMyProfile(authState.userDid)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val feed =
        authState.mapNotNull { it }.flatMapLatest { authState ->
            getFeedFlow(authState.userDid)
        }

    private fun getFeedFlow(userDid: String): Flow<RequestResult<List<FeedViewPost>, AppError>> =
        flow {
            when (val response = profileRepository.getProfileFeed(userDid)) {
                is Response.Success -> {
                    emit(RequestResult.Success(response.data.feed))
                }

                is Response.Error -> {
                    emit(RequestResult.Error(response.error))
                }
            }
        }
}

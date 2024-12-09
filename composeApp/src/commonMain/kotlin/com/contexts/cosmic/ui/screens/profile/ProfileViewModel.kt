package com.contexts.cosmic.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.contexts.cosmic.data.network.httpclient.AuthManager
import com.contexts.cosmic.domain.repository.ProfileRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    authManager: AuthManager,
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val profile =
        authManager.getAuthState().flatMapLatest { authState ->
            if (authState != null) {
                profileRepository.getMyProfile(authState.userDid)
            } else {
                flowOf()
            }
        }
}

package com.contexts.cosmic.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.data.network.httpclient.toErrorMessage
import com.contexts.cosmic.data.network.model.response.ProfileViewResponse
import com.contexts.cosmic.domain.repository.PreferencesRepository
import com.contexts.cosmic.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val profile: ProfileViewResponse? = null,
    val isLoading: Boolean = false

)
class ProfileViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            preferencesRepository.getUserInfo().collect { userInfo ->
                userInfo?.did?.let {
                    when(val response = profileRepository.getMyProfile(it)) {
                        is Response.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    profile = response.data
                                )
                            }
                        }
                        is Response.Error -> {
                            print(response.error.toErrorMessage())
                        }
                    }
                }
            }
        }


    }

    fun refresh() {

    }
}
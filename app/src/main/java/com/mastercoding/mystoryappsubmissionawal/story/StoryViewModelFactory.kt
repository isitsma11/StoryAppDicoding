package com.mastercoding.mystoryappsubmissionawal.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mastercoding.mystoryappsubmissionawal.api.ApiService

class StoryViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

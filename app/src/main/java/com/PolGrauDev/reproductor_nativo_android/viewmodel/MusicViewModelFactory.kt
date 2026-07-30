package com.PolGrauDev.reproductor_nativo_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.PolGrauDev.reproductor_nativo_android.App

class MusicViewModelFactory(private val app: App) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(MusicViewModel::class.java)) {
            "Unknown ViewModel class: $modelClass"
        }
        return MusicViewModel(app.mediaRepository, app.playlistRepository, app.applicationContext) as T
    }
}

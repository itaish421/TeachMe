package com.example.teachme

sealed class LoadingState {
    data object Loaded: LoadingState()
    data object Loading: LoadingState()
}
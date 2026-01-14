package com.smartchip.aidrink.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TestVM : ViewModel() {
    init {
        viewModelScope.launch {
            println("Agora funciona")
        }
    }
}
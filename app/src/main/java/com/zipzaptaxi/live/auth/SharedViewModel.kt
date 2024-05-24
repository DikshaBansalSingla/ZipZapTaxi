package com.zipzaptaxi.live.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    val isDrawerOpen = MutableLiveData<Boolean>()

    fun setDrawerOpen(isOpen: Boolean) {
        isDrawerOpen.value = isOpen
    }
}
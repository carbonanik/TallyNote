package com.carbondev.tallynote.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.carbondev.tallynote.repository.AuthRepository

class SplashViewModel : ViewModel() {
    private val authRepository = AuthRepository

    val loggedIn : LiveData<Boolean>
        get() = authRepository.loggedIn

    fun checkIfLoggedIn(){
        authRepository.checkIfLoggedIn()
    }

}
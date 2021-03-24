package com.carbondev.tallynote.view.viewmodel

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carbondev.tallynote.repository.AuthRepository
import com.hbb20.CountryCodePicker

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository

    val loginNumber = MutableLiveData<String>().apply { value = "" }
    val loginPassword = MutableLiveData<String>().apply { value = "" }

    val info = MutableLiveData<String>()
//    val authInfo : LiveData<String>
//        get() = authRepository.authInfo

    val loginSuccessful : LiveData<Boolean>
        get() = authRepository.loginSuccessful

    val logInErrorMassage : LiveData<String>
        get() = AuthRepository.logInErrorMassage

    private val _buttonEnabled = MutableLiveData<Boolean>()
    val buttonEnabled: LiveData<Boolean>
        get() = _buttonEnabled

    val processingLogin = MutableLiveData<Boolean>().apply { value = false }

    var ccp : CountryCodePicker? = null

    val onViewPasswordClick = MutableLiveData<Boolean>().apply { value = false }

    fun onLoginPress(){
        if (loginPassword.value!!.length >= 6 ){
            processingLogin.value = true
            authRepository.signInWithEmailPassword(fakeEmail(), loginPassword.value!!)
        } else {
            info.value = "Password is too Short"
        }
    }

    fun onViewPasswordClick(){
        onViewPasswordClick.value = !onViewPasswordClick.value!!
    }

    fun validateInput() {
        _buttonEnabled.value = loginNumber.value!!.isNotEmpty() && loginPassword.value!!.isNotEmpty() && !processingLogin.value!!
    }

    private fun fakeEmail(): String {
        return ccp!!.selectedCountryCodeWithPlus + loginNumber.value + "@carbondev.com"
    }

    fun clearAllAuthVariable() {
        authRepository.clearAllAuthVariable()
    }
}
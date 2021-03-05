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

    var ccp : CountryCodePicker? = null

    val onSignInButtonClick = MutableLiveData<Boolean>()
    val onViewPasswordClick = MutableLiveData<Boolean>().apply { value = false }


    fun onLoginPress(){
        onSignInButtonClick.value = true
        if (loginPhoneNumberValid() && !loginPassword.value.isNullOrEmpty() && loginPassword.value!!.length >= 6 ){
            authRepository.signInWithEmailPassword(fakeEmail(), loginPassword.value!!)

        } else {

            if (loginPassword.value.isNullOrEmpty() || loginNumber.value.isNullOrEmpty()){
                info.value = "Fill up Phone and Password Field"
            } else if (loginPassword.value!!.length <= 6) {
                info.value = "Password is too Short"
            } else if (!loginPhoneNumberValid()) {
                info.value = "Invalid Phone Number"
            } else {
                info.value = "Fill up Correctly"
            }
        }
    }

    fun onViewPasswordClick(){
        onViewPasswordClick.value = !onViewPasswordClick.value!!
    }

    private fun loginPhoneNumberValid(): Boolean {
        return loginNumber.value!!.isNotEmpty() && loginNumber.value!!.length <= 11 && loginNumber.value!!.isDigitsOnly()
    }

    fun validateInput() {
        _buttonEnabled.value = !(loginNumber.value!!.isEmpty() || loginPassword.value!!.isEmpty())
    }

    private fun fakeEmail(): String {
        return ccp!!.selectedCountryCodeWithPlus + loginNumber.value + "@carbondev.com"
    }

    fun clearAllAuthVariable() {
        authRepository.clearAllAuthVariable()
    }
}
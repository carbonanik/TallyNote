package com.carbondev.tallynote.view.viewmodel

import android.app.Activity
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carbondev.tallynote.repository.AuthRepository
import com.carbondev.tallynote.repository.FirebaseDataRepository
import com.hbb20.CountryCodePicker

class SignupViewModel : ViewModel() {

    private val authRepository = AuthRepository
    private val remoteData = FirebaseDataRepository

    var phoneNumberWithCountryCode : String = ""
    var ccp : CountryCodePicker? = null

    val userName = MutableLiveData<String>().apply { value = "" }
    val phoneNumber = MutableLiveData<String>().apply { value = "" }
    val password = MutableLiveData<String>().apply { value = "" }
    val conPassword = MutableLiveData<String>().apply { value = "" }
    val info = MutableLiveData<String>().apply { value = "" }

    val accountExist = authRepository.accountExist

    val storedVerificationId : LiveData<String>
        get() = authRepository.storedVerificationId

    val buttonEnabled = MutableLiveData<Boolean>().apply { value = false }
    val processingSignUp = MutableLiveData<Boolean>().apply { value = false }
    val onViewPasswordClick = MutableLiveData<Boolean>().apply { value = false }

    fun onRegisterButtonClick(){

        if (phoneNumberValid() && !password.value.isNullOrBlank() && password.value!!.length >= 6 && conPassword.value!! == password.value!! && !userName.value.isNullOrBlank()){
            processingSignUp.value = true
            phoneNumberWithCountryCode = ccp!!.selectedCountryCodeWithPlus + phoneNumber.value

            info.value = "Please Wait For Verification Code"
            authRepository.accountExist(phoneNumberWithCountryCode)

        } else {
            processingSignUp.value = false
            if (userName.value.isNullOrBlank()){
                info.value = "Name is Empty!"

            } else if (!phoneNumberValid()){
                info.value = "Phone Number Invalid!"

            } else if (password.value.isNullOrBlank()){
                info.value = "Your Password is Empty!"

            } else if (password.value!!.length < 6){
                info.value = "Password is Too Short!"

            } else if (password.value!! != conPassword.value!!){
                info.value = "Password Didn't match!"

            }
        }
    }

    private fun phoneNumberValid(): Boolean {
        return if (ccp!!.selectedCountryCodeWithPlus == "+880") {
            phoneNumber.value?.length == 10
        } else {
            !phoneNumber.value.isNullOrEmpty() && phoneNumber.value!!.isDigitsOnly() //&& phoneNumber.value!!.length >= 11
        }
    }

    fun verifyNumber(activity: Activity){
        authRepository.verifyNumber(phoneNumberWithCountryCode, activity)
    }

    fun onViewPasswordClick(){
        onViewPasswordClick.value = !onViewPasswordClick.value!!
    }

    fun clearAuthVariable(){
        authRepository.clearAllAuthVariable()
    }
}
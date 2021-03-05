package com.carbondev.tallynote.view.viewmodel

import android.app.Activity
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carbondev.tallynote.repository.AuthRepository
import com.carbondev.tallynote.repository.FirebaseDataRepository
import com.hbb20.CountryCodePicker

class ForgrtPasswordViewModel : ViewModel() {

    private val authRepository = AuthRepository
    private val remoteData = FirebaseDataRepository

    var ccp : CountryCodePicker? = null

    private var fullPhoneNumber : String = ""

    val forgetNumber = MutableLiveData<String>()
    val forgetPassword = MutableLiveData<String>()

    var receivedCode = MutableLiveData<String>()

    val info = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()

    val onViewPasswordClick = MutableLiveData<Boolean>()
    val onSendCodeButtonClick = MutableLiveData<Boolean>()
//    val verifyResetCodeButtonClicks = MutableLiveData<Boolean>()


    val numberExists : LiveData<Boolean>
        get() = authRepository.numberExists

    val storedVerificationId : LiveData<String>
        get() = authRepository.storedVerificationId

    val phoneResistrationSuccessful : LiveData<Boolean>
        get() = authRepository.phoneRegistrationSuccessful

    val passwordChanged : LiveData<Boolean>
        get() = authRepository.passwordChanged

    fun onSendCodeButtonClick(){
        isLoading.value = true
        if ( !forgetPassword.value.isNullOrBlank() && forgetPassword.value!!.length >= 6 && !forgetNumber.value.isNullOrBlank()){

            //add prefix to number from user
            fullPhoneNumber = ccp!!.selectedCountryCodeWithPlus + forgetNumber.value
            info.value = "Please Wait For Verification Code"
            authRepository.numberExists(fullPhoneNumber)

        } else {
            isLoading.value = false
            if (forgetNumber.value.isNullOrBlank()){
                info.value = "Number is Empty!"
            }
            else if (!phoneNumberValid()){
                info.value = "Phone Number Invalid!"
            } else if (forgetPassword.value.isNullOrBlank()){
                info.value = "Your Password is Empty!"

            } else if (forgetPassword.value!!.length < 6){
                info.value = "Password is Too Short!"
            }
        }
    }

    private fun phoneNumberValid(): Boolean {
        return if (ccp!!.selectedCountryCodeWithPlus == "+880") {
            forgetNumber.value?.length == 10
        } else {
            !forgetNumber.value.isNullOrEmpty() && forgetNumber.value!!.isDigitsOnly()
        }
    }

    fun verifyNumber(activity: Activity){
        authRepository.verifyNumber(fullPhoneNumber, activity)
    }
}
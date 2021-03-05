package com.carbondev.tallynote.view.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carbondev.tallynote.repository.AuthRepository
import com.carbondev.tallynote.repository.FirebaseDataRepository

class VerifyNumberViewModel : ViewModel() {

    private val authRepository = AuthRepository
    private val remoteData = FirebaseDataRepository

    val info = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()
    val receivedCode = MutableLiveData<String>()
//    val verifySignInCodeButtonClicks = MutableLiveData<Boolean>().apply { value = false }

    val phoneRegistrationSuccessful : LiveData<Boolean>
        get() = authRepository.phoneRegistrationSuccessful

    val codeNotMatch : LiveData<Boolean>
        get() = authRepository.codeNotMatch

    val verificationFaild : LiveData<Boolean>
        get() = authRepository.verificationFaild

    val linkEmailPasswordSuccessful : LiveData<Boolean>
        get() = authRepository.linkEmailPasswordSuccessful

    var verifyForSignUp = true

//    fun verifySignInCodeButtonClicks(){
//        verifySignInCodeButtonClicks.value = true
//    }

    fun signInWithPhone(code : String?, activity : Activity){

        if (receivedCode.value.isNullOrEmpty()){
            //empty
            info.value = "Put Your Verification Code!"

        } else {
            isLoading.value = true
            info.value = "Please wait For Verification Complete"
            // sign in with phone number credential
            authRepository.signInWithPhoneAuthCredential(code!!, activity)
        }
    }

    fun linkEmailPasswordLogin(phone : String, password : String, activity : Activity){
        authRepository.linkEmailPasswordLogin(phone, password, activity)
    }

    fun allSuccess(name : String, phone : String){
        remoteData.saveNameToFirebase(name)
        remoteData.saveThisNumberToServer(phone)
    }

    fun changeEmailPassword(password: String) {
        authRepository.changeEmailPassword(password)
    }
}















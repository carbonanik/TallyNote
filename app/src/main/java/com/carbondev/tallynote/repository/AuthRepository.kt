package com.carbondev.tallynote.repository

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.TimeUnit

object AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    private val mNumberExists = MutableLiveData<Boolean>()
    val numberExists : LiveData<Boolean>
        get() = mNumberExists

    private val mLoginSuccessful = MutableLiveData<Boolean>()
    val loginSuccessful : LiveData<Boolean>
        get() = mLoginSuccessful

    private val mStoredVerificationId = MutableLiveData<String>()
    val storedVerificationId : LiveData<String>
        get() = mStoredVerificationId

    private val mPhoneRegistrationSuccessful = MutableLiveData<Boolean>()
    val phoneRegistrationSuccessful : LiveData<Boolean>
        get() = mPhoneRegistrationSuccessful

    private val mCodeNotMatch = MutableLiveData<Boolean>()
    val codeNotMatch : LiveData<Boolean>
        get() = mCodeNotMatch

    private val mVerificationFaild = MutableLiveData<Boolean>()
    val verificationFaild : LiveData<Boolean>
        get() = mVerificationFaild

    private val mLinkEmailPasswordSuccessful = MutableLiveData<Boolean>()
    val linkEmailPasswordSuccessful : LiveData<Boolean>
        get() = mLinkEmailPasswordSuccessful

    private val mPasswordChanged = MutableLiveData<Boolean>()
    val passwordChanged : LiveData<Boolean>
        get() = mPasswordChanged

    private val mLoggedIn = MutableLiveData<Boolean>()
    val loggedIn : LiveData<Boolean>
        get() = mLoggedIn

    private val mLogInErrorMassage = MutableLiveData<String>()
    val logInErrorMassage : LiveData<String>
        get() = mLogInErrorMassage

    private val mRetrieveSmsCode = MutableLiveData<String>()
    val retrieveSmsCode : LiveData<String>
        get() = mRetrieveSmsCode


    fun numberExists(fullPhoneNumber : String) {

        val ref = FirebaseDatabase.getInstance().reference.child("applicationPublic").child("phoneNumber")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mNumberExists.value = dataSnapshot.hasChild(fullPhoneNumber)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                mNumberExists.value = false
            }
        })
    }

    fun signInWithEmailPassword(email : String, password : String){

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                mLoginSuccessful.value = task.isSuccessful
            }
            .addOnFailureListener {
                mLogInErrorMassage.value = it.message
            }
    }

    fun getUid(): String {
        return auth.currentUser!!.uid
    }

    fun verifyNumber(fullPhoneNumber: String, activity : Activity){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            fullPhoneNumber,         // Phone number to verify
            60,                 // Timeout duration
            TimeUnit.SECONDS,   // Unit of timeout
            activity, // Activity (for callback binding)
            callbacks)          // OnVerificationStateChangedCallbacks
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            val smsCode = credential.smsCode
            if (smsCode != null){
                mRetrieveSmsCode.value = smsCode
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {

            mVerificationFaild.value = true
//            println("callback")
//            println(e.message)
//            mAuthInfo.value = e.message // todo
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            mStoredVerificationId.value = verificationId
        }
    }

    fun signInWithPhoneAuthCredential(receivedCode : String, activity : Activity) {

        val credential = PhoneAuthProvider.getCredential(storedVerificationId.value!!, receivedCode)


        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    mPhoneRegistrationSuccessful.value = true

                } else {
                    // invalid credential // verification code dose not match
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        mCodeNotMatch.value = true

                    }
                }
            }
    }

    fun linkEmailPasswordLogin(fullPhoneNumber: String, password: String, activity: Activity){

        val email = "$fullPhoneNumber@carbondev.com"

        val emailCredential = EmailAuthProvider.getCredential(email, password)
        FirebaseAuth.getInstance().currentUser?.linkWithCredential(emailCredential)
            ?.addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {

                    loginWithEmailCredential(emailCredential)
                } else {
                    mVerificationFaild.value = true
//                    println("linkEmailPasswordLogin")
                }
            }
    }

    private fun loginWithEmailCredential(credential: AuthCredential){
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnSuccessListener {
                mLinkEmailPasswordSuccessful.value = true

            }
            .addOnFailureListener {
                mVerificationFaild.value = true
//                println("loginWithEmailCredential")
            }
    }

    fun changeEmailPassword(password : String) {
        val user = FirebaseAuth.getInstance().currentUser

        user!!.updatePassword(password).addOnCompleteListener {
            if (it.isSuccessful){
                mPasswordChanged.value = true
            }
        }
    }

    fun checkIfLoggedIn() {
        mLoggedIn.value = FirebaseAuth.getInstance().currentUser != null
    }

    fun clearAllAuthVariable() {
        mNumberExists.value = false
        mLoginSuccessful.value = false
        mStoredVerificationId.value = ""
        mPhoneRegistrationSuccessful.value = false
        mCodeNotMatch.value = false
        mVerificationFaild.value = false
        mLinkEmailPasswordSuccessful.value = false
        mPasswordChanged.value = false
        mLoggedIn.value = false
        mLogInErrorMassage.value = ""
        mRetrieveSmsCode.value = ""
    }
}
package com.carbondev.tallynote.repository

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit


object AuthRepository {

    private val auth = FirebaseAuth.getInstance()

//    private val mNumberExists = MutableLiveData<Boolean>()
//    val numberExists : LiveData<Boolean>
//        get() = mNumberExists

    private val mAccountExist = MutableLiveData<Boolean>()
    val accountExist: LiveData<Boolean>
        get() = mAccountExist

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


//    fun numberExists(fullPhoneNumber : String) {
//
//        val ref = FirebaseDatabase.getInstance().reference.child("applicationPublic").child("phoneNumber")
//        ref.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                mNumberExists.value = dataSnapshot.hasChild(fullPhoneNumber)
//            }
//            override fun onCancelled(databaseError: DatabaseError) {
//                mNumberExists.value = false
//            }
//        })
//    }

    fun signInWithEmailPassword(email: String, password: String){

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

    fun verifyNumber(phoneNumberWithCountryCode: String, activity: Activity){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumberWithCountryCode,         // Phone number to verify
            60,                 // Timeout duration
            TimeUnit.SECONDS,   // Unit of timeout
            activity, // Activity (for callback binding)
            callbacks
        )          // OnVerificationStateChangedCallbacks

//        val options = PhoneAuthOptions.newBuilder(auth)
//            .setPhoneNumber(phoneNumberWithCountryCode) // Phone number to verify
//            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//            .setActivity(activity) // Activity (for callback binding)
//            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
//            .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            val smsCode = credential.smsCode
            if (smsCode != null){
                mRetrieveSmsCode.value = smsCode
                println("retreved code $smsCode")
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            mVerificationFaild.value = true
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            mStoredVerificationId.value = verificationId
            println(verificationId)
        }
    }

    fun signInWithPhoneAuthCredential(receivedCode: String, activity: Activity) {

        val credential = PhoneAuthProvider.getCredential(storedVerificationId.value!!, receivedCode)


        auth.signInWithCredential(credential)
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
        auth.currentUser?.linkWithCredential(emailCredential)
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
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                mLinkEmailPasswordSuccessful.value = true

            }
            .addOnFailureListener {
                mVerificationFaild.value = true
//                println("loginWithEmailCredential")
            }
    }

    fun changeEmailPassword(password: String) {
        val user = auth.currentUser

        user!!.updatePassword(password).addOnCompleteListener {
            if (it.isSuccessful){
                mPasswordChanged.value = true
            }
        }
    }

    fun checkIfLoggedIn() {
        mLoggedIn.value = auth.currentUser != null
    }

    fun accountExist(fullPhoneNumber: String){
        val fakeEmail = "$fullPhoneNumber@carbondev.com"
        auth.fetchSignInMethodsForEmail(fakeEmail)
            .addOnCompleteListener { task ->
                val isNewUser = task.result!!.signInMethods!!.isEmpty()
                mAccountExist.value = !isNewUser
                println(mAccountExist.value)
            }
    }

    fun clearAllAuthVariable() {
        mAccountExist.value = null
        mLoginSuccessful.value = null
        mStoredVerificationId.value = null
        mPhoneRegistrationSuccessful.value = null
        mCodeNotMatch.value = null
        mVerificationFaild.value = null
        mLinkEmailPasswordSuccessful.value = null
        mPasswordChanged.value = null
        mLoggedIn.value = null
        mLogInErrorMassage.value = null
        mRetrieveSmsCode.value = null
    }
}
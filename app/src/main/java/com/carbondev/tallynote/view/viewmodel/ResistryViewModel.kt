package com.carbondev.tallynote.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ResistryViewModel : ViewModel() {

    private val _startLoginFragment = MutableLiveData<Boolean>().apply { value  = false }
    val startLoginFragment : LiveData<Boolean>
        get() = _startLoginFragment

    private val _startSignUpFragment = MutableLiveData<Boolean>().apply { value  = false }
    val startSignUpFragment : LiveData<Boolean>
        get() = _startSignUpFragment

    private val _startForgetPasswordFragment = MutableLiveData<Boolean>().apply { value  = false }
    val startForgetPasswordFragment : LiveData<Boolean>
        get() = _startForgetPasswordFragment

    fun startNavigateToForgetPasswordFragment(){
        _startForgetPasswordFragment.value = true
    }

    fun doneNavigateToForgetPasswordFragment(){
        _startForgetPasswordFragment.value = false
    }

    fun startNavigateToLoginFragment(){
        _startLoginFragment.value = true
    }

    fun doneNavigateToLoginFragment(){
        _startLoginFragment.value = false
    }

    fun startNavigateToSignUpFragment(){
        _startSignUpFragment.value = true
    }

    fun doneNavigateToSignUpFragment(){
        _startSignUpFragment.value = false
    }


}
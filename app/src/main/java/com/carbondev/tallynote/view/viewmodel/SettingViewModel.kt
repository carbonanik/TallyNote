package com.carbondev.tallynote.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carbondev.tallynote.datamodel.Customer
import com.carbondev.tallynote.repository.FirebaseDataRepository
import com.google.firebase.auth.FirebaseAuth


class SettingViewModel:ViewModel() {

    private val fireData = FirebaseDataRepository
    val ownerNameEditButtonClick = MutableLiveData<Boolean>()
    val languageChangeButtonClick = MutableLiveData<Boolean>()

    val ownerName : LiveData<String>
        get() = fireData.ownerName

    fun onLogOutButtonClick(){

        if (FirebaseAuth.getInstance().currentUser != null){
            FirebaseAuth.getInstance().signOut()
        }
    }

    fun onOwnerNameEditButtonClick(){
        ownerNameEditButtonClick.value = true
    }

    fun onLanguageChangeButtonClick(){
        languageChangeButtonClick.value = true
    }

    fun submitName(name : String){
        fireData.saveNameToFirebase(name)
    }

    fun fetchName(){
        fireData.fetchOwnerName()
    }

    fun saveOldCustomer(oldCustomer: Customer){
//        FirebaseDataRepository.saveOldCustomer(oldCustomer)
    }
}
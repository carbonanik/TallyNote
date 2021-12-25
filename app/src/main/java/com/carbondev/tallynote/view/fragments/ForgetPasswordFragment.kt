package com.carbondev.tallynote.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.carbondev.tallynote.databinding.ForgrtPasswordFragmentBinding
import com.carbondev.tallynote.view.viewmodel.ForgrtPasswordViewModel
import com.hbb20.CountryCodePicker

class ForgetPasswordFragment : Fragment() {
    private lateinit var viewModel: ForgrtPasswordViewModel
    private lateinit var viewBinding: ForgrtPasswordFragmentBinding
    private lateinit var navController: NavController
    private lateinit var cpp : CountryCodePicker

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ForgrtPasswordViewModel::class.java)
        viewBinding = ForgrtPasswordFragmentBinding.inflate(inflater, container, false)
        viewBinding.viewModel = viewModel
        viewBinding.lifecycleOwner = this

        cpp = viewBinding.countryCodePicker
        viewModel.countryCode = cpp.selectedCountryCodeWithPlus

        navController = this.findNavController()

        setObserver()

        return viewBinding.root
    }

    private fun setObserver(){
        viewModel.storedVerificationId.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()){
                viewModel.processing.value = false

                val action = ForgetPasswordFragmentDirections.actionForgetPasswordFragmentToVerifyNumberFragment(
                    viewModel.forgetPassword.value!!,
                    viewModel.phoneNumberWithCountryCode,
                    ""
                )
                navController.navigate(action)
            }
        })

        viewModel.accountExist.observe(viewLifecycleOwner, {
            if (it == true){
                val text = "This Number Already Exist"
                println(text)
//                viewModel.info.value = text
                viewModel.verifyNumber(requireActivity())

            } else if (it == false) {
                val text = "This number dose not exist"
                println(text)
                viewModel.info.value = text
                viewModel.processing.value = false
            }
        })

        viewModel.processing.observe( viewLifecycleOwner, {
            if (it){
                viewBinding.loading.visibility = View.VISIBLE
            } else {
                viewBinding.loading.visibility = View.GONE
            }
        })

        cpp.setOnCountryChangeListener {
            viewModel.countryCode = cpp.selectedCountryCodeWithPlus
        }

        viewModel.apply {
            observeList = listOf( forgetNumber, forgetPassword)
            observeList.forEach {
                it.observe(viewLifecycleOwner, textObserver)
            }
        }
    }

    private lateinit var observeList : List<MutableLiveData<String>>
    private val textObserver = Observer<String> {
        viewModel.buttonEnabled.value = !(observeList.any { it.value.isNullOrBlank() })
    }
}
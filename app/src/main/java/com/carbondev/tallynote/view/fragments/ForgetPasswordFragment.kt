package com.carbondev.tallynote.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.carbondev.tallynote.databinding.ForgrtPasswordFragmentBinding
import com.carbondev.tallynote.view.viewmodel.ForgrtPasswordViewModel

class ForgetPasswordFragment : Fragment() {
    private lateinit var viewModel: ForgrtPasswordViewModel
    private lateinit var viewBinding: ForgrtPasswordFragmentBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ForgrtPasswordViewModel::class.java)
        viewBinding = ForgrtPasswordFragmentBinding.inflate(inflater, container, false)
        viewBinding.viewModel = viewModel
        viewBinding.lifecycleOwner = this
        viewModel.ccp = viewBinding.countryCodePicker

        navController = this.findNavController()

        setObserver()

        return viewBinding.root
    }

    private fun setObserver(){
        viewModel.numberExists.observe(viewLifecycleOwner, Observer {
            if (it){
                viewModel.verifyNumber(requireActivity())
            } else {
                viewModel.info.value = "This Number Dose not Have An Account"
                viewModel.isLoading.value = false
//                //verify this new number
            }
        })

        viewModel.storedVerificationId.observe(viewLifecycleOwner, Observer {
            if (it.isNotBlank()){
                viewModel.isLoading.value = false
//                binding.verificationForm.visibility = View.VISIBLE
            }
        })
    }
}
package com.carbondev.tallynote.view.fragments

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.carbondev.tallynote.R
import com.carbondev.tallynote.databinding.SignupFragmentBinding
import com.carbondev.tallynote.view.viewmodel.SignupViewModel

class SignupFragment : Fragment() {

    private lateinit var viewModel: SignupViewModel
    private lateinit var viewBinding : SignupFragmentBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(SignupViewModel::class.java)
        viewBinding = SignupFragmentBinding.inflate(inflater, container, false)
        viewBinding.viewModel = viewModel
        viewBinding.lifecycleOwner = this
        viewModel.ccp = viewBinding.countryCodePicker

        navController = this.findNavController()

        setObservers()

        return viewBinding.root
    }

    private fun setObservers(){
        viewModel.storedVerificationId.observe(viewLifecycleOwner, {
            if (it.isNotBlank()){
                viewModel.isLoading.value = false

                val action = SignupFragmentDirections.actionSignupFragmentToVerifyNumberFragment(
                    viewModel.password.value!!,
                    viewModel.fullPhoneNumber,
                    viewModel.userName.value!!
                )
                navController.navigate(action)
            }
        })

        viewModel.numberExists.observe(viewLifecycleOwner, {
            if (it){
                viewModel.info.value = "This Number Already Have An Account"
                viewModel.isLoading.value = false
            } else {
//                //verify this new number
                if (viewModel.buttonEnabled.value!!){
                    viewModel.verifyNumber(requireActivity())
                }
            }
        })

        viewModel.info.observe(viewLifecycleOwner, {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })

        viewModel.isLoading.observe( viewLifecycleOwner, {
            if (it){
                viewBinding.loading.visibility = View.VISIBLE
            } else {
                viewBinding.loading.visibility = View.GONE
            }
        })

        viewModel.onViewPasswordClick.observe(viewLifecycleOwner, {
            if (it){
                viewBinding.signUpPassword.transformationMethod = null
                viewBinding.confirmSignUpPassword.transformationMethod = null
                viewBinding.singUpPasswordVisibilityToggle.setImageResource(R.drawable.ic_visibility_off)

            } else {
                viewBinding.confirmSignUpPassword.transformationMethod = PasswordTransformationMethod()
                viewBinding.signUpPassword.transformationMethod = PasswordTransformationMethod()
                viewBinding.singUpPasswordVisibilityToggle.setImageResource(R.drawable.ic_visibility)

            }
        })

        viewModel.userName.observe(viewLifecycleOwner, {
            viewModel.validateInput()
        })

        viewModel.phoneNumber.observe(viewLifecycleOwner, {
            viewModel.validateInput()
        })

        viewModel.password.observe(viewLifecycleOwner, {
            viewModel.validateInput()
        })

        viewModel.conPassword.observe(viewLifecycleOwner, {
            viewModel.validateInput()
        })
    }
}
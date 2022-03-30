package com.carbondev.tallynote.view.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
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

        viewModel.clearAuthVariable()
        setObservers()

        return viewBinding.root
    }

    private lateinit var watchList: List<EditText>
    private fun setObservers(){
        viewModel.storedVerificationId.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                viewModel.processingSignUp.value = false

                val action = SignupFragmentDirections.actionSignupFragmentToVerifyNumberFragment(
                    viewModel.password.value!!,
                    viewModel.phoneNumberWithCountryCode,
                    viewModel.userName.value!!
                )
                navController.navigate(action)
            }
        }

//        viewModel.numberExists.observe(viewLifecycleOwner, {
//            if (it){
//                viewModel.info.value = "This Number Already Have An Account"
//                viewModel.processingSignUp.value = false
//            } else {
////                //verify this new number
//                if (viewModel.buttonEnabled.value!!){
//                    viewModel.verifyNumber(requireActivity())
//                }
//            }
//        })

        viewModel.accountExist.observe(viewLifecycleOwner) {
            if (it == true) {
                val text = "This Number Already Exist"
                println(text)
                viewModel.info.value = text
                viewModel.processingSignUp.value = false
            } else if (it == false) {
                val text = "This number dose not exist"
                println(text)
//                viewModel.info.value = text
                viewModel.verifyNumber(requireActivity())
            }
        }

        viewModel.info.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.processingSignUp.observe( viewLifecycleOwner) {
            if (it) {
                viewBinding.loading.visibility = View.VISIBLE
            } else {
                viewBinding.loading.visibility = View.GONE
            }
        }

        viewModel.onViewPasswordClick.observe(viewLifecycleOwner) {
            if (it) {
                viewBinding.signUpPassword.apply {
                    transformationMethod = null
                    setSelection(text.length)
                }
                viewBinding.confirmSignUpPassword.apply {
                    transformationMethod = null
                    setSelection(text.length)
                }
                viewBinding.singUpPasswordVisibilityToggle.setImageResource(R.drawable.ic_visibility_off)

            } else {
                viewBinding.signUpPassword.apply {
                    transformationMethod = PasswordTransformationMethod()
                    setSelection(text.length)
                }

                viewBinding.confirmSignUpPassword.apply {
                    transformationMethod = PasswordTransformationMethod()
                    setSelection(text.length)
                }
                viewBinding.singUpPasswordVisibilityToggle.setImageResource(R.drawable.ic_visibility)

            }
        }

        viewBinding.apply {
            watchList = listOf(signUpName, signUpPhone, signUpPassword, confirmSignUpPassword)
            watchList.forEach { it.addTextChangedListener(watcher) }
        }
    }


    private val watcher = object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            viewModel.buttonEnabled.value = !(watchList.any { it.text.isNullOrBlank() })
        }

    }
}
package com.carbondev.tallynote.view.fragments

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.carbondev.tallynote.R
import com.carbondev.tallynote.databinding.LoginFragmentBinding
import com.carbondev.tallynote.view.activity.ListActivity
import com.carbondev.tallynote.view.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var viewBinding: LoginFragmentBinding
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        viewBinding = LoginFragmentBinding.inflate(inflater, container, false)
        viewBinding.viewModel = viewModel
        viewBinding.lifecycleOwner = this
        viewModel.ccp = viewBinding.loginCountryCodePicker

        navController = this.findNavController()

        setObservers()

        return viewBinding.root
    }

    private fun openListActivity(){
        val intent = Intent(context, ListActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun setObservers(){
        viewModel.loginNumber.observe(viewLifecycleOwner, {
            viewModel.validateInput()
        })

        viewModel.loginPassword.observe(viewLifecycleOwner, {
            viewModel.validateInput()
        })

        viewModel.info.observe(viewLifecycleOwner, {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })

        viewModel.loginSuccessful.observe(viewLifecycleOwner, {
            if (it && FirebaseAuth.getInstance().currentUser != null){
                viewModel.info.value = "Login Successful"
                openListActivity()
                viewModel.clearAllAuthVariable()
            } else {
                viewBinding.loginButton.isEnabled = true
                viewBinding.loading.visibility = View.GONE
            }
        })

        viewModel.logInErrorMassage.observe(viewLifecycleOwner, {
            viewModel.info.value = it
        })

        viewModel.onSignInButtonClick.observe(viewLifecycleOwner, {
            viewBinding.loginButton.isEnabled = false
            viewBinding.loading.visibility = View.VISIBLE
        })

        viewModel.onViewPasswordClick.observe(viewLifecycleOwner, {
            if (it){
                viewBinding.loginPassword.transformationMethod = null
                viewBinding.loinPasswordVisibilityToggle.setImageResource(R.drawable.ic_visibility_off)

            } else {
                viewBinding.loginPassword.transformationMethod = PasswordTransformationMethod()
                viewBinding.loinPasswordVisibilityToggle.setImageResource(R.drawable.ic_visibility)

            }
        })
    }
}
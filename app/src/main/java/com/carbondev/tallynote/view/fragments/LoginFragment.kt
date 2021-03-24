package com.carbondev.tallynote.view.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
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


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            val states = arrayOf(
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_enabled)
            )
            val colors = intArrayOf(
                Color.parseColor("#0054AC"),
                Color.parseColor("#818181")
            )
            val colorStates = ColorStateList(states,colors)

            viewBinding.loginButton.backgroundTintList = colorStates
        }

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

        viewModel.processingLogin.observe(viewLifecycleOwner, {
            viewModel.validateInput()
            viewBinding.loading.visibility = if (it) {
                View.VISIBLE
            } else {
                View.GONE
            }
        })

        viewModel.info.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()){
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.loginSuccessful.observe(viewLifecycleOwner, {
            viewModel.processingLogin.value = false
            if (it && FirebaseAuth.getInstance().currentUser != null){
                viewModel.info.value = "Login Successful"
                openListActivity()
                viewModel.clearAllAuthVariable()
            }
        })

        viewModel.logInErrorMassage.observe(viewLifecycleOwner, {
            viewModel.info.value = it
        })

        viewModel.onViewPasswordClick.observe(viewLifecycleOwner, {
            if (it){
                viewBinding.loginPassword.apply {
                    transformationMethod = null
                    setSelection(text.length)
                }
                viewBinding.loinPasswordVisibilityToggle.setImageResource(R.drawable.ic_visibility_off)

            } else {
                viewBinding.loginPassword.apply {
                    transformationMethod = PasswordTransformationMethod()
                    setSelection(text.length)
                }
                viewBinding.loinPasswordVisibilityToggle.setImageResource(R.drawable.ic_visibility)

            }
        })
    }
}
package com.carbondev.tallynote.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.carbondev.tallynote.databinding.VerifyNumberFragmentBinding
import com.carbondev.tallynote.view.activity.ListActivity
import com.carbondev.tallynote.view.viewmodel.VerifyNumberViewModel

class VerifyNumberFragment : Fragment() {

    private lateinit var viewModel: VerifyNumberViewModel
    private lateinit var viewBinding : VerifyNumberFragmentBinding
    private lateinit var navController: NavController

    private lateinit var name : String
    private lateinit var phone : String
    private lateinit var pass : String

    private val args: VerifyNumberFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(VerifyNumberViewModel::class.java)

        viewBinding = VerifyNumberFragmentBinding.inflate(inflater, container, false)
        viewBinding.viewModel = viewModel
        viewBinding.lifecycleOwner = this

        navController = this.findNavController()

        setObserver()

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        name = args.name
        phone = args.phone
        pass = args.pass

        viewModel.verifyForSignUp = name.isNotBlank()
    }

    private fun setObserver() {
//        viewModel.receivedCode.observe(viewLifecycleOwner, Observer {
//            if (it.isNullOrBlank()){
//                viewModel.info.value = "Registration Successful"
//                viewModel.linkEmailPasswordLogin(requireActivity())
//            }
//        })

        viewModel.phoneRegistrationSuccessful.observe(viewLifecycleOwner, Observer {
            if (it){
                if (viewModel.verifyForSignUp){
                    viewModel.info.value = "Registration Successful"
                    viewModel.linkEmailPasswordLogin(phone, pass, requireActivity())
                } else {
                    viewModel.info.value = "Registration Successful"
                    viewModel.changeEmailPassword(pass)
                }
            }
        })

        viewModel.receivedCode.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()){
                viewModel.signInWithPhone(it, requireActivity())
            }
        })

        viewModel.codeNotMatch.observe(viewLifecycleOwner, Observer {
            if (it){
                viewModel.info.value = "Verification Code Dose Not Match"
                viewModel.isLoading.value = false
            }
        })

        viewModel.verificationFaild.observe(viewLifecycleOwner, Observer {
            if (it){
                viewModel.info.value = "Verification Failed"
                viewModel.isLoading.value = false
            }
        })

        viewModel.linkEmailPasswordSuccessful.observe(viewLifecycleOwner, Observer {
            if (it){
                viewModel.allSuccess(name)
                openListActivity()
            }
        })

        viewBinding.numberVerifyButton.setOnClickListener {
            viewModel.signInWithPhone(viewModel.receivedCode.value, requireActivity())
        }
    }

    private fun openListActivity(){
        val intent = Intent(context, ListActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}











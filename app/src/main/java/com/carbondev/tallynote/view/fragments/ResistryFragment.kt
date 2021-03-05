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
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.carbondev.tallynote.R
import com.carbondev.tallynote.databinding.ResistryFragmentBinding
import com.carbondev.tallynote.view.viewmodel.ResistryViewModel

class ResistryFragment : Fragment() {

    private lateinit var viewModel: ResistryViewModel
    private lateinit var viewBinding : ResistryFragmentBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ResistryViewModel::class.java)

        viewBinding = ResistryFragmentBinding.inflate(inflater, container, false)
        viewBinding.viewModel = viewModel
        viewBinding.lifecycleOwner = this

        navController = this.findNavController()

        setObservers()

        return viewBinding.root
    }

    private fun setObservers(){
        viewModel.startLoginFragment.observe(viewLifecycleOwner, Observer {
            if (it) {
                navController.navigate(R.id.action_resistryFragment_to_loginFragment)
                viewModel.doneNavigateToLoginFragment()
            }
        })

        viewModel.startSignUpFragment.observe(viewLifecycleOwner, Observer {
            if (it) {
                navController.navigate(R.id.action_resistryFragment_to_signupFragment)
                viewModel.doneNavigateToSignUpFragment()
            }
        })

        viewModel.startForgetPasswordFragment.observe(viewLifecycleOwner, Observer {
            if (it) {
                navController.navigate(R.id.action_resistryFragment_to_forgetPasswordFragment)
                viewModel.doneNavigateToForgetPasswordFragment()
            }
        })

        viewBinding.privacyPolicyTv.setOnClickListener {
            MaterialDialog(requireContext()).show {
                title(text = "Privacy Policy")
                listItems(items = listOf(
                    getString(R.string.privacy_policy_01),
                    getString(R.string.privacy_policy_02),
                    getString(R.string.privacy_policy_03),
                    getString(R.string.privacy_policy_04),
                    getString(R.string.privacy_policy_05),
                    getString(R.string.privacy_policy_06),
                    getString(R.string.privacy_policy_07),
                    getString(R.string.privacy_policy_08),
                    getString(R.string.privacy_policy_09))
                )
                positiveButton(text = "Agreed!!")
                lifecycleOwner(this@ResistryFragment)
            }
        }
    }
}
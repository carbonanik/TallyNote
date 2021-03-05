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
import com.carbondev.tallynote.R
import com.carbondev.tallynote.databinding.SplashFragmentBinding
import com.carbondev.tallynote.view.activity.ListActivity
import com.carbondev.tallynote.view.viewmodel.SplashViewModel
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private lateinit var viewModel: SplashViewModel
    private lateinit var viewBinding : SplashFragmentBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)

        viewBinding = SplashFragmentBinding.inflate(inflater, container, false)
        viewBinding.viewModel = viewModel
        viewBinding.lifecycleOwner = this

        navController = this.findNavController()

        tryPersistence()

        setObservers()

        return viewBinding.root
    }

    private fun tryPersistence(){
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        } catch (e: DatabaseException) {
            println("persistence Error")
            println(e)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.checkIfLoggedIn()
    }

    private fun openRegistry() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(500)
            navController.navigate(R.id.action_splashFragment_to_resistryFragment)
        }
    }

    private fun openListActivity(){
        val intent = Intent(context, ListActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun setObservers(){
        viewModel.loggedIn.observe(viewLifecycleOwner, Observer {
            if (it) {
                openListActivity()
            } else {
                openRegistry()
            }
        })
    }
}
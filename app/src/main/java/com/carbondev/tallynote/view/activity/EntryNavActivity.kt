package com.carbondev.tallynote.view.activity

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.carbondev.tallynote.R
import com.carbondev.tallynote.utils.MyPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class EntryNavActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val languageToLoad  = MyPreference(this).getLanguage()
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config,
            baseContext.resources.displayMetrics
        )
        setContentView(R.layout.activity_entry_nav)



        val navHostFragment = supportFragmentManager.findFragmentById(R.id.entry_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.entry_nav)

        navController.graph = graph

    }

//    override fun onSupportNavigateUp(): Boolean {
//        return NavigationUI.navigateUp(navController)
//    }
}
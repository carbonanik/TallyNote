package com.carbondev.tallynote.view.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.carbondev.tallynote.R
import com.carbondev.tallynote.databinding.ActivitySettingBinding
import com.carbondev.tallynote.datamodel.Customer
import com.carbondev.tallynote.repository.FirebaseDataRepository
import com.carbondev.tallynote.utils.MyPreference
import com.carbondev.tallynote.view.viewmodel.SettingViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import java.io.InputStream
import java.util.*

class SettingActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingViewModel
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivitySettingBinding
    private lateinit var preference: MyPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        setupBinding(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.addAuthStateListener(authStateListener)
        preference = MyPreference(this)
        litchener()
    }

    private fun setupBinding(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(SettingViewModel::class.java)

        binding =
            DataBindingUtil.setContentView<ActivitySettingBinding>(this, R.layout.activity_setting)
                .apply {
                    this.lifecycleOwner = this@SettingActivity
                    this.settingViewModel = viewModel
                }
    }

    //Declaration and defination
    private val authStateListener = FirebaseAuth.AuthStateListener {
        if (it.currentUser == null) {
            val intent = Intent(this, EntryNavActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            this.startActivity(intent)
        }
    }

    private fun litchener() {
        binding.backButton.setOnClickListener {
            super.onBackPressed()
        }

        viewModel.ownerNameEditButtonClick.observe(this, {
            if (it) {
                MaterialDialog(this).show {
                    input(
                        hint = getString(R.string.your_name),
                        prefill = viewModel.ownerName.value
                    ) { _, text ->
                        if (text.isNotBlank()) {
                            viewModel.submitName(text.toString())
                            viewModel.fetchName()
                        }
                    }
                    positiveButton(R.string.ok)
                    negativeButton(R.string.cancel)
                }

                viewModel.ownerNameEditButtonClick.value = false
            }
        })

        viewModel.languageChangeButtonClick.observe(this, {
            if (it) {
                MaterialDialog(this).show {

                    listItemsSingleChoice(R.array.languages) { _, index, _ ->
                        println(index)
                        if (index == 0) {
                            preference.setLanguage(resources.getResourceEntryName(R.string.en))
                            changeLanguage()
                        } else if (index == 1) {
                            preference.setLanguage(resources.getResourceEntryName(R.string.bn))
                            changeLanguage()
                        }
                    }

//                    positiveButton(R.string.ok)
//                    negativeButton(R.string.cancel)
                }
                viewModel.languageChangeButtonClick.value = false
            }
        })

        binding.facebookPage.setOnClickListener {
            openFacebookLink()
        }

        binding.shareApp.setOnClickListener {
            openPlayStore()
        }
        binding.raw.setOnClickListener {
            openRaw()
        }
    }

    private fun changeLanguage() {
        val languageToLoad = preference.getLanguage()
        val locale = Locale(languageToLoad!!)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )

        restartActivity()
    }


    private fun openFacebookLink() {
//        val openURL = Intent(Intent.ACTION_VIEW)
//        openURL.data = Uri.parse("https://www.facebook.com/tallynote.info/")
//        startActivity(openURL)
//
        try {
            packageManager.getPackageInfo("com.facebook.katana", 0)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/101045342101746")))
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/tallynote.info/")
                )
            )
        }
    }

    private fun openPlayStore() {

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"

        intent.putExtra(Intent.EXTRA_SUBJECT, "Share The App")
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "https://play.google.com/store/apps/details?id=$packageName"
        )
        startActivity(Intent.createChooser(intent, "Share The App With Others"))
//        try {
//            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
//        } catch (e: ActivityNotFoundException) {
//            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
//        }
    }

    private fun restartActivity() {
        startActivity(intent)
        finish()
    }

    fun openRaw() {
//        val strm = resources.openRawResource(R.raw.customers)
//        val json = strm.bufferedReader().readText()
//        val customers = Gson().fromJson(json, Customers::class.java).customer
//        customers.forEach {
//            viewModel.saveOldCustomer(it)
//        }
    }

    data class Customers(
        val customer: List<Customer>
    )

}



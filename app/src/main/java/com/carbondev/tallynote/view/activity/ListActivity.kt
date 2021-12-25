package com.carbondev.tallynote.view.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.carbondev.tallynote.R
import com.carbondev.tallynote.datamodel.Customer
import com.carbondev.tallynote.datamodel.INTENT_CUSTOMER_DETAIL
import com.carbondev.tallynote.datamodel.INTENT_SELL_CURT
import com.carbondev.tallynote.datamodel.USERS
import com.carbondev.tallynote.databinding.ActivityListBinding
import com.carbondev.tallynote.databinding.AddCustomerBinding
import com.carbondev.tallynote.databinding.CheckConnectivityBinding
import com.carbondev.tallynote.utils.Connectivity
import com.carbondev.tallynote.view.viewmodel.ListViewModel
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_list.view.*


class ListActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var viewModel: ListViewModel
    private lateinit var binding : ActivityListBinding

    private var  newCustomerAdded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)


        if ( !(Connectivity().check(this)) ){
            showCheckConnection()
        } else {
            tryPersistence()
            syncDB()
        }

        setUpBinding(savedInstanceState)

        observers()
    }


    override fun onRestart() {
        viewModel.refreshList()
        super.onRestart()
    }

    private fun setUpBinding(savedInstanceState: Bundle?) {

        val linearLayoutManager = LinearLayoutManager(this)

        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)

        binding = DataBindingUtil.setContentView<ActivityListBinding>(
            this,
            R.layout.activity_list).apply {
            lifecycleOwner = this@ListActivity
            listViewModel = viewModel
            customerRecyclerVew.layoutManager = linearLayoutManager
            customerListSearchView.setOnQueryTextListener(this@ListActivity)
        }

        if (savedInstanceState == null ){
            viewModel.init()
        }

        binding.customerListSearchView.setOnClickListener {
            binding.customerListSearchView.isIconified = false
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        viewModel.customerList.value = ArrayList(viewModel.queryFilter(newText!!))
//        Toast.makeText(this, newText, Toast.LENGTH_SHORT).show()
        return false
    }

    private fun observers() {
        viewModel.onAddCustomerClick.observe(this, {

            val addCustomerDialog = AlertDialog.Builder(this).create()
            val view = layoutInflater.inflate(R.layout.add_customer, null)
            val addCustomerBinding = AddCustomerBinding.bind(view)

            addCustomerBinding.customer = Customer()
            addCustomerDialog.setView(view)
            addCustomerBinding.addButton.setOnClickListener {

                val customer = addCustomerBinding.customer

                if (customer!!.name.isNotEmpty()){
                    viewModel.addNewCustomer(customer)
                    newCustomerAdded = true
                }
                addCustomerDialog.dismiss()
            }

            addCustomerBinding.cancelButton.setOnClickListener {
                addCustomerDialog.dismiss()
            }

            addCustomerDialog.show()
        })


        viewModel.onItemClick.observe(this, { customerKey ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(INTENT_CUSTOMER_DETAIL, customerKey)
            this.startActivity(intent)
        })


        viewModel.onNoteClick.observe(this, {
            val intent = Intent(this, NoteListActivity::class.java)
            this.startActivity(intent)
        })

        viewModel.onCartClick.observe(this, {
            val intent = Intent(this, SellCartActivity::class.java)
            this.startActivity(intent)
        })

        viewModel.onOptionCartClick.observe(this, { customerId ->
            val intent = Intent(this, SellCartActivity::class.java)
            intent.putExtra(INTENT_SELL_CURT, customerId)
            this.startActivity(intent)
        })

        viewModel.onSettingClick.observe( this, {
            val intent = Intent( this, SettingActivity::class.java)
            this.startActivity(intent)
        })

        viewModel.liveCustomerList.observe(this, {
            viewModel.customerList.value = viewModel.liveCustomerList.value
            viewModel.refreshList()
            if (newCustomerAdded){
                scrollToTop()
                newCustomerAdded = false
            }
        })

        viewModel.customerList.observe(this, {
            viewModel.refreshList()
        })

//        viewModel.ownerTotalDue.observe(this, Observer {
//            binding.ownerTotalDue
//        })
    }

    private fun scrollToTop(){
        binding.customerRecyclerVew.smoothScrollToPosition(viewModel.liveCustomerList.value!!.size)
    }

    private fun showCheckConnection(){
        val checkD = AlertDialog.Builder(this).create()
        checkD.setCancelable(false)

        val view = layoutInflater.inflate(R.layout.check_connectivity, null)
        val checkB = CheckConnectivityBinding.bind(view)
        checkD.setView(view)

        checkB.ok.setOnClickListener {
            checkD.dismiss()
            if ( (Connectivity().check(this)) ){
                syncDB()
            }// else {
//                finish()
           // }
        }

        checkD.show()
    }

    private fun syncDB(){
        val ref = FirebaseDatabase.getInstance().getReference(USERS)
        ref.keepSynced(true)
    }

    private fun tryPersistence(){
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        } catch (e: DatabaseException) {
            println(e)
        }
    }

}
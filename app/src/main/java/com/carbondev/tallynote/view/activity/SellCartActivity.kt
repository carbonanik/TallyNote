package com.carbondev.tallynote.view.activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.carbondev.tallynote.R
import com.carbondev.tallynote.datamodel.INTENT_SELL_CURT
import com.carbondev.tallynote.databinding.ActivitySellCartBinding
import com.carbondev.tallynote.databinding.SinglePayInputDialogBinding
import com.carbondev.tallynote.utils.SoftKeyboard
import com.carbondev.tallynote.view.adapter.ProductLibraryAdapter
import com.carbondev.tallynote.view.viewmodel.SellCartViewModel
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class SellCartActivity : AppCompatActivity() {

    private lateinit var viewModel: SellCartViewModel
    private lateinit var binding: ActivitySellCartBinding
    private lateinit var productLibraryAdapter: ProductLibraryAdapter
    private var softKeyboardIsOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell_cart)
        setupBindings(savedInstanceState)
        observerListener()
        softKeyEvent()
    }

    private fun setupBindings(savedInstanceState: Bundle?) {

        viewModel = ViewModelProvider(this).get(SellCartViewModel::class.java)

        if (savedInstanceState == null) {
            viewModel.init(getCustomerKeyIntent())
        }

        binding = DataBindingUtil.setContentView<ActivitySellCartBinding>(
            this,
            R.layout.activity_sell_cart
        ).apply {
            lifecycleOwner = this@SellCartActivity
            sellCartViewModel = viewModel
            recyclerViewSellItem.layoutManager = LinearLayoutManager(this@SellCartActivity)
            recyclerViewSellItem.adapter = viewModel.getSellItemAdapter()

            productLibraryAdapter = ProductLibraryAdapter(viewModel)

//            val reverseLinearLayoutManager = LinearLayoutManager(this@SellCartActivity)
//            reverseLinearLayoutManager.reverseLayout = true
//            reverseLinearLayoutManager.stackFromEnd = true
            libraryRecyclerView.layoutManager = LinearLayoutManager(this@SellCartActivity)
            libraryRecyclerView.adapter = productLibraryAdapter
        }
    }

    private fun getCustomerKeyIntent(): String {
        return intent.getStringExtra(INTENT_SELL_CURT) ?: ""
    }

    private fun observerListener() {

        viewModel.openSell.observe(this, {
            viewModel.refreshProductList()
        })

        viewModel.cursorGoUp.observe(this, {
            if (it) {
                binding.productNameEditText.clearFocus()
                binding.detailEditText.clearFocus()
//            binding.priceEditText.clearFocus()
                binding.productNameEditText.requestFocus()
                viewModel.cursorGoUp.value = false
            }
        })

        viewModel.showPaymentForm.observe(this, {
            if (it) {

                binding.sellCartPaymentFormWithShowButton.animate()
                    .translationY(binding.sellCartPaymentForm.height.toFloat())
                binding.sellCartPaymentFormShowButton.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp)

                val param =
                    binding.recyclerViewSellItem.layoutParams// = 50//binding.recyclerViewSellItem.height + binding.sellCartPaymentForm.height
                param.height =
                    binding.recyclerViewSellItem.height + binding.sellCartPaymentForm.height
                binding.recyclerViewSellItem.layoutParams = param

            } else {
                binding.sellCartPaymentFormWithShowButton.animate().translationY(0F)
                binding.sellCartPaymentFormShowButton.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)

                val param =
                    binding.recyclerViewSellItem.layoutParams// = 50//binding.recyclerViewSellItem.height + binding.sellCartPaymentForm.height
                param.height =
                    binding.recyclerViewSellItem.height - binding.sellCartPaymentForm.height
                binding.recyclerViewSellItem.layoutParams = param
            }
        })

        viewModel.payButtonClick.observe(this, {

            val payDialog = AlertDialog.Builder(this).create()
            val view = layoutInflater.inflate(R.layout.single_pay_input_dialog, null)
            val singlePayInputDialogBinding = SinglePayInputDialogBinding.bind(view)

            singlePayInputDialogBinding.addButton.setOnClickListener {
                val payAmount = singlePayInputDialogBinding.inputNumber
                if (!payAmount.isNullOrEmpty()) {
                    viewModel.addPaymentToSell(payAmount = payAmount)
                    payDialog.dismiss()
                }
            }

            singlePayInputDialogBinding.paidButton.setOnClickListener {
                viewModel.addPaymentToSell(payAll = true)
                payDialog.dismiss()
            }

            singlePayInputDialogBinding.canselButton.setOnClickListener {
                payDialog.dismiss()
            }

            payDialog.setView(view)

            payDialog.show()
        })

        viewModel.equalPressOnCalculator.observe(this, {
            binding.myKeyboard.publicEqualButton()
        })

        viewModel.librarySearchText.observe(this, viewModel::getLibraryFilteredList)


        viewModel.queryLibraryList.observe(this, {
            productLibraryAdapter.refreshData(it)
        })

        viewModel.libraryItemClick.observe(this, {
            if (it.detail.isNotEmpty()) {
                viewModel.autoFillUpProduct(it)
                binding.libraryListView.visibility = View.GONE
            }
        })

//        val editText = findViewById<View>(R.id.priceEditText) as EditText

        binding.priceEditText.setOnClickListener {
            if (binding.myKeyboard.visibility == View.GONE) {
                binding.myKeyboard.visibility = View.VISIBLE

                SoftKeyboard(this).hideKeyboardFromActivity()

                binding.productNameEditText.clearFocus()
                binding.detailEditText.clearFocus()

            } else {
                binding.myKeyboard.visibility = View.GONE
            }
        }

        binding.productNameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.myKeyboard.visibility = View.GONE
            }
        }

        binding.detailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.myKeyboard.visibility = View.GONE
            }
        }


        val ic = binding.priceEditText.onCreateInputConnection(EditorInfo())
        binding.myKeyboard.setInputConnection(ic)

        binding.backButton.setOnClickListener {
            when {
                softKeyboardIsOpen ->
                    SoftKeyboard(this).hideKeyboardFromActivity()
                binding.myKeyboard.visibility == View.VISIBLE ->
                    binding.myKeyboard.visibility = View.GONE
                else -> super.onBackPressed()
            }
        }

        //
        binding.getNameFromLibrary.setOnClickListener {
            viewModel.isName = true
            binding.libraryListView.visibility = View.VISIBLE
            viewModel.getLibraryFilteredList("")
        }

        binding.getDetaiFromLibrary.setOnClickListener {
            viewModel.isName = false
            binding.libraryListView.visibility = View.VISIBLE
            viewModel.getLibraryFilteredList("")
        }

        binding.librarySearchInputField.setStartIconOnClickListener {
            if (softKeyboardIsOpen) {
                SoftKeyboard(this).hideKeyboardFromActivity()
            } else {
                binding.libraryListView.visibility = View.GONE
            }
        }

        binding.addLibraryItemButton.setOnClickListener {
            viewModel.addLibraryItem(viewModel.libraryAddItem.value)
            viewModel.libraryAddItem.value = ""
        }
    }

    override fun onBackPressed() {
        when {
            binding.libraryListView.visibility == View.VISIBLE ->
                binding.libraryListView.visibility = View.GONE
            binding.myKeyboard.visibility == View.VISIBLE ->
                binding.myKeyboard.visibility = View.GONE
            else -> super.onBackPressed()
        }
    }

    private fun softKeyEvent(){
        KeyboardVisibilityEvent.setEventListener(
            this,
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    softKeyboardIsOpen = isOpen
                }

            })
    }


}


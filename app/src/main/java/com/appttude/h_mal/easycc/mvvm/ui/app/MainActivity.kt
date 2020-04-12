package com.appttude.h_mal.easycc.mvvm.ui.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.appttude.h_mal.easycc.R
import com.appttude.h_mal.easycc.databinding.ActivityMainBinding
import com.appttude.h_mal.easycc.utils.DisplayToast
import com.appttude.h_mal.easycc.utils.clearEditText
import com.appttude.h_mal.easycc.utils.hideView
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MainActivity : AppCompatActivity(), RateListener, KodeinAware, View.OnClickListener{

    override val kodein by kodein()
    private val factory : MainViewModelFactory by instance()

    companion object{
        lateinit var viewModel: MainViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)

        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        viewModel.rateListener = this

        intent.extras?.apply {
            val itemOne = getString("parse_1")
            val itemTwo = getString("parse_2")

            if (!itemOne.isNullOrEmpty() && !itemTwo.isNullOrEmpty()){
                viewModel.rateIdTo = itemOne
                viewModel.rateIdFrom = itemTwo

                viewModel.getExchangeRate()
            }
        }

        viewModel.start()

        topInsertValue.addTextChangedListener(textWatcherClass)
        bottomInsertValues.addTextChangedListener(textWatcherClass2)

        currency_one.setOnClickListener(this)
        currency_two.setOnClickListener(this)
    }

    private fun showCustomDialog(view: View?){
        val dialogClass = CustomDialogClass(this, view as TextView, viewModel)
        dialogClass.show()
    }

    override fun onStarted() {
        progressBar.hideView(false)
    }

    override fun onSuccess() {
        progressBar.hideView(true)
        bottomInsertValues.clearEditText()
        topInsertValue.clearEditText()
    }

    override fun onFailure(message: String) {
        progressBar.hideView(true)
        DisplayToast(message)
    }

    override fun onClick(view: View?) {
        showCustomDialog(view)
    }

    private val textWatcherClass: TextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, st: Int, b: Int, c: Int) {
            bottomInsertValues.removeTextChangedListener(textWatcherClass2)
            if (topInsertValue.text.isNullOrEmpty()) {
                bottomInsertValues.setText("")
            }
        }
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {
            try {
                viewModel.setBottomValue(s.toString(), bottomInsertValues)
            } catch (e: NumberFormatException) {
                Log.e(this.javaClass.simpleName, "no numbers inserted")
            }
            bottomInsertValues.addTextChangedListener(textWatcherClass2)
        }
    }

    private val textWatcherClass2: TextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, st: Int, b: Int, c: Int) {
            topInsertValue.removeTextChangedListener(textWatcherClass)
            if (bottomInsertValues.text.isNullOrEmpty()) {
                topInsertValue.clearEditText()
            }
        }
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {
            try {
                viewModel.setTopValue(s.toString(), topInsertValue)
            } catch (e: NumberFormatException) {
                Log.e(this.javaClass.simpleName, "no numbers inserted")
            }
            topInsertValue.addTextChangedListener(textWatcherClass)
        }
    }

}

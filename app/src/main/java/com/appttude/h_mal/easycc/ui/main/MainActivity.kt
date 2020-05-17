package com.appttude.h_mal.easycc.ui.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.appttude.h_mal.easycc.R
import com.appttude.h_mal.easycc.databinding.ActivityMainBinding
import com.appttude.h_mal.easycc.utils.clearEditText
import com.appttude.h_mal.easycc.utils.displayToast
import com.appttude.h_mal.easycc.utils.hideView
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MainActivity : AppCompatActivity(), KodeinAware, View.OnClickListener {

    override val kodein by kodein()
    // Retrieve MainViewModelFactory via dependency injection
    private val factory: MainViewModelFactory by instance()

    companion object {
        lateinit var viewModel: MainViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Keyboard is not overlapping views
        this.window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        viewModel = ViewModelProviders.of(this, factory)
                .get(MainViewModel::class.java)

        // Bind viewmodel to layout with view binding
        DataBindingUtil
                .setContentView<ActivityMainBinding>(this, R.layout.activity_main)
                .apply {
            viewmodel = viewModel
            lifecycleOwner = this@MainActivity
        }

        viewModel.initiate(intent.extras)

        setUpListeners()
        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.operationStartedListener.observe(this, Observer {
            // Show progress bar
            progressBar.hideView(false)
        })
        viewModel.operationFinishedListener.observe(this, Observer { pair ->
            // hide progress bar
            progressBar.hideView(true)
            if (pair.first){
                // Operation was successful remove text in EditTexts
                bottomInsertValues.clearEditText()
                topInsertValue.clearEditText()
            }else{
                // Display Toast with error message returned from Viewmodel
                pair.second?.let { displayToast(it) }
            }
        })
    }

    private fun setUpListeners(){
        topInsertValue.addTextChangedListener(textWatcherClass)
        bottomInsertValues.addTextChangedListener(textWatcherClass2)

        currency_one.setOnClickListener(this)
        currency_two.setOnClickListener(this)
    }

    private fun showCustomDialog(view: View?) {
        CustomDialogClass(this, object : ClickListener {
            override fun onText(currencyName: String) {
                (view as TextView).text = currencyName
                viewModel.setCurrencyName(view.tag, currencyName)
            }

        }).show()
    }

    override fun onClick(view: View?) {
        showCustomDialog(view)
    }

    // Text watcher applied to EditText @topInsertValue
    private val textWatcherClass: TextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, st: Int, b: Int, c: Int) {
            // Remove text watcher on other text watcher to prevent infinite loop
            bottomInsertValues.removeTextChangedListener(textWatcherClass2)
            // Clear any values if current EditText is empty
            if (topInsertValue.text.isNullOrEmpty())
                bottomInsertValues.setText("")
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {
            bottomInsertValues.setText(viewModel.getConversion(s.toString()))
            // add Text watcher back as it is safe to do so
            bottomInsertValues.addTextChangedListener(textWatcherClass2)
        }
    }

    private val textWatcherClass2: TextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, st: Int, b: Int, c: Int) {

            topInsertValue.removeTextChangedListener(textWatcherClass)
            if (bottomInsertValues.text.isNullOrEmpty())
                topInsertValue.clearEditText()
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {
            topInsertValue.setText(viewModel.getReciprocalConversion(s.toString()))
            topInsertValue.addTextChangedListener(textWatcherClass)
        }
    }

}

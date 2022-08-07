package com.appttude.h_mal.easycc.ui.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.appttude.h_mal.easycc.R
import com.appttude.h_mal.easycc.databinding.ActivityMainBinding
import com.appttude.h_mal.easycc.utils.clearEditText
import com.appttude.h_mal.easycc.utils.displayToast
import com.appttude.h_mal.easycc.utils.hideView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*
         * Prevent keyboard overlapping views
         */
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

        viewModel.initiate(intent.extras)

        binding.currencyOne.text = viewModel.rateIdTo
        binding.currencyTwo.text  = viewModel.rateIdFrom

        setUpListeners()
        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.operationStartedListener.observe(this) {
            binding.progressBar.hideView(false)
        }
        viewModel.operationFinishedListener.observe(this) { pair ->
            // hide progress bar
            binding.progressBar.hideView(true)
            if (pair.first) {
                // Operation was successful remove text in EditTexts
                binding.bottomInsertValues.clearEditText()
                binding.topInsertValue.clearEditText()
            } else {
                // Display Toast with error message returned from Viewmodel
                pair.second?.let { displayToast(it) }
            }
        }
    }

    private fun setUpListeners() {
        binding.topInsertValue.addTextChangedListener(textWatcherClass)
        binding.bottomInsertValues.addTextChangedListener(textWatcherClass2)

        binding.currencyOne.setOnClickListener(this)
        binding.currencyTwo.setOnClickListener(this)
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
            binding.bottomInsertValues.removeTextChangedListener(textWatcherClass2)
            // Clear any values if current EditText is empty
            if (binding.topInsertValue.text.isNullOrEmpty())
                binding.bottomInsertValues.setText("")
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {
            binding.bottomInsertValues.setText(viewModel.getConversion(s.toString()))
            // add Text watcher back as it is safe to do so
            binding.bottomInsertValues.addTextChangedListener(textWatcherClass2)
        }
    }

    private val textWatcherClass2: TextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, st: Int, b: Int, c: Int) {

            binding.topInsertValue.removeTextChangedListener(textWatcherClass)
            if (binding.bottomInsertValues.text.isNullOrEmpty())
                binding.topInsertValue.clearEditText()
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {
            binding.topInsertValue.setText(viewModel.getReciprocalConversion(s.toString()))
            binding.topInsertValue.addTextChangedListener(textWatcherClass)
        }
    }

}

package com.appttude.h_mal.easycc.ui.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import com.appttude.h_mal.easycc.databinding.ActivityMainBinding
import com.appttude.h_mal.easycc.models.CurrencyModel
import com.appttude.h_mal.easycc.ui.BaseActivity
import com.appttude.h_mal.easycc.utils.clearEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel>(), View.OnClickListener {

    override val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*
         * Prevent keyboard overlapping views
         */
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )

        viewModel.initiate(intent.extras)

        binding.currencyOne.text = viewModel.rateIdFrom
        binding.currencyTwo.text = viewModel.rateIdTo

        setUpListeners()
    }

    override fun onSuccess(data: Any?) {
        super.onSuccess(data)
        if (data is CurrencyModel) {
            binding.bottomInsertValues.clearEditText()
            binding.topInsertValue.clearEditText()
        }
    }

    private fun setUpListeners() {
        binding.topInsertValue.addTextChangedListener(textWatcherClass)
        binding.bottomInsertValues.addTextChangedListener(textWatcherClass2)

        binding.currencyOne.setOnClickListener(this)
        binding.currencyTwo.setOnClickListener(this)
    }

    private fun showCustomDialog(view: View?) {
        CustomDialogClass(this) {
            (view as TextView).text = it
            viewModel.setCurrencyName(view.tag, it)
        }.show()
    }

    override fun onClick(view: View?) {
        showCustomDialog(view)
    }

    private val textWatcherClass: TextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, st: Int, b: Int, c: Int) {
            // Remove text watcher on other text watcher to prevent infinite loop
            binding.bottomInsertValues.removeTextChangedListener(textWatcherClass2)

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

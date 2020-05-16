package com.appttude.h_mal.easycc.mvvm.ui.app

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
import com.appttude.h_mal.easycc.mvvm.utils.DisplayToast
import com.appttude.h_mal.easycc.mvvm.utils.clearEditText
import com.appttude.h_mal.easycc.mvvm.utils.hideView
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MainActivity : AppCompatActivity(), RateListener, KodeinAware, View.OnClickListener {

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
            progressBar.hideView(false)
        })
        viewModel.operationFinishedListener.observe(this, Observer { pair ->
            progressBar.hideView(true)
            if (pair.first){
                bottomInsertValues.clearEditText()
                topInsertValue.clearEditText()
            }else{
                pair.second?.let { DisplayToast(it) }
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

        val dialogClass = CustomDialogClass(this, object : ClickListener {
            override fun onText(currencyName: String) {
                (view as TextView).text = currencyName
                viewModel.setCurrencyName(view.tag, currencyName)
            }
        })
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
            if (topInsertValue.text.isNullOrEmpty())
                bottomInsertValues.setText("")
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {
            bottomInsertValues.setText(viewModel.getConversion(s.toString()))
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

package com.appttude.h_mal.easycc.ui.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.appttude.h_mal.easycc.R
import com.appttude.h_mal.easycc.databinding.CurrencyAppWidgetConfigureBinding
import com.appttude.h_mal.easycc.ui.main.ClickListener
import com.appttude.h_mal.easycc.ui.main.CustomDialogClass
import com.appttude.h_mal.easycc.utils.displayToast
import com.appttude.h_mal.easycc.utils.transformIntToArray
import com.appttude.h_mal.easycc.widget.CurrencyAppWidgetKotlin
import kotlinx.android.synthetic.main.currency_app_widget_configure.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

/**
 * The configuration screen for the [CurrencyAppWidgetKotlin] AppWidget.
 */
class CurrencyAppWidgetConfigureActivityKotlin : AppCompatActivity(), KodeinAware, View.OnClickListener {

    override val kodein by kodein()
    private val factory: WidgetViewModelFactory by instance()

    var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    companion object {
        lateinit var viewModel: WidgetViewModel
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)

        // Find the widget id from the intent.
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        // ViewModel setup
        viewModel = ViewModelProviders.of(this, factory).get(WidgetViewModel::class.java)
        viewModel.initiate(mAppWidgetId)

        setupDataBinding()
        setupObserver()
        setupClickListener()
    }

    private fun setupClickListener() {
        submit_widget.setOnClickListener(this)
        currency_one.setOnClickListener(this)
        currency_two.setOnClickListener(this)
    }

    private fun setupObserver() {
        viewModel.operationFinishedListener.observe(this, Observer {

            // it.first is a the success of the operation
            if (it.first){
                displaySubmitDialog()
            }else{
                // failed operation - display toast with message from it.second
                it.second?.let { message -> displayToast(message) }
            }
        })
    }

    private fun setupDataBinding() {
        // data binding to @R.layout.currency_app_widget_configure
        DataBindingUtil.setContentView<CurrencyAppWidgetConfigureBinding>(
                this,
                R.layout.currency_app_widget_configure
        ).apply {
            viewmodel = viewModel
            lifecycleOwner = this@CurrencyAppWidgetConfigureActivityKotlin
        }
    }

    override fun onClick(view: View?) {
        when (view?.tag.toString()) {
            "top", "bottom" -> showCustomDialog(view)
            "submit" -> viewModel.submitSelectionOnClick()
            else -> {
                return
            }
        }
    }

    private fun displaySubmitDialog() {
        val message = viewModel.getSubmitDialogMessage()
        WidgetSubmitDialog(this, message, object : DialogSubmit {
            override fun onSubmit() {
                sendUpdateIntent()
                finishCurrencyWidgetActivity()
            }
        }).show()
    }


    private fun showCustomDialog(view: View?) {
        CustomDialogClass(this, object : ClickListener {
            override fun onText(currencyName: String) {
                (view as TextView).text = currencyName
                viewModel.setCurrencyName(view.tag, currencyName)
            }
        }).show()
    }

    fun finishCurrencyWidgetActivity(){
        // Make sure we pass back the original appWidgetId
        val resultValue = intent
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    fun sendUpdateIntent() {
        // It is the responsibility of the configuration activity to update the app widget
        // Send update broadcast to widget app class
        Intent(this@CurrencyAppWidgetConfigureActivityKotlin,
                CurrencyAppWidgetKotlin::class.java
        ).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            viewModel.setWidgetStored()

            // Put current app widget ID into extras and send broadcast
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, transformIntToArray(mAppWidgetId))
            sendBroadcast(this)
        }
    }

}

package com.appttude.h_mal.easycc.ui.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.appttude.h_mal.easycc.databinding.CurrencyAppWidgetConfigureBinding
import com.appttude.h_mal.easycc.ui.main.ClickListener
import com.appttude.h_mal.easycc.ui.main.CustomDialogClass
import com.appttude.h_mal.easycc.utils.displayToast
import com.appttude.h_mal.easycc.utils.transformIntToArray
import com.appttude.h_mal.easycc.widget.CurrencyAppWidgetKotlin
import dagger.hilt.android.AndroidEntryPoint

/**
 * The configuration screen for the [CurrencyAppWidgetKotlin] AppWidget.
 */
@AndroidEntryPoint
class CurrencyAppWidgetConfigureActivityKotlin : AppCompatActivity(),
    View.OnClickListener {

    val viewModel: WidgetViewModel by viewModels()

    private lateinit var binding: CurrencyAppWidgetConfigureBinding
    private var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        binding = CurrencyAppWidgetConfigureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)

        // Find the widget id from the intent.
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        viewModel.initiate(mAppWidgetId)

        setupObserver()
        setupClickListener()
    }

    private fun setupClickListener() {
        binding.submitWidget.setOnClickListener(this)
        binding.currencyOne.setOnClickListener(this)
        binding.currencyTwo.setOnClickListener(this)
    }

    private fun setupObserver() {
        viewModel.operationFinishedListener.observe(this) {

            // it.first is a the success of the operation
            if (it.first) {
                displaySubmitDialog()
            } else {
                // failed operation - display toast with message from it.second
                it.second?.let { message -> displayToast(message) }
            }
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

    fun finishCurrencyWidgetActivity() {
        // Make sure we pass back the original appWidgetId
        val resultValue = intent
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    fun sendUpdateIntent() {
        // It is the responsibility of the configuration activity to update the app widget
        // Send update broadcast to widget app class
        Intent(
            this@CurrencyAppWidgetConfigureActivityKotlin,
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

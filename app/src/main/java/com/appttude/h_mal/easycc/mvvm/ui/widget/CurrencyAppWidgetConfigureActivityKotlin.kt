package com.appttude.h_mal.easycc.mvvm.ui.widget

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.appttude.h_mal.easycc.R
import com.appttude.h_mal.easycc.databinding.CurrencyAppWidgetConfigureBinding
import com.appttude.h_mal.easycc.mvvm.ui.app.RateListener
import com.appttude.h_mal.easycc.utils.DisplayToast
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

/**
 * The configuration screen for the [CurrencyAppWidgetKotlin] AppWidget.
 */
class CurrencyAppWidgetConfigureActivityKotlin : AppCompatActivity(), KodeinAware, RateListener {

    override val kodein by kodein()
    private val factory : WidgetViewModelFactory by instance()

    var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    companion object{
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

        viewModel = ViewModelProviders.of(this, factory).get(WidgetViewModel::class.java)
        val binding: CurrencyAppWidgetConfigureBinding = DataBindingUtil.setContentView(this, R.layout.currency_app_widget_configure)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        viewModel.initialise(mAppWidgetId)
        viewModel.rateListener = this

    }

    override fun onStarted() {}

    override fun onSuccess() {
        WidgetSubmitDialog(this,mAppWidgetId, viewModel).show()
    }

    override fun onFailure(message: String) {
        DisplayToast(message)
    }

}

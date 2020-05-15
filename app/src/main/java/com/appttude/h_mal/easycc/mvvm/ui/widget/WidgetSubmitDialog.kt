package com.appttude.h_mal.easycc.mvvm.ui.widget

import android.app.Activity
import android.app.Dialog
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import com.appttude.h_mal.easycc.R
import com.appttude.h_mal.easycc.mvvm.utils.transformIntToArray
import kotlinx.android.synthetic.main.confirm_dialog.*


/**
 * Dialog created when submitting the completed selections
 * in [CurrencyAppWidgetConfigureActivityKotlin]
 */
class WidgetSubmitDialog(
    private val activity: Activity,
    private val appWidgetId: Int,
    private val viewModel: WidgetViewModel
) :Dialog(activity){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.confirm_dialog)
        // layer behind dialog to be transparent
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        // Dialog cannot be cancelled by clicking away
        setCancelable(false)

        confirm_text.text = StringBuilder().append("Create widget for ")
                .append(viewModel.getWidgetStringName())
                .append("?").toString()

        confirm_yes.setOnClickListener {
            // It is the responsibility of the configuration activity to update the app widget
            // Send update broadcast to widget app class
            Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE,
                    null,
                    context,
                    CurrencyAppWidgetKotlin::class.java).apply {
                // Save current widget pairs
                viewModel.setWidgetStored()
                // Put current app widget ID into extras and send broadcast
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, transformIntToArray(appWidgetId) )
                activity.sendBroadcast(this)
            }


            // Make sure we pass back the original appWidgetId
            val resultValue = activity.intent
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            activity.setResult(Activity.RESULT_OK, resultValue)
            activity.finish()
        }

        confirm_no.setOnClickListener { dismiss() }
    }
}
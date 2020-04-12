package com.appttude.h_mal.easycc.mvvm.ui.widget

import android.app.Activity
import android.app.Dialog
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import com.appttude.h_mal.easycc.R
import kotlinx.android.synthetic.main.confirm_dialog.*


/*
widget for when submitting the completed selections
 */
class WidgetSubmitDialog(
    private val activity: Activity,
    private val appWidgetId: Int,
    private val viewModel: WidgetViewModel
) :Dialog(activity){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.confirm_dialog)

//        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(false)

        //todo: amend widget text
        confirm_text.text = StringBuilder().append("Create widget for ")
                .append(viewModel.getWidgetStringName())
                .append("?").toString()

        confirm_yes.setOnClickListener {
            viewModel.setWidgetStored()

            val intent = Intent(context, CurrencyAppWidgetKotlin::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, IntArray(appWidgetId))
            context.sendBroadcast(intent)

            // Make sure we pass back the original appWidgetId
            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            activity.setResult(Activity.RESULT_OK, resultValue)
            activity.finish()
        }

        confirm_no.setOnClickListener { dismiss() }
    }
}
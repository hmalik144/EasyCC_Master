package com.appttude.h_mal.easycc.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import com.appttude.h_mal.easycc.helper.WidgetHelper
import com.appttude.h_mal.easycc.widget.WidgetServiceIntent.Companion.enqueueWork
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [CurrencyAppWidgetKotlin]
 */
@AndroidEntryPoint
class CurrencyAppWidgetKotlin : AppWidgetProvider() {

    @Inject
    lateinit var helper: WidgetHelper

    //update trigger either on timed update or from from first start
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        loadWidget(context)
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            helper.removeWidgetData(appWidgetId)
        }
        super.onDeleted(context, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        loadWidget(context)
        super.onEnabled(context)
    }

    private fun loadWidget(context: Context) {
        val mIntent = Intent(context, WidgetServiceIntent::class.java)
        enqueueWork(context, mIntent)
    }
}


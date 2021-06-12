package com.appttude.h_mal.easycc.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import com.appttude.h_mal.easycc.helper.WidgetHelper
import com.appttude.h_mal.easycc.widget.WidgetServiceIntent.Companion.enqueueWork
import org.kodein.di.KodeinAware
import org.kodein.di.LateInitKodein
import org.kodein.di.generic.instance


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [CurrencyAppWidgetKotlin]
 */

class CurrencyAppWidgetKotlin : AppWidgetProvider() {

    //DI with kodein to use in CurrencyAppWidgetKotlin
    private val kodein = LateInitKodein()
    private val repository: WidgetHelper by kodein.instance()

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
        kodein.baseKodein = (context.applicationContext as KodeinAware).kodein
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            repository.removeWidgetData(appWidgetId)
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


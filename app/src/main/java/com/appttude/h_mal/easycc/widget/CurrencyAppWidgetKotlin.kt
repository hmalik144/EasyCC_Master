package com.appttude.h_mal.easycc.widget

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.appttude.h_mal.easycc.R
import com.appttude.h_mal.easycc.helper.WidgetHelper
import com.appttude.h_mal.easycc.ui.main.MainActivity
import com.appttude.h_mal.easycc.utils.transformIntToArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.LateInitKodein
import org.kodein.di.generic.instance


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [CurrencyAppWidgetConfigureActivityKotlin]
 */
private const val TAG = "CurrencyAppWidgetKotlin"

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
        kodein.baseKodein = (context.applicationContext as KodeinAware).kodein
        Log.i(TAG, "onUpdate() appWidgetIds = ${appWidgetIds.size}")
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
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
        kodein.baseKodein = (context.applicationContext as KodeinAware).kodein
        // Enter relevant functionality for when the first widget is created
        AppWidgetManager.getInstance(context).apply {
            val thisAppWidget =
                ComponentName(context.packageName, CurrencyAppWidgetKotlin::class.java.name)
            val appWidgetIds = getAppWidgetIds(thisAppWidget)
            onUpdate(context, this, appWidgetIds)
        }
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        kodein.baseKodein = (context.applicationContext as KodeinAware).kodein
        // Enter relevant functionality for when the last widget is disabled
    }


    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.currency_app_widget)

        CoroutineScope(Dispatchers.Main).launch {
            val exchangeResponse = repository.getWidgetData()

            exchangeResponse?.let {
                val titleString = "${it.from}${it.to}"
                views.setTextViewText(R.id.exchangeName, titleString)
                views.setTextViewText(R.id.exchangeRate, it.rate.toString())

                setUpdateIntent(context, appWidgetId).let { intent ->
                    //set the pending intent to the icon
                    views.setImageViewResource(R.id.refresh_icon, R.drawable.ic_refresh_white_24dp)
                    views.setOnClickPendingIntent(R.id.refresh_icon, intent)
                }

                val clickIntentTemplate = clickingIntent(context)

                val configPendingIntent =
                    PendingIntent.getActivity(
                        context, appWidgetId, clickIntentTemplate,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                views.setOnClickPendingIntent(R.id.widget_view, configPendingIntent)
            }

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

    }

    private fun setUpdateIntent(context: Context, appWidgetId: Int): PendingIntent? {
        //Create update intent for refresh icon
        val updateIntent = Intent(
            context, CurrencyAppWidgetKotlin::class.java
        ).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, transformIntToArray(appWidgetId))
        }
        //add previous intent to this pending intent
        return PendingIntent.getBroadcast(
            context,
            appWidgetId,
            updateIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun clickingIntent(
        context: Context
    ): Intent {
        val pair = repository.repository.getConversionPair()
        val s1 = pair.first
        val s2 = pair.second
        return Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            putExtra("parse_1", s1)
            putExtra("parse_2", s2)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
    }

    private fun <T: Activity> clickingIntent(
        context: Context,
        activity: Class<T>,
        vararg argPairs: Pair<String, Any?>
    ): Intent {

        return Intent(context, activity::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            argPairs.forEach {
                putExtra(it.first, it.second)
            }
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
    }

    private fun <T: Any> Intent.putExtra(s: String, second: T?) {
        when(second){
            is String -> putExtra(s,second)
        }
    }
}


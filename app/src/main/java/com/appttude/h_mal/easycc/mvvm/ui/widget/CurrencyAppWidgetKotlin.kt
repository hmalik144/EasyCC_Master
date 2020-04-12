package com.appttude.h_mal.easycc.mvvm.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import com.appttude.h_mal.easycc.legacy.MainActivityJava
import com.appttude.h_mal.easycc.R
import com.appttude.h_mal.easycc.mvvm.data.Repository.Repository
import com.appttude.h_mal.easycc.mvvm.data.network.NetworkConnectionInterceptor
import com.appttude.h_mal.easycc.mvvm.data.network.api.GetData
import com.appttude.h_mal.easycc.mvvm.data.prefs.PreferenceProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.LateInitKodein
import org.kodein.di.generic.instance
import java.io.IOException

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [CurrencyAppWidgetConfigureActivityKotlin]
 */
class CurrencyAppWidgetKotlin : AppWidgetProvider()  {

    private val kodein = LateInitKodein()
    private val repository : Repository by kodein.instance()

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        kodein.baseKodein = (context.applicationContext as KodeinAware).kodein
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        kodein.baseKodein = (context.applicationContext as KodeinAware).kodein
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            repository.removeWidgetConversionPairs(appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        kodein.baseKodein = (context.applicationContext as KodeinAware).kodein
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        kodein.baseKodein = (context.applicationContext as KodeinAware).kodein
        // Enter relevant functionality for when the last widget is disabled
    }


    fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        //todo: get value from repository
        val stringList = repository.getWidgetConversionPairs(appWidgetId)
        val s1 = stringList[0]
        val s2 = stringList[1]

        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.currency_app_widget)
        views.setTextViewText(R.id.exchangeName, "Rates")
        views.setTextViewText(R.id.exchangeRate, "not set")

        //todo: async task to get rate
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = repository.getData(s1!!.substring(0,3),s2!!.substring(0,3))

                response?.results?.iterator()?.next()?.value?.let {
                    val titleString = "${it.fr}${it.to}"
                    views.setTextViewText(R.id.exchangeName, titleString)
                    views.setTextViewText(R.id.exchangeRate, it.value.toString())
                }
            }catch (io : IOException){
                Toast.makeText(context,io.message, Toast.LENGTH_LONG).show()
            }finally {
                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views)

                val opacity = 0.3f //opacity = 0: fully transparent, opacity = 1: no transparancy
                val backgroundColor = 0x000000 //background color (here black)

                views.setInt(R.id.widget_view, "setBackgroundColor", (opacity * 0xFF).toInt() shl 24 or backgroundColor)

                val clickIntentTemplate = Intent(context, MainActivityJava::class.java).apply {
                    action = Intent.ACTION_MAIN
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    putExtra("parse_1", s1)
                    putExtra("parse_2", s2)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }

                val configPendingIntent = PendingIntent.getActivity(context, 0, clickIntentTemplate, PendingIntent.FLAG_UPDATE_CURRENT)
                views.setOnClickPendingIntent(R.id.widget_view, configPendingIntent)
            }
        }

    }

    fun setupRepository(context: Context): Repository {
        val networkInterceptor =  NetworkConnectionInterceptor(context)
        val getData = GetData(networkInterceptor)
        val prefs = PreferenceProvider(context)
        return Repository(getData,prefs,context)
    }
}


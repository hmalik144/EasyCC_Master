package com.appttude.h_mal.easycc.widget

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.JobIntentService
import com.appttude.h_mal.easycc.R
import com.appttude.h_mal.easycc.helper.WidgetHelper
import com.appttude.h_mal.easycc.ui.main.MainActivity
import com.appttude.h_mal.easycc.utils.transformIntToArray
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WidgetServiceIntent : JobIntentService() {

    //DI with kodein to use in CurrencyAppWidgetKotlin
    @Inject
    lateinit var helper: WidgetHelper

    override fun onHandleWork(intent: Intent) {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val thisAppWidget = ComponentName(packageName, CurrencyAppWidgetKotlin::class.java.name)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)

        for (appWidgetId in appWidgetIds) {
            updateAppWidget(this, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.currency_app_widget)

        CoroutineScope(Dispatchers.Main).launch {
            val exchangeResponse = helper.getWidgetData()

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
                        PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
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
            FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )
    }

    private fun clickingIntent(
        context: Context
    ): Intent {
        val pair = helper.repository.getConversionPair()
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

    companion object {
        /**
         * Unique job ID for this service.
         */
        private const val JOB_ID = 1000

        /**
         * Convenience method for enqueuing work in to this service.
         */
        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, WidgetServiceIntent::class.java, JOB_ID, work)
        }
    }
}
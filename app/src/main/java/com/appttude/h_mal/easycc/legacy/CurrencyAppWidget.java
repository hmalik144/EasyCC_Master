package com.appttude.h_mal.easycc.legacy;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.appttude.h_mal.easycc.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import static com.appttude.h_mal.easycc.legacy.CurrencyAppWidgetConfigureActivity.loadTitlePref;
import static com.appttude.h_mal.easycc.legacy.PublicMethods.UriBuilder;
import static com.appttude.h_mal.easycc.legacy.PublicMethods.createUrl;
import static com.appttude.h_mal.easycc.legacy.PublicMethods.makeHttpRequest;
import static com.appttude.h_mal.easycc.legacy.PublicMethods.round;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link CurrencyAppWidgetConfigureActivity CurrencyAppWidgetConfigureActivity}
 */
public class CurrencyAppWidget extends AppWidgetProvider {

    static String LOG_TAG = CurrencyAppWidget.class.getSimpleName();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        String s1 = loadTitlePref(context,appWidgetId,0);
        String s2 = loadTitlePref(context,appWidgetId,1);

        String URL = UriBuilder(s1,s2);
        WidgetAsyncTask widgetAsyncTask = new WidgetAsyncTask(context,appWidgetId,appWidgetManager,s1.substring(0,3),s2.substring(0,3));
        widgetAsyncTask.execute(URL);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            CurrencyAppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static class WidgetAsyncTask extends AsyncTask<String, Void, Double> {

        private Context context;
        private int appWidgetId;
        private AppWidgetManager appWidgetManager;
        private String s1;
        private String s2;

        public WidgetAsyncTask(Context context, int appWidgetId, AppWidgetManager appWidgetManager, String s1, String s2) {
            this.context = context;
            this.appWidgetId = appWidgetId;
            this.appWidgetManager = appWidgetManager;
            this.s1 = s1;
            this.s2 = s2;
        }

        @Override
        protected Double doInBackground(String... urlString) {
            String jsonResponse = null;

            if (urlString.length < 1 || urlString[0] == null) {
                return null;
            }
            try {
                URL url = createUrl(urlString[0]);
                jsonResponse = makeHttpRequest(url);

            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), "Problem making the HTTP request.", e);
            }


            return extractFeatureFromJson(jsonResponse, s1, s2);

        }

        @Override
        protected void onPostExecute(Double result) {
            super.onPostExecute(result);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.currency_app_widget);
            views.setTextViewText(R.id.exchangeName, s1 + s2);
            views.setTextViewText(R.id.exchangeRate, round(result,2)+"");

            float opacity = 0.3f;           //opacity = 0: fully transparent, opacity = 1: no transparancy
            int backgroundColor = 0x000000; //background color (here black)
            views.setInt( R.id.widget_view, "setBackgroundColor", (int)(opacity * 0xFF) << 24 | backgroundColor);

            Intent clickIntentTemplate = new Intent(context, MainActivityJava.class);

            clickIntentTemplate.setAction(Intent.ACTION_MAIN);
            clickIntentTemplate.addCategory(Intent.CATEGORY_LAUNCHER);
            clickIntentTemplate.putExtra("parse_1",s1);
            clickIntentTemplate.putExtra("parse_2",s2);
            clickIntentTemplate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, clickIntentTemplate, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_view, configPendingIntent);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }

        private static double extractFeatureFromJson(String newsJSON, String s1, String s2) {
            double conversionValue = 0.00;

            Log.i(LOG_TAG, "extractFeatureFromJson: " + newsJSON);

            Log.i(LOG_TAG, "extractFeatureFromJson: " + s1 + "_" + s2);

            if (TextUtils.isEmpty(newsJSON)) {
                return 0.00;
            }

            try {
                JSONObject jObject = new JSONObject(newsJSON);
                conversionValue = jObject.getDouble(s1 + "_" + s2);

            } catch (JSONException e) {

                Log.e("MainActivityJava", "Problem parsing the JSON results", e);
            }
            return conversionValue;
        }

    }
}


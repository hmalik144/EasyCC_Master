package com.appttude.h_mal.easycc.legacy;

import android.app.Activity;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.appttude.h_mal.easycc.R;

/**
 * The configuration screen for the {@link CurrencyAppWidget CurrencyAppWidget} AppWidget.
 */
public class CurrencyAppWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.example.haimalik.myapplication.CurrencyAppWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private static final int PRIMARY = 0;
    private static final int SECONDARY = 1;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;


    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = CurrencyAppWidgetConfigureActivity.this;

            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.confirm_dialog);

            TextView text = (TextView) dialog.findViewById(R.id.confirm_text);
            text.setText(new StringBuilder().append("Create widget for ")
                    .append(loadTitlePref(context, mAppWidgetId, PRIMARY))
                    .append(loadTitlePref(context, mAppWidgetId, SECONDARY))
                    .append("?").toString());

            TextView yes = dialog.findViewById(R.id.confirm_yes);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //do for yes
                    // It is the responsibility of the configuration activity to update the app widget
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    CurrencyAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

                    // Make sure we pass back the original appWidgetId
                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                    setResult(RESULT_OK, resultValue);
                    finish();
                }
            });

            TextView no = dialog.findViewById(R.id.confirm_no);
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        }
    };

    public CurrencyAppWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveCurrencyPref(Context context, int appWidgetId, String text, int item) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "_" + item, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    public static String loadTitlePref(Context context, int appWidgetId, int item) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "_" + item, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getResources().getStringArray(R.array.currency_arrays)[0].substring(0,3);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY+ appWidgetId + "_0");
        prefs.remove(PREF_PREFIX_KEY+ appWidgetId + "_1");
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.currency_app_widget_configure);

        final TextView currencyOne = findViewById(R.id.currency_one);
        final TextView currencyTwo = findViewById(R.id.currency_two);
        TextView submit = findViewById(R.id.submit_widget);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }


        currencyOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialogClass dialogClass = new CustomDialogClass(CurrencyAppWidgetConfigureActivity.this,currencyOne,PRIMARY);
                dialogClass.show();
            }
        });


        currencyTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialogClass dialogClass = new CustomDialogClass(CurrencyAppWidgetConfigureActivity.this,currencyTwo,SECONDARY);
                dialogClass.show();
            }
        });

        submit.setOnClickListener(mOnClickListener);

    }

    private class CustomDialogClass extends Dialog implements android.view.View.OnClickListener{

        Context context;
        ListView listView;
        TextView textView;
        EditText editText;
        int item;

        public CustomDialogClass(@NonNull Context context, TextView textView, int item) {
            super(context);
            this.context = context;
            this.textView = textView;
            this.item = item;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.custom_dialog);

            getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            listView = (ListView) findViewById(R.id.list_view);
            editText = (EditText) findViewById(R.id.search_text) ;

            final ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(context,R.array.currency_arrays,android.R.layout.simple_list_item_1);
            listView.setAdapter(arrayAdapter);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    arrayAdapter.getFilter().filter(charSequence);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String text = adapterView.getItemAtPosition(i).toString().substring(0,3);
                    textView.setText(adapterView.getItemAtPosition(i).toString());
                    saveCurrencyPref(context,mAppWidgetId,text,item);
                    dismiss();
                }
            });
        }

        @Override
        public void onClick(View view) {

        }
    }

}


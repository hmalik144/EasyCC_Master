package com.appttude.h_mal.easycc;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import java.text.DecimalFormat;

import static com.appttude.h_mal.easycc.PublicMethods.UriBuilder;
import static com.appttude.h_mal.easycc.PublicMethods.createUrl;
import static com.appttude.h_mal.easycc.PublicMethods.makeHttpRequest;

public class MainActivity extends AppCompatActivity {

    EditText currencyOneEditText;
    EditText currencyTwoEditText;
    TextView currencyOne;
    TextView currencyTwo;
    double conversionRateOne;
    ProgressBar spinner;
    LinearLayout wholeView;

    private String URL = "https://free.currencyconverterapi.com/api/v3/convert?";
    private String CURRENCY_ONE = "currency_one_pref";
    private String CURRENCY_TWO = "currency_two_pref";

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        currencyOneEditText = (EditText) findViewById(R.id.editText);
        currencyTwoEditText = (EditText) findViewById(R.id.editText2);

        currencyOne = (TextView) findViewById(R.id.currency_one);
        currencyTwo = (TextView) findViewById(R.id.currency_two);

        if (getIntent().getExtras() != null) {
            Bundle b = getIntent().getExtras();
            currencyOne.setText(arrayEntry(b.getString("parse_1")));
            currencyTwo.setText(arrayEntry(b.getString("parse_2")));
        }else{
            if (pref != null) {
                currencyOne.setText(pref.getString(CURRENCY_ONE, String.valueOf(getResources().getTextArray(R.array.currency_arrays)[0])));
                currencyTwo.setText(pref.getString(CURRENCY_TWO, String.valueOf(getResources().getTextArray(R.array.currency_arrays)[0])));
            }else{
                currencyOne.setText(getResources().getTextArray(R.array.currency_arrays)[0]);
                currencyTwo.setText(getResources().getTextArray(R.array.currency_arrays)[0]);
            }
        }

        currencyOneEditText.addTextChangedListener(TextWatcherClass);
        currencyTwoEditText.addTextChangedListener(TextWatcherClass2);

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        wholeView = findViewById(R.id.whole_view);

//        addListenerOnSpinnerItemSelection();

        currencyOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialogClass dialogClass = new CustomDialogClass(MainActivity.this,currencyOne);
                dialogClass.show();
            }
        });

        currencyTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialogClass dialogClass = new CustomDialogClass(MainActivity.this,currencyTwo);
                dialogClass.show();
            }
        });

        String stringURL = UriBuilder(currencyOne.getText().toString().substring(0,3),
                currencyTwo.getText().toString().substring(0,3));
        MyAsyncTask task = new MyAsyncTask();
        task.execute(stringURL);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (getIntent().getExtras() != null) {
            Bundle b = getIntent().getExtras();
            currencyOne.setText(arrayEntry(b.getString("parse_1")));
            currencyTwo.setText(arrayEntry(b.getString("parse_2")));
        }
    }

    private TextWatcher TextWatcherClass = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            currencyTwoEditText.removeTextChangedListener(TextWatcherClass2);
            if(currencyOneEditText.getText().toString().isEmpty()){
                currencyTwoEditText.setText("");
                return;
            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {

            try{

            Double topValue = Double.parseDouble(currencyOneEditText.getText().toString());
            Double bottomValue = topValue * conversionRateOne;
            DecimalFormat df = new DecimalFormat("#.##");
            bottomValue = Double.valueOf(df.format(bottomValue));
            currencyTwoEditText.setText(bottomValue.toString());

            }
            catch (NumberFormatException e){
                Log.e(LOG_TAG, "no numbers inserted");
            }
            currencyTwoEditText.addTextChangedListener(TextWatcherClass2);
        }

    };

    private TextWatcher TextWatcherClass2 = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            currencyOneEditText.removeTextChangedListener(TextWatcherClass);
            if(currencyTwoEditText.getText().toString().isEmpty()){
                currencyOneEditText.setText("");
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {

            try{

            Double bottomValue = Double.parseDouble(currencyTwoEditText.getText().toString());
            Double topValue = bottomValue * (1 / conversionRateOne);
            DecimalFormat df = new DecimalFormat("#.##");
            topValue = Double.valueOf(df.format(topValue));
            currencyOneEditText.setText(topValue.toString());


            }
            catch (NumberFormatException e){
                Log.e(LOG_TAG, "no numbers inserted");
            }
            currencyOneEditText.addTextChangedListener(TextWatcherClass);
        }

    };



    private double extractFeatureFromJson(String newsJSON) {
        double conversionValue = 0.00;

        Log.i(LOG_TAG, "extractFeatureFromJson: " + newsJSON);

        String currencyOneVal = currencyOne.getText().toString().substring(0,3);
        String currencyTwoVal = currencyTwo.getText().toString().substring(0,3);

        Log.i(LOG_TAG, "extractFeatureFromJson: " + currencyOneVal + "_" + currencyTwoVal);

        if (TextUtils.isEmpty(newsJSON)) {
            return 0.00;
        }

        try {
            JSONObject jObject = new JSONObject(newsJSON);
            conversionValue = jObject.getDouble(currencyOneVal + "_" + currencyTwoVal);

        } catch (JSONException e) {

            Log.e("MainActivity", "Problem parsing the JSON results", e);
        }
        return conversionValue;
    }

    protected class MyAsyncTask extends AsyncTask<String, Void, Double> {

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
                Log.e(LOG_TAG, "Problem making the HTTP request.", e);
            }

            if (jsonResponse.equals("")){
                return null;
            }

            return extractFeatureFromJson(jsonResponse);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spinner.setVisibility(View.VISIBLE);
            AlphaAnimation animation1 = new AlphaAnimation(1.0f, 0.2f);
            animation1.setDuration(200);
            wholeView.startAnimation(animation1);
            wholeView.setAlpha(0.2f);
        }

        @Override
        protected void onPostExecute(Double result) {
            super.onPostExecute(result);

            spinner.setVisibility(View.GONE);
            wholeView.setAlpha(1.0f);

            if (result == null){
                Toast.makeText(MainActivity.this, "Failed to retrieve exchange rate", Toast.LENGTH_SHORT).show();
            }else{
                conversionRateOne = result;
            }

        }

    }

    private class CustomDialogClass extends Dialog implements android.view.View.OnClickListener{

        Context context;
        ListView listView;
        TextView textView;
        EditText editText;
        int selection;

        public CustomDialogClass(@NonNull Context context, TextView textView) {
            super(context);
            this.context = context;
            this.textView = textView;
            if (textView.getId() == R.id.currency_one){
                selection = 1;
            }else{
                selection = 2;
            }
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
                    textView.setText(adapterView.getItemAtPosition(i).toString());
                    SharedPreferences.Editor editor = pref.edit();
                    if (selection == 1) {
                        editor.putString(CURRENCY_ONE,adapterView.getItemAtPosition(i).toString());
                    }else{
                        editor.putString(CURRENCY_TWO,adapterView.getItemAtPosition(i).toString());
                    }
                    editor.apply();
                    currencyOneEditText.setText("");
                    currencyTwoEditText.setText("");
                    String stringURL = UriBuilder(currencyOne.getText().toString().substring(0,3),
                            currencyTwo.getText().toString().substring(0,3));
                    MyAsyncTask task = new MyAsyncTask();
                    task.execute(stringURL);
                    dismiss();
                }
            });
        }

        @Override
        public void onClick(View view) {

        }
    }

    private String arrayEntry (String s){
        String[] strings = getResources().getStringArray(R.array.currency_arrays);
        String returnString = strings[0];
        for (String string : strings) {
            if (s.equals(string.substring(0, 3))) {
                returnString = string;

            }
        }

        return returnString;
    }


}

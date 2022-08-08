package com.appttude.h_mal.easycc.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.appttude.h_mal.easycc.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Shared prefs class used for storing conversion name values as pairs
 * Then retrieving as pairs
 *
 */
private const val CURRENCY_ONE = "conversion_one"
private const val CURRENCY_TWO = "conversion_two"

class PreferenceProvider @Inject constructor(@ApplicationContext context: Context) {

    private val appContext = context.applicationContext

    // Instance of Shared preferences
    private val preference: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(appContext)

    // Lazy declaration of default rate if no rate is retrieved from
    private val defaultRate: String by lazy {
        context.resources.getStringArray(R.array.currency_arrays)[0]
    }

    // Save currency pairs into prefs
    fun saveConversionPair(s1: String, s2: String) {
        preference.edit()
            .putString(CURRENCY_ONE, s1)
            .putString(CURRENCY_TWO, s2)
            .apply()
    }

    // Retrieve Currency pairs from prefs
    // Returns Pairs
    fun getConversionPair(): Pair<String?, String?> {
        val fromString = getConversionString(CURRENCY_ONE)
        val toString = getConversionString(CURRENCY_TWO)

        return Pair(fromString, toString)
    }


    private fun getConversionString(conversionName: String): String? {
        return preference
            .getString(conversionName, defaultRate)
    }

    // Save currency pairs for widget
    fun saveWidgetConversionPair(
        fromString: String,
        toString: String, appWidgetId: Int
    ) {

        preference.edit()
            .putString("${appWidgetId}_$CURRENCY_ONE", fromString)
            .putString("${appWidgetId}_$CURRENCY_TWO", toString)
            .apply()
    }

    // Retrieve currency pairs for widget
    fun getWidgetConversionPair(appWidgetId: Int): Pair<String?, String?> {
        val fromString = getWidgetConversionString(appWidgetId, CURRENCY_ONE)
        val toString = getWidgetConversionString(appWidgetId, CURRENCY_TWO)

        return Pair(fromString, toString)
    }

    private fun getWidgetConversionString(
        appWidgetId: Int, conversionName: String
    ): String? {
        return preference
            .getString("${appWidgetId}_$conversionName", defaultRate)
    }

    fun removeWidgetConversion(id: Int) {
        preference.edit()
            .remove("${id}_$CURRENCY_ONE")
            .remove("${id}_$CURRENCY_TWO")
            .apply()
    }

}
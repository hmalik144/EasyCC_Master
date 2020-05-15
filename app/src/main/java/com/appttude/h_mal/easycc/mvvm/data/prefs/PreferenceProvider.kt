package com.appttude.h_mal.easycc.mvvm.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.appttude.h_mal.easycc.R

private const val CONVERSION_ONE = "conversion_one"
private const val CONVERSION_TWO = "conversion_two"
private const val CONVERSION_ONE_WIDGET = "conversion_one_widget"
private const val CONVERSION_TWO_WIDGET = "conversion_two_widget"

class PreferenceProvider(
    context: Context
) {

    private val appContext = context.applicationContext

    private val preference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    private val defaultRate: String = context.resources.getStringArray(R.array.currency_arrays)[0]


    fun saveConversionPair(s1: String, s2: String) {
        preference.edit().putString(
                CONVERSION_ONE,
                s1
        ).putString(
                CONVERSION_TWO,
                s2
        ).apply()
    }

    fun getConversionPair(): Pair<String?, String?> {
        val s1 = getLastConversionOne()
        val s2 = getLastConversionTwo()

        return Pair(s1,s2)
    }

    private fun getLastConversionOne(): String? {
        return preference.getString(CONVERSION_ONE, defaultRate)
    }

    private fun getLastConversionTwo(): String? {
        return preference.getString(CONVERSION_TWO, defaultRate)
    }

    fun saveWidgetConversionPair(s1: String, s2: String, id: Int) {
        preference.edit().putString(
                "${id}_$CONVERSION_ONE",
                s1
        ).putString(
                "${id}_$CONVERSION_TWO",
                s2
        ).apply()
    }

    fun getWidgetConversionPair(id: Int): Pair<String?, String?> {
        val s1 = getWidgetLastConversionOne(id)
        val s2 = getWidgetLastConversionTwo(id)

        return Pair(s1, s2)
    }

    private fun getWidgetLastConversionOne(id: Int): String? {
        return preference.getString("${id}_$CONVERSION_ONE", defaultRate)
    }

    private fun getWidgetLastConversionTwo(id: Int): String? {
        return preference.getString("${id}_$CONVERSION_TWO", defaultRate)
    }

    fun removeWidgetConversion(id: Int){
        preference.edit().remove("${id}_$CONVERSION_ONE").apply()
        preference.edit().remove("${id}_$CONVERSION_TWO").apply()
    }

}
package com.appttude.h_mal.easycc.mvvm.data.Repository

import android.content.Context
import com.appttude.h_mal.easycc.BuildConfig
import com.appttude.h_mal.easycc.mvvm.data.prefs.PreferenceProvider
import com.appttude.h_mal.easycc.R
import com.appttude.h_mal.easycc.mvvm.data.network.response.ResponseObject
import com.appttude.h_mal.easycc.mvvm.data.network.SafeApiRequest
import com.appttude.h_mal.easycc.mvvm.data.network.api.GetData

class Repository (
        private val api: GetData,
        private val prefs: PreferenceProvider,
        context: Context
): SafeApiRequest(){

    var ccApiKey = BuildConfig.CC_API_KEY

    private val appContext = context.applicationContext

    suspend fun getData(s1: String, s2: String): ResponseObject?{
        return apiRequest{ api.getCurrencyRate(convertPairsListToString(s1, s2),ccApiKey)}
    }

    fun getConversionPair(): List<String?> {
        return prefs.getConversionPair()
    }

    fun setConversionPair(s1: String, s2: String){
        prefs.saveConversionPair(s1, s2)
    }

    private fun convertPairsListToString(s1: String, s2: String): String = "${s1.substring(0,3)}_${s2.substring(0,3)}"

    fun getArrayList(): Array<String> = appContext.resources.getStringArray(R.array.currency_arrays)

    fun getWidgetConversionPairs(id: Int): List<String?> = prefs.getWidgetConversionPair(id)

    fun setWidgetConversionPairs(s1: String, s2: String, id: Int) = prefs.saveWidgetConversionPair(s1, s2, id)

    fun removeWidgetConversionPairs(id: Int) = prefs.removeWidgetConversion(id)

}
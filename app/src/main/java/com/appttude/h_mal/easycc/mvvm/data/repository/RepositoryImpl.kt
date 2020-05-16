package com.appttude.h_mal.easycc.mvvm.data.repository

import android.content.Context
import com.appttude.h_mal.easycc.mvvm.data.prefs.PreferenceProvider
import com.appttude.h_mal.easycc.R
import com.appttude.h_mal.easycc.mvvm.data.network.response.ResponseObject
import com.appttude.h_mal.easycc.mvvm.data.network.SafeApiRequest
import com.appttude.h_mal.easycc.mvvm.data.network.api.CurrencyApi
import com.appttude.h_mal.easycc.mvvm.utils.convertPairsListToString

/**
 * Default implementation of [Repository]. Single entry point for managing currency' data.
 */
class RepositoryImpl (
        private val api: CurrencyApi,
        private val prefs: PreferenceProvider,
        context: Context
):Repository, SafeApiRequest(){

    private val appContext = context.applicationContext

    override suspend fun getData(fromCurrency: String, toCurrency: String
    ): ResponseObject{
        // Set currency pairs as correct string for api query eg. AUD_GBP
        val currencyPair = convertPairsListToString(fromCurrency, toCurrency)
        return responseUnwrap{ api.getCurrencyRate(currencyPair)}
    }

    override fun getConversionPair(): Pair<String?, String?> {
        return prefs.getConversionPair()
    }

    override fun setConversionPair(fromCurrency: String, toCurrency: String){
        prefs.saveConversionPair(fromCurrency, toCurrency)
    }

    override fun getArrayList(): Array<String> =
            appContext.resources.getStringArray(R.array.currency_arrays)

    override fun getWidgetConversionPairs(appWidgetId: Int): Pair<String?, String?> =
            prefs.getWidgetConversionPair(appWidgetId)

    override fun setWidgetConversionPairs(fromCurrency: String,
            toCurrency: String, appWidgetId: Int) {
        return prefs.saveWidgetConversionPair(fromCurrency, toCurrency, appWidgetId)
    }

    override fun removeWidgetConversionPairs(id: Int) =
            prefs.removeWidgetConversion(id)

}
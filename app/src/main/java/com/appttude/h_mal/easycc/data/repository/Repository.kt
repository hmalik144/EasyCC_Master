package com.appttude.h_mal.easycc.data.repository

import com.appttude.h_mal.easycc.models.CurrencyModel

/**
 * Main entry point for accessing currency data.
 */
interface Repository {

    suspend fun getDataFromApi(fromCurrency: String, toCurrency: String): CurrencyModel

    fun getConversionPair(): Pair<String?, String?>

    fun setConversionPair(fromCurrency: String, toCurrency: String)

    fun getCurrenciesList(): Array<String>

    fun getWidgetConversionPairs(appWidgetId: Int): Pair<String?, String?>

    fun setWidgetConversionPairs(fromCurrency: String, toCurrency: String, appWidgetId: Int)

    fun removeWidgetConversionPairs(id: Int)

}
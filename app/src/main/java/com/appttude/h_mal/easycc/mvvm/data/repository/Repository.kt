package com.appttude.h_mal.easycc.mvvm.data.repository

import com.appttude.h_mal.easycc.mvvm.data.network.response.ResponseObject

/**
 * Main entry point for accessing currency data.
 */

interface Repository {

    suspend fun getData(fromCurrency: String, toCurrency: String): ResponseObject

    fun getConversionPair(): Pair<String?, String?>

    fun setConversionPair(fromCurrency: String, toCurrency: String)

    fun getArrayList(): Array<String>

    fun getWidgetConversionPairs(appWidgetId: Int): Pair<String?, String?>

    fun setWidgetConversionPairs(fromCurrency: String, toCurrency: String, appWidgetId: Int)

    fun removeWidgetConversionPairs(id: Int)

}
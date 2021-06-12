package com.appttude.h_mal.easycc.data.repository

import android.content.res.Resources
import com.appttude.h_mal.easycc.R
import com.appttude.h_mal.easycc.data.network.SafeApiRequest
import com.appttude.h_mal.easycc.data.network.api.BackupCurrencyApi
import com.appttude.h_mal.easycc.data.network.api.CurrencyApi
import com.appttude.h_mal.easycc.data.network.response.CurrencyResponse
import com.appttude.h_mal.easycc.data.network.response.ResponseObject
import com.appttude.h_mal.easycc.data.prefs.PreferenceProvider
import com.appttude.h_mal.easycc.utils.convertPairsListToString

/**
 * Default implementation of [Repository]. Single entry point for managing currency' data.
 */
class RepositoryImpl(
    private val api: CurrencyApi,
    private val backUpApi: BackupCurrencyApi,
    private val prefs: PreferenceProvider
) : Repository, SafeApiRequest() {

    override suspend fun getDataFromApi(
        fromCurrency: String,
        toCurrency: String
    ): ResponseObject {
        // Set currency pairs as correct string for api query eg. AUD_GBP
        val currencyPair = convertPairsListToString(fromCurrency, toCurrency)
        return responseUnwrap { api.getCurrencyRate(currencyPair) }
    }

    override suspend fun getBackupDataFromApi(
        fromCurrency: String,
        toCurrency: String
    ): CurrencyResponse {
        return responseUnwrap { backUpApi.getCurrencyRate(fromCurrency, toCurrency) }
    }

    override fun getConversionPair(): Pair<String?, String?> {
        return prefs.getConversionPair()
    }

    override fun setConversionPair(fromCurrency: String, toCurrency: String) {
        prefs.saveConversionPair(fromCurrency, toCurrency)
    }

    override fun getCurrenciesList(): Array<String> =
        Resources.getSystem().getStringArray(R.array.currency_arrays)

    override fun getWidgetConversionPairs(appWidgetId: Int): Pair<String?, String?> =
        prefs.getWidgetConversionPair(appWidgetId)

    override fun setWidgetConversionPairs(
        fromCurrency: String,
        toCurrency: String, appWidgetId: Int
    ) {
        return prefs.saveWidgetConversionPair(fromCurrency, toCurrency, appWidgetId)
    }

    override fun removeWidgetConversionPairs(id: Int) =
        prefs.removeWidgetConversion(id)

}
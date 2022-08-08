package com.appttude.h_mal.easycc.application.modules

import com.appttude.h_mal.easycc.application.TestRunner.Companion.idlingResources
import com.appttude.h_mal.easycc.data.network.response.CurrencyResponse
import com.appttude.h_mal.easycc.data.network.response.ResponseObject
import com.appttude.h_mal.easycc.data.repository.Repository
import com.appttude.h_mal.easycc.models.CurrencyObject
import kotlinx.coroutines.delay
import javax.inject.Inject

class MockRepository @Inject constructor() : Repository {

    override suspend fun getDataFromApi(fromCurrency: String, toCurrency: String): ResponseObject {
        idlingResources.increment()
        delay(500)
        return ResponseObject(
            results = mapOf(
                Pair(
                    "AUD_GBP", CurrencyObject(
                        id = "AUD_GBP",
                        fr = "AUD",
                        to = "GBP",
                        value = 0.546181
                    )
                )
            )
        ).also {
            idlingResources.decrement()
        }
    }

    override suspend fun getBackupDataFromApi(
        fromCurrency: String,
        toCurrency: String
    ): CurrencyResponse {
        idlingResources.increment()
        delay(500)
        return CurrencyResponse(
            rates = mapOf(Pair("GBP", 0.54638)),
            amount = 1.0,
            base = "AUD",
            date = "2021-06-11"
        ).also {
            idlingResources.decrement()
        }
    }

    override fun getConversionPair(): Pair<String?, String?> {
        return Pair("AUD - Australian Dollar", "GBP - British Pound")
    }

    override fun setConversionPair(fromCurrency: String, toCurrency: String) {}

    override fun getCurrenciesList(): Array<String> =
        arrayOf("AUD - Australian Dollar", "GBP - British Pound")

    override fun getWidgetConversionPairs(appWidgetId: Int): Pair<String?, String?> {
        return Pair(null, null)
    }

    override fun setWidgetConversionPairs(
        fromCurrency: String,
        toCurrency: String,
        appWidgetId: Int
    ) {
    }

    override fun removeWidgetConversionPairs(id: Int) {}

}
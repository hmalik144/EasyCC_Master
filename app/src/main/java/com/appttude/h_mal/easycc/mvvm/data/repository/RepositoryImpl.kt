package com.appttude.h_mal.easycc.mvvm.data.repository

import android.content.Context
import com.appttude.h_mal.easycc.BuildConfig
import com.appttude.h_mal.easycc.mvvm.data.prefs.PreferenceProvider
import com.appttude.h_mal.easycc.R
import com.appttude.h_mal.easycc.mvvm.data.network.response.ResponseObject
import com.appttude.h_mal.easycc.mvvm.data.network.SafeApiRequest
import com.appttude.h_mal.easycc.mvvm.data.network.api.CurrencyApi
import com.appttude.h_mal.easycc.mvvm.utils.convertPairsListToString


class RepositoryImpl (
        private val api: CurrencyApi,
        private val prefs: PreferenceProvider,
        context: Context
):Repository, SafeApiRequest(){

    private val appContext = context.applicationContext

    override suspend fun getData(s1: String, s2: String
    ): ResponseObject{
        val currencyPair = convertPairsListToString(s1, s2)
        return responseUnwrap{
            api.getCurrencyRate(currencyPair)}
    }

    override fun getConversionPair(): Pair<String?, String?> {
        return prefs.getConversionPair()
    }

    override fun setConversionPair(s1: String, s2: String){
        prefs.saveConversionPair(s1, s2)
    }

    override fun getArrayList(): Array<String> =
            appContext.resources.getStringArray(R.array.currency_arrays)

    override fun getWidgetConversionPairs(id: Int): Pair<String?, String?> =
            prefs.getWidgetConversionPair(id)

    override fun setWidgetConversionPairs(s1: String, s2: String, id: Int) =
            prefs.saveWidgetConversionPair(s1, s2, id)

    override fun removeWidgetConversionPairs(id: Int) =
            prefs.removeWidgetConversion(id)

}
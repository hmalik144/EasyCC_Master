package com.appttude.h_mal.easycc.mvvm.data.repository

import com.appttude.h_mal.easycc.R
import com.appttude.h_mal.easycc.mvvm.data.network.response.ResponseObject
import com.appttude.h_mal.easycc.mvvm.utils.convertPairsListToString

interface Repository {

    suspend fun getData(s1: String, s2: String): ResponseObject

    fun getConversionPair(): Pair<String?, String?>

    fun setConversionPair(s1: String, s2: String)

    fun getArrayList(): Array<String>

    fun getWidgetConversionPairs(id: Int): Pair<String?, String?>

    fun setWidgetConversionPairs(s1: String, s2: String, id: Int)

    fun removeWidgetConversionPairs(id: Int)

}
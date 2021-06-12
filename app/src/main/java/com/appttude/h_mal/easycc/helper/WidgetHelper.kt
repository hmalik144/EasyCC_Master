package com.appttude.h_mal.easycc.helper

import com.appttude.h_mal.easycc.data.repository.Repository
import com.appttude.h_mal.easycc.models.CurrencyModel
import com.appttude.h_mal.easycc.utils.trimToThree

class WidgetHelper(
    private val helper: CurrencyDataHelper,
    val repository: Repository
) {

    suspend fun getWidgetData(): CurrencyModel? {
        try {
            val pair = repository.getConversionPair()
            val s1 = pair.first?.trimToThree() ?: return null
            val s2 = pair.second?.trimToThree() ?: return null

            return helper.getDataFromApi(s1, s2).getCurrencyModel()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun removeWidgetData(id: Int) {
        repository.removeWidgetConversionPairs(id)
    }
}
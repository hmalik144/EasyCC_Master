package com.appttude.h_mal.easycc.helper

import com.appttude.h_mal.easycc.data.repository.Repository
import com.appttude.h_mal.easycc.models.CurrencyModel
import com.appttude.h_mal.easycc.utils.trimToThree
import javax.inject.Inject

class WidgetHelper @Inject constructor(
    val repository: Repository
) {

    suspend fun getWidgetData(appWidgetId: Int): CurrencyModel? {
        try {
            val pair = repository.getWidgetConversionPairs(appWidgetId)
            val s1 = pair.first?.trimToThree() ?: return null
            val s2 = pair.second?.trimToThree() ?: return null

            return repository.getDataFromApi(s1, s2)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun removeWidgetData(id: Int) {
        repository.removeWidgetConversionPairs(id)
    }
}
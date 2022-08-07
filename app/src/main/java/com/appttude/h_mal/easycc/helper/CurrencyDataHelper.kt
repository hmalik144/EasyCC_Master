package com.appttude.h_mal.easycc.helper

import com.appttude.h_mal.easycc.data.repository.Repository
import com.appttude.h_mal.easycc.models.CurrencyModelInterface
import javax.inject.Inject

class CurrencyDataHelper @Inject constructor(
    val repository: Repository
) {

    suspend fun getDataFromApi(from: String, to: String): CurrencyModelInterface {
        return try {
            repository.getDataFromApi(from, to)
        } catch (e: Exception) {
            e.printStackTrace()
            repository.getBackupDataFromApi(from, to)
        }
    }
}
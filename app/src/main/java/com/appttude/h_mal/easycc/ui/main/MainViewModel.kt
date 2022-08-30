package com.appttude.h_mal.easycc.ui.main

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.appttude.h_mal.easycc.data.repository.Repository
import com.appttude.h_mal.easycc.ui.BaseViewModel
import com.appttude.h_mal.easycc.utils.toTwoDpString
import com.appttude.h_mal.easycc.utils.trimToThree
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

/**
 * ViewModel for the task Main Activity Screen
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
) : BaseViewModel() {

    private val conversionPairs by lazy { repository.getConversionPair() }

    // Viewbinded variables
    var rateIdFrom: String? = null
    var rateIdTo: String? = null

    private var conversionRate: Double = 1.00

    private fun getExchangeRate() {
        onStart()
        if (rateIdFrom.isNullOrEmpty() || rateIdTo.isNullOrEmpty()) {
            onError("Select both currencies")
            return
        }

        if (rateIdFrom == rateIdTo) {
            conversionRate = 1.00
            onError("Currency selections are the same")
            return
        }

        viewModelScope.launch {
            try {
                // Non-null assertion (!!) as values have been null checked and have not changed
                val exchangeResponse = repository.getDataFromApi(
                    rateIdFrom!!.trimToThree(),
                    rateIdTo!!.trimToThree()
                )

                exchangeResponse.let {
                    conversionRate = it.rate
                    repository.setConversionPair(rateIdFrom!!, rateIdTo!!)

                    onSuccess(it)
                    return@launch
                }
            } catch (e: IOException) {
                e.message?.let { onError(it) }
            }
        }
    }

    fun getConversion(fromValue: String): String? {
        return try {
            val fromValDouble = fromValue.toDouble()
            val bottomVal1 = (fromValDouble * conversionRate)
            bottomVal1.toTwoDpString()
        } catch (e: NumberFormatException) {
            null
        }
    }

    fun getReciprocalConversion(toValue: String): String? {
        return try {
            val toDoubleVal = toValue.toDouble()
            val newTopVal = toDoubleVal.times((1 / conversionRate))
            newTopVal.toTwoDpString()
        } catch (e: NumberFormatException) {
            null
        }
    }

    // Start operation based on dialog selection
    fun setCurrencyName(tag: Any?, currencyName: String) {
        when (tag.toString()) {
            "top" -> rateIdFrom = currencyName
            "bottom" -> rateIdTo = currencyName
            else -> {
                return
            }
        }

        getExchangeRate()
    }

    // Start operation based on possible values stored in bundle or retrieve from repository
    fun initiate(extras: Bundle?) {
        rateIdFrom = extras?.getString("parse_1") ?: conversionPairs.first
        rateIdTo = extras?.getString("parse_2") ?: conversionPairs.second

        getExchangeRate()
    }

}
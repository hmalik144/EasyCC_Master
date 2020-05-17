package com.appttude.h_mal.easycc.ui.main

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appttude.h_mal.easycc.data.repository.Repository
import com.appttude.h_mal.easycc.utils.toTwoDpString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * ViewModel for the task Main Activity Screen
 */
private const val TAG = "MainViewModel"
class MainViewModel(
        // Repository injected via Viewmodel factory
        private val repository: Repository
) : ViewModel(){

    private val conversionPairs by lazy { repository.getConversionPair() }

    // Viewbinding to textviews in @activity_main.xml
    var rateIdFrom: String? = null
    var rateIdTo: String? = null

    //operation results livedata based on outcome of operation
    val operationStartedListener = MutableLiveData<Boolean>()
    val operationFinishedListener = MutableLiveData<Pair<Boolean, String?>>()

    private var conversionRate: Double = 1.00

    private fun getExchangeRate(){
        operationStartedListener.postValue(true)

        // Null check on currency values
        if (rateIdFrom.isNullOrEmpty() || rateIdTo.isNullOrEmpty()){
            operationFinishedListener.postValue(Pair(false, "Select currencies"))
            return
        }

        // No need to call api as it will return exchange rate as 1
        if (rateIdFrom == rateIdTo){
            conversionRate = 1.00
            operationFinishedListener.postValue(Pair(true, null))
            return
        }

        // Open Coroutine on IO thread to carry out async task
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Non-null assertion (!!) as values have been null checked and have not changed
                val exchangeResponse = repository.getData(rateIdFrom!!, rateIdTo!!)

                exchangeResponse.results?.iterator()?.next()?.value?.let {
                    // Response Successful and contains @param CurrencyObject
                    repository.setConversionPair(rateIdFrom!!, rateIdTo!!)

                    operationFinishedListener.postValue(Pair(true, null))
                    conversionRate = it.value
                    return@launch
                }
            }catch(e: IOException){
                e.message?.let {
                    operationFinishedListener.postValue(Pair(false, it))
                    return@launch
                }
            }
            operationFinishedListener.postValue(Pair(false, "Failed to retrieve rate"))
        }
    }

    fun getConversion(fromValue: String): String? {
        return try {
            val fromValDouble = fromValue.toDouble()
            val bottomVal1 = (fromValDouble * conversionRate)
            bottomVal1.toTwoDpString()
        }catch (e: NumberFormatException) {
            null
        }
    }

    fun getReciprocalConversion(toValue: String): String? {
        return try {
            val toDoubleVal = toValue.toDouble()
            val newTopVal = toDoubleVal.times((1/conversionRate))
            newTopVal.toTwoDpString()
        } catch (e: NumberFormatException) {
            null
        }
    }

    // Start operation based on dialog selection
    fun setCurrencyName(tag: Any?, currencyName: String){
        when(tag.toString()){
            "top" -> rateIdFrom = currencyName
            "bottom" -> rateIdTo = currencyName
            else -> { return }
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
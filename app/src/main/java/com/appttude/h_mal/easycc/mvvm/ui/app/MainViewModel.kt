package com.appttude.h_mal.easycc.mvvm.ui.app

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appttude.h_mal.easycc.mvvm.data.repository.Repository
import com.appttude.h_mal.easycc.mvvm.utils.toTwoDp
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.DecimalFormat

private const val TAG = "MainViewModel"
class MainViewModel(
        private val repository: Repository
) : ViewModel(){

    private val conversionPairs by lazy { repository.getConversionPair() }

    var rateIdFrom: String? = null
    var rateIdTo: String? = null

    //operation results livedata based on outcome of operation
    val operationStartedListener = MutableLiveData<Boolean>()
    val operationFinishedListener = MutableLiveData<Pair<Boolean, String?>>()

    private var conversionRate: Double = 0.00

    fun getExchangeRate(){

        operationStartedListener.postValue(false)

        if (rateIdFrom.isNullOrEmpty() || rateIdTo.isNullOrEmpty()){
            operationFinishedListener.postValue(Pair(false, "Select currencies"))
            return
        }

        if (rateIdFrom == rateIdTo){
            conversionRate = 1.00
            operationFinishedListener.postValue(Pair(true, null))
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val exchangeResponse = repository.getData(rateIdFrom!!, rateIdTo!!)
                repository.setConversionPair(rateIdFrom!!, rateIdTo!!)

                exchangeResponse.results?.iterator()?.next()?.value?.let {
                    operationFinishedListener.postValue(Pair(true, null))
                    conversionRate = it.value
                    return@launch
                }
            }catch(e: IOException){
                operationFinishedListener.postValue(Pair(false, e.message ?: "Currency Retrieval failed"))
                return@launch
            }

            operationFinishedListener.postValue(Pair(false, "Failed to retrieve rate"))
        }
    }

    fun getConversion(fromValue: String): String? {
        return try {
            val fromValDouble = fromValue.toDouble()
            val bottomVal1 = (fromValDouble * conversionRate).toTwoDp()
            bottomVal1.toBigDecimal().toPlainString()
        }catch (e: NumberFormatException) {
            Log.e(TAG, "no numbers inserted")
            null
        }
    }

    fun getReciprocalConversion(toValue: String): String? {
        return try {
            val toDoubleVal = toValue.toDouble()
            val newTopVal = toDoubleVal.times((1/conversionRate)).toTwoDp()
            newTopVal.toBigDecimal().toPlainString()
        } catch (e: NumberFormatException) {
            Log.e(TAG, "no numbers inserted")
            null
        }
    }

    fun setCurrencyName(tag: Any?, currencyName: String){
        if (tag.toString() == "top"){
            rateIdFrom = currencyName
        }else{
            rateIdTo = currencyName
        }
        getExchangeRate()
    }

    fun initiate(extras: Bundle?) {
        rateIdFrom = extras?.getString("parse_1") ?: conversionPairs.first
        rateIdTo = extras?.getString("parse_2") ?: conversionPairs.second

        getExchangeRate()
    }

}
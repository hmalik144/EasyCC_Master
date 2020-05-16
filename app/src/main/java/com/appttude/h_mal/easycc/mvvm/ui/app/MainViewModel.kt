package com.appttude.h_mal.easycc.mvvm.ui.app

import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appttude.h_mal.easycc.mvvm.data.repository.Repository
import com.appttude.h_mal.easycc.mvvm.utils.toTwoDp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.DecimalFormat

class MainViewModel(
        private val repository: Repository
) : ViewModel(){

    private val defaultValue by lazy { repository.getArrayList()[0] }
    private val conversionPairs by lazy { repository.getConversionPair() }

    var rateIdFrom: String? = conversionPairs.first ?: defaultValue
    var rateIdTo: String? = conversionPairs.second ?: defaultValue

    var topVal: String? = null
    var bottomVal: String? = null

    var rateListener: RateListener? = null

    //operation results livedata based on outcome of operation
    val operationSuccess = MutableLiveData<Pair<Boolean, String>>()
    val currencyRate = MutableLiveData<Double>()

    private var conversionRate: Double = 0.00

    fun getExchangeRate(){
        rateListener?.onStarted()
        if (rateIdFrom.isNullOrEmpty() || rateIdTo.isNullOrEmpty()){
            rateListener?.onFailure("Select currencies")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val exchangeResponse = repository.getData(rateIdFrom!!, rateIdTo!!)
                repository.setConversionPair(rateIdFrom!!, rateIdTo!!)

                exchangeResponse.results?.iterator()?.next()?.value?.let {
                    rateListener?.onSuccess()
                    conversionRate = it.value
                    return@launch
                }

            }catch(e: IOException){
                rateListener?.onFailure(e.message ?: "Currency Retrieval failed")
                return@launch
            }
            rateListener?.onFailure("Failed to retrieve rate")
        }
    }

    fun setBottomValue(fromValue : String, editText: EditText) {
        val fromValDouble = fromValue.toDouble()
        val bottomVal1 = (fromValDouble * conversionRate).toTwoDp()
        editText.setText(bottomVal1.toBigDecimal().toPlainString())
    }

    fun setTopValue(toValue : String, editText: EditText) {
        val toDoubleVal = toValue.toDouble()
        val newTopVal = toDoubleVal.times((1/conversionRate)).toTwoDp()
        editText.setText(newTopVal.toBigDecimal().toPlainString())
    }

    fun start(){
        if (rateIdFrom != rateIdTo){
            getExchangeRate()
        }
    }

}
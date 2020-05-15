package com.appttude.h_mal.easycc.mvvm.ui.widget

import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appttude.h_mal.easycc.mvvm.data.repository.RepositoryImpl
import com.appttude.h_mal.easycc.mvvm.ui.app.RateListener

class WidgetViewModel(
        private val repository: RepositoryImpl
) : ViewModel(){

    var rateListener: RateListener? = null

    var appWidgetId: Int? = null

    var rateIdFrom = MutableLiveData<String>()
    var rateIdTo = MutableLiveData<String>()

    fun initialise(appId: Int){
        appWidgetId = appId
        val widgetString = getWidgetStored(appId)

        rateIdFrom.value = widgetString.first
        rateIdTo.value = widgetString.second

    }

    fun selectCurrencyOnClick(view: View){
        if (appWidgetId == null){
            Toast.makeText(view.context, "No App Widget ID", Toast.LENGTH_LONG).show()
            return
        }

        WidgetItemSelectDialog(view.context, object : DialogResult {
            override fun result(result: String) {
                if (view.tag.toString() == "top"){
                    rateIdFrom.value = result
                }else{
                    rateIdTo.value = result
                }
                Toast.makeText(view.context, result, Toast.LENGTH_LONG).show()
            }
        }).show()
    }

    fun submitSelectionOnClick(view: View){
        if (appWidgetId == null){
            rateListener?.onFailure("No App Widget ID")
            return
        }

        if (rateIdFrom.value == rateIdTo.value){
            rateListener?.onFailure("Selected rates cannot be the same")
            return
        }

        rateListener?.onSuccess()
    }

    fun getWidgetStored(id: Int) = repository.getWidgetConversionPairs(id)

    fun setWidgetStored() {
        if (rateIdTo.value == null && rateIdFrom.value == null){
            rateListener?.onFailure("Selections incomplete")
            return
        }

        if (rateIdFrom.value == rateIdTo.value){
            rateListener?.onFailure("Selected rates cannot be the same")
            return
        }

        repository.setWidgetConversionPairs(rateIdFrom.value!!,rateIdTo.value!!,appWidgetId!!)
    }

    fun getWidgetStringName() = "${rateIdFrom.value!!.trimToThree()}${rateIdTo.value!!.trimToThree()}"

    private fun String.trimToThree() = this.substring(0,3)


}
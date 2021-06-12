package com.appttude.h_mal.easycc.ui.widget

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appttude.h_mal.easycc.data.repository.Repository
import com.appttude.h_mal.easycc.utils.trimToThree

class WidgetViewModel(
    private val repository: Repository
) : ViewModel() {

    private val defaultCurrency: String by lazy { repository.getCurrenciesList()[0] }
    var appWidgetId: Int? = null

    // data binding to @R.layout.currency_app_widget_configure
    var rateIdFrom: String? = null
    var rateIdTo: String? = null

    // Live data to feedback to @CurrencyAppWidgetConfigureActivityKotlin
    val operationFinishedListener = MutableLiveData<Pair<Boolean, String?>>()

    // Setup viewmodel app widget ID
    // Set default values for text views
    fun initiate(appId: Int) {
        appWidgetId = appId
        val widgetString = repository.getWidgetConversionPairs(appId)

        rateIdFrom = widgetString.first ?: defaultCurrency
        rateIdTo = widgetString.second ?: defaultCurrency

    }

    // Retrieve name for submit dialog (eg. AUDGBP)
    fun getSubmitDialogMessage(): String {
        val widgetName = getWidgetStringName()
        return StringBuilder().append("Create widget for ")
            .append(widgetName)
            .append("?").toString()
    }

    fun submitSelectionOnClick() {
        if (rateIdTo == null || rateIdFrom == null) {
            operationFinishedListener.value = Pair(false, "Selections incomplete")
            return
        }
        if (rateIdFrom == rateIdTo) {
            operationFinishedListener.value =
                Pair(false, "Selected rates cannot be the same ${rateIdFrom}${rateIdTo}")
            return
        }
        operationFinishedListener.value = Pair(true, null)
    }

    fun setWidgetStored() {
        repository.setWidgetConversionPairs(rateIdFrom!!, rateIdTo!!, appWidgetId!!)
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
    }

    private fun getWidgetStringName() = "${rateIdFrom!!.trimToThree()}${rateIdTo!!.trimToThree()}"

}
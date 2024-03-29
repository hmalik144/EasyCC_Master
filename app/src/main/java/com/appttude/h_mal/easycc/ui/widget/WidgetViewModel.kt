package com.appttude.h_mal.easycc.ui.widget

import com.appttude.h_mal.easycc.data.repository.Repository
import com.appttude.h_mal.easycc.ui.BaseViewModel
import com.appttude.h_mal.easycc.utils.trimToThree
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WidgetViewModel @Inject constructor(
    private val repository: Repository
) : BaseViewModel() {

    private val defaultCurrency: String by lazy { repository.getCurrenciesList()[0] }
    var appWidgetId: Int? = null

    // data binding to @R.layout.currency_app_widget_configure
    var rateIdFrom: String? = null
    var rateIdTo: String? = null

    // Live data to feedback to @CurrencyAppWidgetConfigureActivityKotlin

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
            onError("Selections incomplete")
            return
        }
        if (rateIdFrom == rateIdTo) {
            onError("Selected rates cannot be the same")
            return
        }
        onSuccess(Unit)
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
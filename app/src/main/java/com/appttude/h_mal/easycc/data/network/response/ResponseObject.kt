package com.appttude.h_mal.easycc.data.network.response

import com.appttude.h_mal.easycc.models.CurrencyModel
import com.appttude.h_mal.easycc.models.CurrencyModelInterface
import com.appttude.h_mal.easycc.models.CurrencyObject
import com.google.gson.annotations.SerializedName

data class ResponseObject(
        @field:SerializedName("query")
        var query : Any? = null,
        @field:SerializedName("results")
        var results : Map<String, CurrencyObject>? = null
): CurrencyModelInterface {

        override fun getCurrencyModel(): CurrencyModel {
                val res = results?.iterator()?.next()?.value
                return CurrencyModel(
                        res?.fr,
                        res?.to,
                        res?.value ?: 0.0
                )
        }

}
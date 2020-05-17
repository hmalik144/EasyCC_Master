package com.appttude.h_mal.easycc.data.network.response

import com.appttude.h_mal.easycc.models.CurrencyObject
import com.google.gson.annotations.SerializedName

class ResponseObject(
        @SerializedName("query")
        var query : Any,
        @SerializedName("results")
        var results : Map<String, CurrencyObject>?
)
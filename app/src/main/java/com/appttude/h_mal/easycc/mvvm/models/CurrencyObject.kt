package com.appttude.h_mal.easycc.mvvm.models

import com.google.gson.annotations.SerializedName

data class CurrencyObject (
        @SerializedName("id")
        var id : String,
        @SerializedName("fr")
        var fr : String,
        @SerializedName("to")
        var to : String,
        @SerializedName("val")
        var value: Double
)
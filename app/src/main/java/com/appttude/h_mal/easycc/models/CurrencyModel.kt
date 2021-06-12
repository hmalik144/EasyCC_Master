package com.appttude.h_mal.easycc.models

data class CurrencyModel(
    val from: String?,
    val to: String?,
    var rate: Double = 0.0
)

interface CurrencyModelInterface {
    fun getCurrencyModel(): CurrencyModel
}
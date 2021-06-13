package com.appttude.h_mal.easycc.data.network.response

import com.appttude.h_mal.easycc.models.CurrencyModel
import com.appttude.h_mal.easycc.models.CurrencyModelInterface
import com.google.gson.annotations.SerializedName

data class CurrencyResponse(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("amount")
	val amount: Double? = null,

	@field:SerializedName("rates")
	var rates: Map<String, Double>? = null,

	@field:SerializedName("base")
	val base: String? = null
) : CurrencyModelInterface {

    override fun getCurrencyModel(): CurrencyModel {
        return CurrencyModel(
			base,
			rates?.iterator()?.next()?.key,
			rates?.iterator()?.next()?.value ?: 0.0
		)
    }

}

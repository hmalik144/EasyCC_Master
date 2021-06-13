package com.appttude.h_mal.easycc.utils

import java.lang.Double.valueOf
import java.text.DecimalFormat

fun transformIntToArray(int: Int): IntArray {
    return intArrayOf(int)
}

fun String.trimToThree(): String {
    val size = length
    return when {
        size > 3 -> substring(0, 3)
        else -> this
    }
}

fun convertPairsListToString(s1: String, s2: String): String =
    "${s1.trimToThree()}_${s2.trimToThree()}"

fun Double.toTwoDp() = run {
    try {
        val df = DecimalFormat("0.00")
        valueOf(df.format(this))
    } catch (e: NumberFormatException) {
        e.printStackTrace()
        this
    }

}

fun Double.toTwoDpString(): String {
    return this.toTwoDp().toBigDecimal().toPlainString()
}
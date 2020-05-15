package com.appttude.h_mal.easycc.mvvm.utils

fun transformIntToArray(int: Int): IntArray{
    return intArrayOf(int)
}

fun String.trimToThree(): String{
    if (this.length > 3){
        return this.substring(0, 3)
    }
    return this
}

fun convertPairsListToString(s1: String, s2: String): String =
        "${s1.trimToThree()}_${s2.trimToThree()}"
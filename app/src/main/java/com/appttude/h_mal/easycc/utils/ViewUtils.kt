package com.appttude.h_mal.easycc.utils

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.Toast

fun EditText.clearEditText() {
    this.setText("")
}

fun View.hideView(vis: Boolean) {
    visibility = if (vis) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

fun Context.displayToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

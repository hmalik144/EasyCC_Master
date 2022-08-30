package com.appttude.h_mal.easycc.utils

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.AnimRes

fun EditText.clearEditText() {
    this.setText("")
}

fun Context.displayToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun View.triggerAnimation(@AnimRes id: Int, complete: (View) -> Unit) {
    val animation = AnimationUtils.loadAnimation(context, id)
    animation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationEnd(animation: Animation?) = complete(this@triggerAnimation)
        override fun onAnimationStart(a: Animation?) {}
        override fun onAnimationRepeat(a: Animation?) {}
    })
    startAnimation(animation)
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

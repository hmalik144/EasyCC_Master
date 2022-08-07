package com.appttude.h_mal.easycc.application

import android.app.Application
import android.content.Context
import androidx.test.espresso.idling.CountingIdlingResource

import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication


class TestRunner : AndroidJUnitRunner() {

    companion object {
        val idlingResources = CountingIdlingResource("Data_loader")
    }

    @Throws(
        InstantiationException::class,
        IllegalAccessException::class,
        ClassNotFoundException::class
    )
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
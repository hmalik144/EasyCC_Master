package com.appttude.h_mal.easycc.mvvm.ui.app

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.ArrayAdapter
import com.appttude.h_mal.easycc.R
import kotlinx.android.synthetic.main.custom_dialog.*

class CustomDialogClass(
        context: Context,
        private val clickListener: ClickListener
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_dialog)

        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val arrayAdapter =
                ArrayAdapter.createFromResource(
                        context, R.array.currency_arrays,
                        android.R.layout.simple_list_item_1)

        list_view.adapter = arrayAdapter

        search_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                arrayAdapter.filter.filter(charSequence)
            }
            override fun afterTextChanged(editable: Editable) {}
        })

        list_view.setOnItemClickListener{ adapterView, _, i, _ ->
            clickListener.onText(adapterView.getItemAtPosition(i).toString())
            dismiss()
        }
    }
}

interface ClickListener{
    fun onText(currencyName: String)
}
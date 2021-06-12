package com.appttude.h_mal.easycc.ui.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.appttude.h_mal.easycc.R
import kotlinx.android.synthetic.main.confirm_dialog.*


/**
 * Dialog created when submitting the completed selections
 * in [CurrencyAppWidgetConfigureActivityKotlin]
 */
class WidgetSubmitDialog(
    context: Context,
    private val messageString: String,
    private val dialogInterface: DialogSubmit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.confirm_dialog)
        // layer behind dialog to be transparent
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        // Dialog cannot be cancelled by clicking away
        setCancelable(false)

        confirm_text.text = messageString

        // handle dialog buttons
        confirm_yes.setOnClickListener { dialogInterface.onSubmit() }
        confirm_no.setOnClickListener { dismiss() }
    }
}

interface DialogSubmit {
    fun onSubmit()
}
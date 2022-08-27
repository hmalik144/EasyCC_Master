package com.appttude.h_mal.easycc.ui.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.appttude.h_mal.easycc.databinding.ConfirmDialogBinding


/**
 * Dialog created when submitting the completed selections
 * in [CurrencyAppWidgetConfigureActivityKotlin]
 */
class WidgetSubmitDialog(
    context: Context,
    private val messageString: String,
    private val dialogInterface: DialogSubmit
) : Dialog(context) {

    private lateinit var binding: ConfirmDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ConfirmDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // layer behind dialog to be transparent
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        // Dialog cannot be cancelled by clicking away
        setCancelable(false)

        binding.confirmText.text = messageString

        // handle dialog buttons
        binding.confirmYes.setOnClickListener { dialogInterface.onSubmit() }
        binding.confirmNo.setOnClickListener { dismiss() }
    }
}

interface DialogSubmit {
    fun onSubmit()
}
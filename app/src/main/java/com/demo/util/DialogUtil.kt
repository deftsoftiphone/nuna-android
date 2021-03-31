package com.demo.util


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.view.WindowManager
import com.demo.R
import kotlinx.android.synthetic.main.layout_custom_dialog.*
import kotlinx.android.synthetic.main.layout_delete_post_dialog.*
import kotlinx.android.synthetic.main.layout_logout_dialog.*

class DialogUtil() {

    var dialog: Dialog? = null

    private constructor(builder: Builder) : this() {
        if (dialog != null) {
            dialog?.dismiss()
        }
        dialog = Dialog(builder.context)
        when (builder.dialogType) {

            //dialog type success
            DialogType.SUCCESS -> {
//                dialog?.setContentView(R.layout.layout_custom_dialog)
//                dialog?.btOkay?.setOnClickListener {
//                    dialog?.cancel()
//                    builder.successClickListener?.onOkayClick()
//                }
            }

            //dialog type error
            DialogType.ERROR -> {

            }

            //dialog type input
            DialogType.INPUT_TYPE -> {
//                dialog?.setContentView(R.layout.layout_custom_input_dialog)
//                if (!builder.message.isNullOrBlank()) {
//                    if (!builder.message.equals("0")) {
//                        dialog?.etData?.setText(builder.message)
//                    }
//                }
//                dialog?.btnDone?.setOnClickListener {
//                    dialog?.cancel()
//                    builder.inputDialogListener?.fetchValue(dialog?.etData?.text.toString())
//                }
//                dialog?.btnCancel?.setOnClickListener {
//                    dialog?.cancel()
//                }
            }

            DialogType.LOGOUT -> {
                dialog?.setContentView(R.layout.layout_logout_dialog)
                dialog?.btnYes?.setOnClickListener {
                    dialog?.cancel()
                    builder.yesNoDialogClickListener?.onClickYes()
                }
                dialog?.btnNo?.setOnClickListener {
                    dialog?.cancel()
                    builder.yesNoDialogClickListener?.onClickNo()
                }
            }

            DialogType.DELETE -> {
                dialog?.setContentView(R.layout.layout_delete_post_dialog)
                dialog?.btnCancel?.setOnClickListener {
                    dialog?.cancel()
                    builder.yesNoDialogClickListener?.onClickNo()
                }
                dialog?.btnDelete?.setOnClickListener {
                    dialog?.cancel()
                    builder.yesNoDialogClickListener?.onClickYes()
                }

                ClickGuard.guard(dialog?.btnCancel)
                ClickGuard.guard(dialog?.btnDelete)
            }

            DialogType.INPUT_TYPE_DESTINATION -> {
//                dialog?.setContentView(R.layout.layout_custom_input_dialog)
//                dialog?.etData?.visibility = View.GONE
//                dialog?.llDestination?.visibility = View.VISIBLE
//                dialog?.btnDone?.setOnClickListener {
//                    dialog?.cancel()
//                    builder.inputDialogListener?.fetchValue(dialog?.etDestination?.text.toString())
//                }
//                dialog?.btnCancel?.setOnClickListener {
//                    dialog?.cancel()
//                }
            }

            //dialog type yes_no
            DialogType.ALERT -> {
//                dialog?.setContentView(R.layout.dialog_alert)
//                dialog?.btYes?.setOnClickListener {
//                    dialog?.cancel()
//                    builder.yesNoDialogClickListener?.onClickYes()
//                }
//                dialog?.btNo?.setOnClickListener {
//                    dialog?.cancel()
//                    builder.yesNoDialogClickListener?.onClickNo()
//                }
            }

            //dialog type yes_no
            DialogType.YES_NO_TYPE -> {

            }


        }
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )


        if (dialog != null) {
            val displayMetrics = DisplayMetrics()
            dialog?.window!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog?.window!!.attributes)
            val dialogWindowWidth = (displayMetrics.widthPixels * 0.9f).toInt()
            val dialogWindowHeight = (displayMetrics.heightPixels * 0.5f).toInt()
            layoutParams.width = dialogWindowWidth
            layoutParams.height = dialogWindowHeight
            dialog?.window!!.attributes = layoutParams
//            dialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        }
//        dialog?.tvTitle?.text = builder.title
        dialog?.tvMessage?.text = builder.message
        dialog?.show()
    }

    companion object {
        inline fun build(context: Context, block: Builder.() -> Unit) =
            Builder(context).apply(block).build()
    }

    class Builder(val context: Context) {

        var title: String? = null
        var message: String? = null
        var dialogType: DialogType? = null
        var yesNoDialogClickListener: YesNoDialogClickListener? = null
        var successClickListener: SuccessClickListener? = null
        var errorClickListener: ErrorClickListener? = null
        var inputDialogListener: InputDialogListener? = null

        fun build() {
            when {
                title.isNullOrEmpty() && dialogType != DialogType.LOGOUT -> {
                    throw IllegalArgumentException("Dialog title required.")
                }
                message.isNullOrEmpty() && dialogType != DialogType.LOGOUT -> {
                    throw IllegalArgumentException("Dialog message required.")
                }
                dialogType == null -> {
                    throw IllegalArgumentException("Dialog type required.")
                }
                else -> {
                    if (dialogType == DialogType.SUCCESS && successClickListener == null) {
                        throw IllegalArgumentException("Dialog SuccessClickListener required.")
                    } else if (dialogType == DialogType.ERROR && errorClickListener == null) {
                        throw IllegalArgumentException("Dialog ErrorClickListener required.")
                    } else if (dialogType == DialogType.YES_NO_TYPE && yesNoDialogClickListener == null) {
                        throw IllegalArgumentException("Dialog YesNoClickListener required.")
                    } else if (dialogType == DialogType.INPUT_TYPE && inputDialogListener == null) {
                        throw IllegalArgumentException("Dialog inputDialogListener required.")
                    } else {
                        DialogUtil(this)
                    }
                }
            }
        }
    }

    enum class DialogType {
        ALERT,
        ERROR,
        SUCCESS,
        YES_NO_TYPE,
        INPUT_TYPE,
        INPUT_TYPE_DESTINATION,
        LOGOUT,
        DELETE
    }

    interface InputDialogListener {
        fun fetchValue(string: String)
    }

    interface SuccessClickListener {
        fun onOkayClick()
    }

    interface ErrorClickListener {
        fun onOkayClick()
    }

    interface YesNoDialogClickListener {
        fun onClickYes()
        fun onClickNo()
    }
}







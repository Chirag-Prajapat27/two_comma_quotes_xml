package com.app.twocommaquotes.customclasses

import android.app.Activity
import android.app.Dialog
import android.widget.TextView
import com.app.twocommaquotes.R

//This class is use to display progressbar when api get response from server
class CustomDialog private constructor() {
    private var dialog: Dialog? = null

    val isDialogShowing: Boolean
        get() = if (dialog != null && dialog!!.isShowing) {
            dialog!!.isShowing
        } else {
            false
        }

    /**
     * Show progress bar...
     *
     * @param mContext : the context in which the dialog should run
     */
    fun showDialog(mContext: Activity?) {
        dialog = Dialog(mContext!!, R.style.DialogTheme)
        dialog!!.setCancelable(false)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setContentView(R.layout.item_loader)
        show()
    }

    //show progressbar with message
    fun showDialog(mContext: Activity?, message: String?) {
        dialog = Dialog(mContext!!, R.style.DialogTheme)
        dialog!!.setCancelable(false)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setContentView(R.layout.item_loader_message)

        val mTxtMessage: TextView = dialog!!.findViewById(R.id.tv_txtMessage)
        mTxtMessage.text = message
        show()
    }

    //hide progressbar
    fun hide() {
        if (dialog != null) {
            dialog!!.dismiss()
        }
    }

    //show progressbar
    private fun show() {
        try {
            if (dialog != null) {
                if (!dialog!!.isShowing) {
                    dialog!!.show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private var instance: CustomDialog? = null

        fun getInstance(): CustomDialog {
            if (instance == null) {
                instance = CustomDialog()
            }
            return instance as CustomDialog
        }
    }
}
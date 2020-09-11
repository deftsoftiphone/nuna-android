package com.demo.util

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog


object AlertDialogUtils {

    fun showDialog(context: Context, title: String, message: String, positiveButtonText: String, negativeButtonText: String, onClick: DialogInterface.OnClickListener) {

        val alertDialog = AlertDialog.Builder(context)

        // Setting Dialog Title
        alertDialog.setTitle(title)

        // Setting Dialog Message
        alertDialog.setMessage(message)

        alertDialog.setPositiveButton(positiveButtonText, onClick)
        alertDialog.setNegativeButton(negativeButtonText, onClick)

        // Showing Alert Message
        alertDialog.create().show()

    }

    fun showDialog(context: Context, title: String, message: String, positiveButtonText: String, onClick: DialogInterface.OnClickListener) {

        val alertDialog = AlertDialog.Builder(context)

        // Setting Dialog Title
        alertDialog.setTitle(title)

        // Setting Dialog Message
        alertDialog.setMessage(message)

        alertDialog.setPositiveButton(positiveButtonText, onClick)

        // Showing Alert Message
        alertDialog.create().show()

    }


    fun showDialogWithSelection(context: Context, title: String, items: Array<String>, onClick: DialogInterface.OnClickListener) {

        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setItems(items,onClick)
        builder.create().show()
    }



}
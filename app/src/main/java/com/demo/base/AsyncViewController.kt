package com.demo.base

import android.content.DialogInterface

interface AsyncViewController {

    fun showProgressDialog()

    fun hideProgressDialog()

    fun showAlertDialog(msg: String, btnListener : DialogInterface.OnClickListener?)

    fun showAlertDialog(title : String, msg: String, btnPosTxt : String, btnNegTxt : String, btnListener : DialogInterface.OnClickListener?)

    fun hideAlertDialog()

    fun hideKeyboard()

    fun showKeyboard()
    fun setLang(strLang: String)

    fun onNoInternet()

    fun onApiCallFailed(apiUrl: String, errCode: Int, errorMessage: String) : Boolean

    fun hasPermission(s: String): Boolean
}
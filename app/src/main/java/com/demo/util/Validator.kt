package com.demo.util

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.ObservableField
import com.banuba.sdk.core.ui.ext.layoutInflater
import com.demo.R
import com.demo.base.MainApplication
import com.demo.model.request.RequestChangePassword


class Validator {


    companion object {

        private var toast: Toast? = null
        val myApp = MainApplication.get()
        val currentActivity: Activity? = myApp.getCurrentActivity()

        fun showCustomToast(msg: String) {
            val inflater = myApp.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater.inflate(com.demo.R.layout.layout_toast_custom, null, false)
            val tv = layout.findViewById(com.demo.R.id.txtvw) as TextView
            tv.text = msg
            val toast = Toast(MainApplication.get())
            toast.duration = Toast.LENGTH_SHORT
            toast.view = layout
            toast.setGravity(Gravity.BOTTOM, 0, 100)
            toast.show()
        }

        fun showMessage(msg: String) {
            toast?.cancel()
            toast = Toast(MainApplication.get())
            toast?.let { toast ->
                myApp.getContext()?.let {
                    val toastView = it.layoutInflater.inflate(
                        R.layout.custom_toast_layout,
                        currentActivity?.findViewById(R.id.toast_container)
                    )
                    val tvMessage: TextView = toastView.findViewById(R.id.tvMessage)
                    tvMessage.text = msg
                    toast.view = toastView
                    toast.setGravity(Gravity.BOTTOM, 0, 280)
                    toast.duration = Toast.LENGTH_LONG
                    toast.show()
                }
            }
        }

        fun isSavePostTitleValid(otp: String?, errMsgHolder: ObservableField<String>): Boolean {
            if (TextUtils.isEmpty(otp)) {
                errMsgHolder.set(
                    MainApplication.get().getString(com.demo.R.string.err_discription_missing)
                )
                showMessage(errMsgHolder.get() ?: "")
                return false
            } else errMsgHolder.set("")

            return true
        }


        fun isSavePostCategoryValid(otp: String?, errMsgHolder: ObservableField<String>): Boolean {
            if (TextUtils.isEmpty(otp)) {
                errMsgHolder.set(MainApplication.get().getString(com.demo.R.string.select_category))
                showMessage(errMsgHolder.get() ?: "")
                return false
            } else errMsgHolder.set("")

            return true
        }

        fun isSavePostDicriptionValid(
            otp: String?,
            errMsgHolder: ObservableField<String>
        ): Boolean {
            if (otp?.isEmpty() == true) {
                errMsgHolder.set(
                    MainApplication.get().getString(com.demo.R.string.err_discription_missing)
                )
                showCustomToast(errMsgHolder.get() ?: "")
                return false
            } else errMsgHolder.set("")


            return true
        }

        fun isOTPValid(otp: String?, errMsgHolder: ObservableField<String>): Boolean {
            if (otp?.isEmpty() == true) {
                errMsgHolder.set(MainApplication.get().getString(com.demo.R.string.err_otp_missing))
                showMessage(errMsgHolder.get() ?: "")
                return false
            } else errMsgHolder.set("")


            return true
        }

        fun isPhoneValid(phone: String?, errMsgHolder: ObservableField<String>): Boolean {
            if (phone?.isEmpty() == true) {
                errMsgHolder.set(
                    MainApplication.get().getString(com.demo.R.string.err_mobile_missing)
                )
                showCustomToast(errMsgHolder.get() ?: "")
                return false
            } else errMsgHolder.set("")

            if (phone?.length != 10) {
                errMsgHolder.set(
                    MainApplication.get().getString(com.demo.R.string.err_mobile_missing)
                )
                showCustomToast(errMsgHolder.get() ?: "")
                return false
            } else errMsgHolder.set("")

            return true
        }

        fun isEmailValid(email: String?, errMsgHolder: ObservableField<String>): Boolean {
            if (email?.isEmpty() == true) {
                errMsgHolder.set(MainApplication.get().getString(com.demo.R.string.enter_email_ID))
                showCustomToast(errMsgHolder.get() ?: "")
                return false
            } else errMsgHolder.set("")

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                errMsgHolder.set(MainApplication.get().getString(com.demo.R.string.enter_email_ID))
                showCustomToast(errMsgHolder.get() ?: "")
                return false
            } else errMsgHolder.set("")

            return true
        }

        fun isPasswordValid(password: String?, errMsgHolder: ObservableField<String>): Boolean {
            if (password?.isEmpty() == true) {
                errMsgHolder.set(
                    MainApplication.get().getString(com.demo.R.string.err_password_missing)
                )
                showCustomToast(errMsgHolder.get() ?: "")
                return false
            } else errMsgHolder.set("")

            if (password!!.length !in 3..20) {
                errMsgHolder.set(
                    MainApplication.get().getString(com.demo.R.string.err_password_length_invalid)
                )
                showCustomToast(errMsgHolder.get() ?: "")
                return false
            } else errMsgHolder.set("")

            return true
        }

        fun validateChangePasswordForm(
            data: RequestChangePassword,
            errOldPassword: ObservableField<String>,
            errNewPassword: ObservableField<String>,
            errConfirmPassword: ObservableField<String>
        ): Boolean {

            val context: Context = MainApplication.get().getContext()

            errOldPassword.set("")
            errNewPassword.set("")
            errConfirmPassword.set("")

            if (data.oldPassword.isEmpty()) {
                errOldPassword.set(context.getString(com.demo.R.string.err_old_password_missing))
                showCustomToast(errOldPassword.get() ?: "")
                return false
            }

            if (data.newPassword.isEmpty()) {
                errNewPassword.set(context.getString(com.demo.R.string.err_new_password_missing))
                showCustomToast(errNewPassword.get() ?: "")
                return false
            }

            if (data.newPassword.length < 3) {
                errNewPassword.set(context.getString(com.demo.R.string.err_new_password_min_length))
                showCustomToast(errNewPassword.get() ?: "")
                return false
            }

            if (data.newPassword.length > 20) {
                errNewPassword.set(context.getString(com.demo.R.string.err_new_password_max_length))
                showCustomToast(errNewPassword.get() ?: "")
                return false
            }

            if (data.confirmPassword.isEmpty()) {
                errConfirmPassword.set(context.getString(com.demo.R.string.err_confirm_password_missing))
                showCustomToast(errConfirmPassword.get() ?: "")
                return false
            }

            if (data.newPassword.trim() != data.confirmPassword.trim()) {
                errConfirmPassword.set(context.getString(com.demo.R.string.err_passwords_not_same))
                showCustomToast(errConfirmPassword.get() ?: "")
                return false
            }

            /*if (data.oldPassword.trim() == data.newPassword.trim()){
                errConfirmPassword.set(context.getString(R.string.err_new_password_matches_new_password))
                return false
            }*/

            return true
        }
    }

}
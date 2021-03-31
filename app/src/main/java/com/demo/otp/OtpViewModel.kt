package com.demo.otp

import android.os.Bundle
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import com.demo.base.ParentViewModel
import com.demo.model.request.RequestUserRegister
import com.demo.model.request.RequestValidate
import com.demo.model.response.MasterResponse
import com.demo.model.response.ResponseLogin
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.login.LoginRepository
import com.demo.util.ParcelKeys
import java.util.regex.Matcher
import java.util.regex.Pattern

class OtpViewModel(
//    controller: AsyncViewController,
    private val loginRepository: LoginRepository,
    private val resources: ResourcesProvider
) : ParentViewModel() {
    val requestRegister = ObservableField<RequestUserRegister>()
    var phoneNumberS: String? = ""
    var countryCallingCodeS: String? = ""

    val requestOtp = ObservableField<RequestValidate>()

    init {
        requestRegister.set(RequestUserRegister())
        requestOtp.set(RequestValidate())
    }

    fun isOTPValid(): Boolean {
        val data = requestOtp.get()
        if (data!!.otpCode.toString().isEmpty() || data.otpCode.toString().length < 4) {
//            Validator.showMessage(resources.getString(R.string.invalidOtpErr))
            MasterResponse<ResponseLogin>()
            return false

        }
        return true
    }

    fun isOTPEmpty(): Boolean {
        val data = requestOtp.get()
        return TextUtils.isEmpty(data!!.otpCode.toString())
    }

    fun parseBundle(arguments: Bundle?) {
        phoneNumberS = arguments?.getString(ParcelKeys.PHONENUMBER)
        countryCallingCodeS = arguments?.getString(ParcelKeys.COUNTRYCODE)

        val requestValidate = RequestValidate(phoneNumberS)
        requestValidate.countryCallingCode = countryCallingCodeS
        requestValidate.phoneNumber = phoneNumberS
        requestOtp.set(requestValidate)

        val requestRegisterl = RequestUserRegister(phoneNumberS)
//        requestRegisterl.countryCallingCode = countryCallingCodeS
        requestRegisterl.mobileNo = phoneNumberS
        requestRegister.set(requestRegisterl)
    }

    suspend fun resendOTP(): LiveData<BaseResponse> {
        return loginRepository.login(requestRegister.get()!!)
    }

    suspend fun verifyOTP(): LiveData<BaseResponse> {
        return loginRepository.verifyOTP(requestOtp.get()!!)
    }

    fun getOTP(message: String): String {
        val OTP_REGEX: String? = "[0-9]{1,4}"
        val p: Pattern = Pattern.compile(OTP_REGEX)
        val m: Matcher = p.matcher(message)
        m.find()
        println(m.group())
        return m.group()
    }
}
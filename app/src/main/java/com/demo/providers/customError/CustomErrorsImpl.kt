package com.demo.providers.customError

import com.demo.R
import com.demo.providers.resources.ResourcesProvider

class CustomErrorsImpl(private val resources: ResourcesProvider) : CustomErrors {
    override fun otpErrorMsg(defaultMsg: String?): String {
        return when (defaultMsg) {
            resources.getString(R.string.emptyOTPMsg) -> resources.getString(
                R.string.invalidOtpErr
            )
            resources.getString(R.string.invalidOTPMsg) -> resources.getString(
                R.string.invalidOtpErr
            )
            else -> resources.getString(R.string.something_went_wrong)
        }
    }

    override fun postErrorMsg(defaultMsg: String?): String {
        return when (defaultMsg) {
            resources.getString(R.string.noHashtagMsg) -> resources.getString(
                R.string.no_hashtags_available
            )
            else -> resources.getString(R.string.something_went_wrong)
        }
    }
}
package com.demo.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Response(

	@field:SerializedName("gujrati")
	val gujrati: String? = null,

	@field:SerializedName("marathi")
	val marathi: String? = null,

	@field:SerializedName("tamil")
	val tamil: String? = null,

	@field:SerializedName("english")
	val english: String? = null,

	@field:SerializedName("bengali")
	val bengali: String? = null,

	@field:SerializedName("hindi")
	val hindi: String? = null,

	@field:SerializedName("noOfPostAssociated")
	val noOfPostAssociated: String? = null,

	@field:SerializedName("kannada")
	val kannada: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("telugu")
	val telugu: String? = null,

	@field:SerializedName("isOther")
	val isOther: Boolean? = null,

	@field:SerializedName("categoryName")
	val categoryName: String? = null
) : Parcelable

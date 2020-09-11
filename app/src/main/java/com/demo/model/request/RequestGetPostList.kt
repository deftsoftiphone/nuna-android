package com.demo.model.request

import android.location.Location
import com.demo.util.Prefs
import com.google.gson.annotations.SerializedName


data class RequestGetPostList(





	@field:SerializedName("followingUsersPost")
      var followingUsersPost: Boolean = false,

	@field:SerializedName("searchKeyWord")
	var searchKeyWord: String = "",

	@field:SerializedName("categoryIds")
	var categoryIds: String = "",

	@field:SerializedName("latitude")
	var latitude: Double? = null,

	@field:SerializedName("longitude")
	var longitude: Double? = null,

	@field:SerializedName("targetUserId")
	var targetUserId: Int = 0,

	@field:SerializedName("languageId")
	var languageId: Int = 0,



	@field:SerializedName("userId")
	var userId: Int? = Prefs.init().loginData?.userId,

	@field:SerializedName("radios")
	var radios: Double = 100.0,

	@field:SerializedName("pageNumber")
	var pageNumber: Int = 0,

	@field:SerializedName("pageSize")
	var pageSize: Int = 8
) {

	fun addCategory(id : Int){
		if (categoryIds.isBlank()) {
			categoryIds = id.toString()
			return
		}
		val currSet = categoryIds.split(",").toHashSet()
		if (!currSet.contains(id.toString())){
			currSet.add(id.toString())
		}
		categoryIds = currSet.joinToString().replace(" ","")
	}

	fun removeCategory(id : Int){
		val currSet = categoryIds.split(",").toHashSet()
		if (currSet.contains(id.toString())){
			currSet.remove(id.toString())
		}
		categoryIds = currSet.joinToString().replace(" ","")
	}

	fun toggleLocation(location : Location?, turnOn : Boolean){
		if (turnOn && location!=null){
			latitude = location.latitude
			longitude = location.longitude
		}else{
			latitude = 0.0
			longitude = 0.0
		}
	}
}
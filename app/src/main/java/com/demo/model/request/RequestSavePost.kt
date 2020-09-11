package com.demo.model.request

import com.demo.util.Prefs
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody


data class RequestSavePost(

    @field:SerializedName("languageId")
    var languageId: MultipartBody.Part? = null,

    @field:SerializedName("description")
    var description: MultipartBody.Part? = null,

    @field:SerializedName("image")
    var image: MultipartBody.Part? = null,

    @field:SerializedName("profileImage")
    var profileImage: MultipartBody.Part? = null,

    @field:SerializedName("video")
    var video: MultipartBody.Part? = null,

    @field:SerializedName("categoryId")
    var categoryId: MultipartBody.Part? = null

//    @field:SerializedName("languageId")
//    var languageId: MultipartBody.Part? = null,
//
//    @field:SerializedName("description")
//    var description: MultipartBody.Part? = null,
//


//    @field:SerializedName("image")
//    var image: MultipartBody.Part? = null,
//
//    @field:SerializedName("video")
//    var video: MultipartBody.Part? = null,
//
//    @field:SerializedName("categoryId")
//    var categoryId: MultipartBody.Part? = null,
//
//    @field:SerializedName("user")
//	var user: User? = User(Prefs.get().loginData!!.userId),
//
//    @field:SerializedName("language")
//	var language: Language? = Language(Prefs.get().selectedLangId),
//
//    @field:SerializedName("postId")
//	var postId: Int? = 0,
//
//    @field:SerializedName("title")
//	var title: String? = "",
//
//    @field:SerializedName("images")
//	var images: String? = "",
//
//    @field:SerializedName("hashTags")
//	var hashTags: String? = "",
//
//    @field:SerializedName("latitude")
//	var latitude: Double? = 0.0,
//
//    @field:SerializedName("longitude")
//	var longitude: Double? = 0.0,
//
//    @field:SerializedName("location")
//	var location: String? = "",
//
//    @field:SerializedName("categoryIds")
//	var categoryIds: String? = ""
)


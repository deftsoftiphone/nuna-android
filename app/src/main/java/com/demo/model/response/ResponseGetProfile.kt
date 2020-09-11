package com.demo.model.response

import com.google.gson.annotations.SerializedName


data class ResponseGetProfile(
    @SerializedName("acceptTC")
    var acceptTC: Boolean = false,
    @SerializedName("active")
    var active: Boolean = false,
    @SerializedName("authKey")
    var authKey: Any? = null,
    @SerializedName("authToken")
    var authToken: Any? = null,
    @SerializedName("bio")
    var bio: String = "",
    @SerializedName("countryCallingCode")
    var countryCallingCode: String = "",
    @SerializedName("dob")
    var dob: String = "",
    @SerializedName("email")
    var email: String = "",
    @SerializedName("fcmToken")
    var fcmToken: Any? = null,
    @SerializedName("firstName")
    var firstName: String = "",
    @SerializedName("followerCount")
    var followerCount: Int = 0,
    @SerializedName("followingCount")
    var followingCount: Int = 0,
    @SerializedName("gender")
    var gender: String = "",
    @SerializedName("ifollow")
    var ifollow: Boolean = false,
    @SerializedName("isInstantAlerts")
    var isInstantAlerts: Boolean = false,
    @SerializedName("lastName")
    var lastName: String = "",
    @SerializedName("latitude")
    var latitude: Int = 0,
    @SerializedName("location")
    var location: Any? = null,
    @SerializedName("longitude")
    var longitude: Int = 0,
    @SerializedName("membershipDate")
    var membershipDate: String = "",
    @SerializedName("message")
    var message: Any? = null,
    @SerializedName("myCommentsCount")
    var myCommentsCount: Int = 0,
    @SerializedName("myLikesCount")
    var myLikesCount: Int = 0,
    @SerializedName("myViewsCount")
    var myViewsCount: Int = 0,
    @SerializedName("otp")
    var otp: Any? = null,
    @SerializedName("password")
    var password: Any? = null,
    @SerializedName("phoneNumber")
    var phoneNumber: String = "",
    @SerializedName("profileId")
    var profileId: Int = 0,
    @SerializedName("profileImage")
    var profileImage: String = "",
    @SerializedName("searchKeyWord")
    var searchKeyWord: Any? = null,
    @SerializedName("socialId")
    var socialId: Any? = null,
    @SerializedName("socialType")
    var socialType: Int = 0,
    @SerializedName("subscribedNewsletter")
    var subscribedNewsletter: Boolean = false,
    @SerializedName("tags")
    var tags: String = "",
    @SerializedName("userId")
    var userId: Int = 0,
    @SerializedName("userName")
    var userName: Any? = null,
    @SerializedName("userRole")
    var userRole: String = "",
    @SerializedName("websiteUrl")
    var websiteUrl: String = "",
    @SerializedName("weeklyUpdates")
    var weeklyUpdates: Boolean = false
)
package com.demo.model

import com.demo.followPopularUsers.FollowStatus
import com.google.gson.annotations.SerializedName


data class UserInfo(
    @SerializedName("acceptTC")
    var acceptTC: Boolean = false,
    @SerializedName("active")
    var active: Boolean = false,
    @SerializedName("authKey")
    var authKey: Any = Any(),
    @SerializedName("authToken")
    var authToken: Any = Any(),
    @SerializedName("bio")
    var bio: Any = Any(),
    @SerializedName("countryCallingCode")
    var countryCallingCode: Any = Any(),
    @SerializedName("email")
    var email: Any = Any(),
    @SerializedName("fcmToken")
    var fcmToken: Any = Any(),
    @SerializedName("firstName")
    var firstName: String = "",
    @SerializedName("followerCount")
    var followerCount: Int = 0,
    @SerializedName("isInstantAlerts")
    var isInstantAlerts: Boolean = false,
    @SerializedName("lastName")
    var lastName: String = "",
    @SerializedName("latitude")
    var latitude: Int = 0,
    @SerializedName("location")
    var location: Any = Any(),
    @SerializedName("longitude")
    var longitude: Int = 0,
    @SerializedName("membershipDate")
    var membershipDate: Any = Any(),
    @SerializedName("message")
    var message: Any = Any(),
    @SerializedName("password")
    var password: Any = Any(),
    @SerializedName("phoneNumber")
    var phoneNumber: Any = Any(),
    @SerializedName("profileId")
    var profileId: Int = 0,
    @SerializedName("profileImage")
    var profileImage: Any = Any(),
    @SerializedName("searchKeyWord")
    var searchKeyWord: Any = Any(),
    @SerializedName("socialId")
    var socialId: Any = Any(),
    @SerializedName("socialType")
    var socialType: Int = 0,
    @SerializedName("subscribedNewsletter")
    var subscribedNewsletter: Boolean = false,
    @SerializedName("tags")
    var tags: Any = Any(),
    @SerializedName("userId")
    var userId: Int = 0,
    @SerializedName("userName")
    var userName: Any = Any(),
    @SerializedName("userRole")
    var userRole: Any = Any(),
    @SerializedName("websiteUrl")
    var websiteUrl: Any = Any(),
    @SerializedName("weeklyUpdates")
    var weeklyUpdates: Boolean = false,
    @SerializedName("ifollow")
    var iFollow: Boolean = false
) {
    var followStatus : FollowStatus = FollowStatus.UNFOLLOWED
}
package com.demo.model.response.baseResponse

import android.os.Parcelable
import com.demo.util.Prefs
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class BaseResponse(
//Base Response
    @field:SerializedName("data")
    var data: Data? = null,

    @field:SerializedName("success")
    var success: Boolean? = false,

    @field:SerializedName("message")
    var message: String? = "",

    @field:SerializedName("error")
    var error: Error? = null,

    @field:SerializedName("user")
    var user: User? = null,

    @field:SerializedName("token")
    var token: String? = null,

    @field:SerializedName("statusErrorCode")
    var statusErrorCode: Int? = null,

    @SerializedName("paging")
    var paging: Paging? = null


)

data class StatusErrorCode(
    @SerializedName("isAvailable")
    val isAvailable: Boolean
)


@Parcelize
data class UserProfile(
    @SerializedName("bioData")
    var bioData: String? = null,
    @SerializedName("commentsRecievedFromOthersPost")
    var commentsRecievedFromOthersPost: Int = 0,
    @SerializedName("commentsSentToOthersPost")
    var commentsSentToOthersPost: Int = 0,
    @SerializedName("noOfFollowers")
    var followers: Int = 0,
    @SerializedName("followings")
    var followings: Int = 0,
    @SerializedName("noOfLikes")
    var noOfLikes: Int = 0,
    @SerializedName("fullName")
    var fullName: String? = null,
    @SerializedName("hashTagFollowed")
    var hashTagFollowed: Int = 0,
    @SerializedName("_id")
    var id: String? = null,
    @SerializedName("likesRecievedFromOthersPost")
    var likesRecievedFromOthersPost: Int = 0,
    @SerializedName("myBoards")
    var myBoards: Int = 0,
    @SerializedName("noOfMyHashTags")
    var noOfMyHashTags: Int = 0,
    @SerializedName("myHashTags")
    val myHashTags: List<HashTag>? = null,
    @SerializedName("followedByMe")
    var followedByMe: Boolean = false,

    @SerializedName("phoneNumber")
    var phoneNumber: String? = null,
    @SerializedName("noOfPostCreated")
    var postShared: Int = 0,

    @SerializedName("profilePicture")
    var profilePicture: ProfilePicture? = null,

    @SerializedName("profileUrl")
    var profileUrl: String? = null,
    @SerializedName("userName")
    var userName: String? = null,
    @SerializedName("viewsOnPostCreated")
    var viewsOnPostCreated: Int = 0
) : Parcelable

@Parcelize
data class PutUserProfile(
    @SerializedName("bioData")
    var bioData: String? = null,

    @SerializedName("fullName")
    var fullName: String? = null,

    @SerializedName("userName")
    var userName: String? = null
) : Parcelable


@Parcelize
data class ProfilePicture(
    @SerializedName("_id")
    val id: String? = "",
    @SerializedName("isImage")
    val isImage: Boolean? = false,
    @SerializedName("mediaUrl")
    var mediaurl: String? = null
) : Parcelable

@Parcelize
data class Error(
    @field:SerializedName("status")
    var status: Int? = 1,

    @field:SerializedName("message")
    var message: String? = null
) : Parcelable


@Parcelize
data class VideosItem(

    @field:SerializedName("mediaUrl")
    var mediaUrl: String? = null,

    @field:SerializedName("id")
    var id: String? = null
) : Parcelable

@Parcelize
data class Data(
    @SerializedName("videoPosts")
    val videoPosts: ArrayList<VideoPostsItem>? = ArrayList(),

    @SerializedName("userDetails")
    val userProfile: UserProfile,

    @SerializedName("isAvailable")
    val isAvailable: Boolean,

    @SerializedName("followedByMe")
    val followedByMe: Boolean,

    @SerializedName("noOfPost")
    val noOfPost: Int,

    @SerializedName("following")
    val followingData: List<PostAssociated>,

    @SerializedName("post")
    val posts: ArrayList<Post>,

    @SerializedName("postDetail")
    val postDetail: Post,

    @SerializedName("activity")
    val activity: List<Activity>,

    @SerializedName("suggestedUser")
    val suggestions: List<Activity>,

    @SerializedName("popularPosts")
    val popularPosts: ArrayList<PostAssociated>? = ArrayList(),

    @field:SerializedName("images")
    var images: List<ImagesItem>? = null,

    @field:SerializedName("videos")
    var videos: ArrayList<VideosItem>? = null,

    @field:SerializedName("title")
    var title: String? = null,

    @field:SerializedName("isDeactivated")
    var isDeactivated: String? = null,

    @field:SerializedName("reported")
    var reported: String? = null,

    @field:SerializedName("_id")
    var id: String? = null,

    @field:SerializedName("profileUrl")
    var profileUrl: String? = null,

    @field:SerializedName("user")
    var user: User? = null,

    @field:SerializedName("users")
    var users: ArrayList<Activity>? = ArrayList(),

    @field:SerializedName("views")
    var views: Int? = null,

    @field:SerializedName("hashTag")
    var hashTag: HashTag? = null,

    @field:SerializedName("likes")
    var likes: Int? = null,

    @field:SerializedName("languageList")
    var languages: List<Language>? = null,

    @field:SerializedName("hashTagList")
    var hashTags: ArrayList<HashTag>? = ArrayList(),

    @field:SerializedName("postCategoryList")
    var categories: ArrayList<Category>? = null,

    @field:SerializedName("reportedByUser")
    var reportedByUser: Int? = null,
    @SerializedName("noOfFollowers")
    var noOfFollowers: String? = null
) : Parcelable

@Parcelize
data class Activity(
    @field:SerializedName("fromUserId")
    var user: User? = null,

    @field:SerializedName("description")
    var description: String? = null,

    @field:SerializedName("createdOn")
    var createdOn: String? = null,

    @field:SerializedName("_id")
    var id: String? = null,

    @field:SerializedName("fullName")
    var fullName: String? = null,

    @field:SerializedName("userName")
    var userName: String? = null,


    @field:SerializedName("noOfFollowers")
    var noOfFollowers: Int? = 0,

    @field:SerializedName("noOfPostsCreated")
    var noOfPostsCreated: Int? = 0,

    @field:SerializedName("timeAgo")
    var timeAgo: String? = null,
    @SerializedName("profilePicture")
    var profilePicture: ProfilePicture? = null,

    @field:SerializedName("followedByMe")
    var followedByMe: Boolean? = false
) : Parcelable


@Parcelize
data class ImagesItem(

    @field:SerializedName("mediaUrl")
    var mediaUrl: String? = null,

    @field:SerializedName("id")
    var id: String? = null
) : Parcelable


@Parcelize
data class HashTag(
    @field:SerializedName("categoryBased")
    val categoryBased: Int? = null,

    @field:SerializedName("commentsBased")
    val commentsBased: Int? = null,

    @field:SerializedName("language")
    val language: Language? = null,

    @field:SerializedName("postAssociated")
    val postAssociated: List<PostAssociated>? = null,

    @field:SerializedName("tagName")
    val tagName: String? = "",

    @field:SerializedName("viewsBased")
    val viewsBased: Int? = null,

    @field:SerializedName("viewsAssociated")
    val viewsAssociated: List<String?>? = null,

    @field:SerializedName("postsBased")
    val postsBased: Int? = 0,

    @field:SerializedName("sharesBased")
    val sharesBased: Int? = null,

    @field:SerializedName("noOfPosts")
    val noOfPosts: Int? = 0,

    @field:SerializedName("sharesAssociated")
    val sharesAssociated: List<String?>? = null,

    @field:SerializedName("_id")
    var id: String? = null,

    @field:SerializedName("category")
    val category: List<Category>? = null,

    @field:SerializedName("commentsAssociated")
    val commentsAssociated: List<String?>? = null,

    @field:SerializedName("followedByMe")
    var followedByMe: Boolean? = null
) : Parcelable

@Parcelize
data class User(

    @field:SerializedName("myBoards")
    var myBoards: List<String>? = null,

    @field:SerializedName("followers")
    var followers: List<String>? = null,

    @field:SerializedName("phoneNumber")
    var phoneNumber: String? = null,

    @field:SerializedName("isDeactivated")
    var isDeactivated: Boolean? = null,

    @field:SerializedName("followings")
    var followings: List<String>? = null,

    @field:SerializedName("_id")
    var id: String? = null,

    @field:SerializedName("userId")
    var userId: String? = null,

    @field:SerializedName("fullName")
    var fullName: String? = null,

    @field:SerializedName("profileImage")
    var profileImage: String? = null,

    @field:SerializedName("userName")
    var userName: String? = null,

    @field:SerializedName("noOfFollowers")
    val noOfFollowers: Int? = null,

    @field:SerializedName("followedByMe")
    var followedByMe: Boolean? = false,

    @field:SerializedName("noOfPostCreated")
    val noOfPostCreated: Int? = null
) : Parcelable

@Parcelize
data class Language(
    @field:SerializedName("_id")
    var _id: String? = null,

    @field:SerializedName("languageName")
    var languageName: String? = null,

    @field:SerializedName("languageIntId")
    var languageIntId: Int? = null,

    @field:SerializedName("description")
    var description: String? = null
) : Parcelable

@Parcelize
data class Category(
    @field:SerializedName("likesAssociated")
    var likesAssociated: List<String>? = null,

    @field:SerializedName("commentsBased")
    var commentsBased: Int? = null,

    @field:SerializedName("kannada")
    var categoryNameKannada: String? = null,

    @field:SerializedName("postAssociated")
    var postAssociated: List<PostAssociated>? = null,

    @field:SerializedName("categoryName")
    var categoryName: String? = null,

    @field:SerializedName("viewsBased")
    var viewsBased: Int? = null,

    @field:SerializedName("gujrati")
    var categoryNameGujrati: String? = null,

    @field:SerializedName("marathi")
    var categoryNameMarathi: String? = null,

    @field:SerializedName("tamil")
    var categoryNameTamil: String? = null,

    @field:SerializedName("hashtagsBased")
    var hashTagsBased: Int? = null,

    @field:SerializedName("viewsAssociated")
    var viewsAssociated: List<String>? = null,

    @field:SerializedName("isDeactivated")
    var isDeactivated: Boolean? = null,

    @field:SerializedName("postsBased")
    var postsBased: Int? = null,

    @field:SerializedName("hashTagAssociated")
    var hashTagAssociated: List<String>? = null,

    @field:SerializedName("__v")
    var V: Int? = null,

    @field:SerializedName("sharesBased")
    var sharesBased: Int? = null,

    @field:SerializedName("sharesAssociated")
    var sharesAssociated: List<String>? = null,

    @field:SerializedName("bengali")
    var categoryNameBengali: String? = null,

    @field:SerializedName("hindi")
    var categoryNameHindi: String? = null,

    @field:SerializedName("likesBased")
    var likesBased: Int? = null,

    @field:SerializedName("noOfPostAssociated")
    var noOfPostAssociated: Int? = null,

    @field:SerializedName("_id")
    var id: String? = null,

    @field:SerializedName("telugu")
    var categoryNameTelugu: String? = null,

    @field:SerializedName("commentsAssociated")
    var commentsAssociated: List<String>? = null,

    @field:SerializedName("isSelected")
    var isSelected: Boolean = false

) : Parcelable


@Parcelize
data class PostAssociated(

    @field:SerializedName("medias")
    val medias: List<Media>? = null,

    @field:SerializedName("noOfLikes")
    val noOfLikes: Int? = null,

    @field:SerializedName("createdBy")
    val createdBy: CreatedBy? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("likes")
    val likes: List<String?>? = null

) : Parcelable


@Parcelize
data class Post(
    @field:SerializedName("medias")
    val medias: List<Media>? = null,

    @field:SerializedName("language")
    var languages: Language? = null,

    @field:SerializedName("noOfLikes")
    val noOfLikes: Int? = null,

    @field:SerializedName("createdBy")
    val createdBy: CreatedBy? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("score")
    var score: String? = null,

    @field:SerializedName("reportedByUser")
    var reportedByUser: String? = null,

    @field:SerializedName("_id")
    var id: String? = null,

    @field:SerializedName("views")
    var views: Int? = null,

    @field:SerializedName("hashTags")
    var hashTag: List<HashTag>? = null,

    @field:SerializedName("likes")
    var likes: Int? = null,
    @field:SerializedName("comments")
    var comments: Int? = null,

    @field:SerializedName("shares")
    var shares: Int? = null
) : Parcelable

@Parcelize
data class Media(
    @SerializedName("createdBy")
    val createdBy: CreatedBy,
    @SerializedName("_id")
    val id: String,
    @SerializedName("isImage")
    val isImage: Boolean,
    @SerializedName("isVideo")
    val isVideo: Boolean,
    @SerializedName("mediaUrl")
    val mediaUrl: String
) : Parcelable

@Parcelize
data class CreatedBy(
    @SerializedName("fullName")
    val fullName: String? = null,

    @SerializedName("userName")
    val userName: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("profileImage")
    val profileImage: String,
    @SerializedName("profilePicture")
    val profilePicture: ProfilePicture
) : Parcelable

data class Paging(
    @SerializedName("count")
    val count: String,
    @SerializedName("page")
    val page: Page
)

data class Page(
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("nextPage")
    val nextPage: Int,
    @SerializedName("prevPage")
    val prevPage: Int,
    @SerializedName("totalCount")
    val totalCount: Int
)

class QueryParams {
    @field:SerializedName("offset")
    var offset: Int? = null

    @field:SerializedName("limit")
    var limit: Int? = null

    @field:SerializedName("search")
    var search: String? = null

    @field:SerializedName("startsWith")
    var startsWith: String? = null

    @field:SerializedName("searchText")
    var searchText: String? = null

    @field:SerializedName("languageId")
    var languageId: String? = Prefs.init().selectedLang._id!!

    @field:SerializedName("id")
    var id: String? = null

    @field:SerializedName("userId")
    var userId: String? = null

    @field:SerializedName("categoryId")
    var categoryId: String? = null

    @field:SerializedName("postOffset")
    var postOffset: Int? = null

    @field:SerializedName("postLimit")
    var postLimit: Int? = null

    @field:SerializedName("hashTagOffset")
    var hashTagOffset: Int? = null

    @field:SerializedName("hashTagLimit")
    var hashTagLimit: Int? = null

    @field:SerializedName("token")
    var token: String? = null

    @field:SerializedName("tab")
    var tab: String? = null

}

@Parcelize
data class Country(
    @field:SerializedName("name") var name: String,
    @field:SerializedName("dial_code") var dial_code: String,
    @field:SerializedName("code") var code: String
) : Parcelable

@Parcelize
data class SocketRequest(
    @field:SerializedName("userId") var userId: String? = "",
    @field:SerializedName("success") var success: Boolean? = null
) : Parcelable

@Parcelize
data class VideoPostsItem(

    @field:SerializedName("medias")
    val medias: List<MediasItem?>? = null,

    @field:SerializedName("noOfLikes")
    val noOfLikes: Int? = null,

    @field:SerializedName("hashTags")
    val hashTags: ArrayList<HashTag>? = ArrayList(),

    @field:SerializedName("createdBy")
    val createdBy: ArrayList<CreatedBy>? = ArrayList(),

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("_id")
    val id: String? = null
) : Parcelable

@Parcelize
data class MediasItem(

    @field:SerializedName("mediaUrl")
    val mediaUrl: String? = null,

    @field:SerializedName("isVideo")
    val isVideo: Boolean? = null,

    @field:SerializedName("_id")
    val id: String? = null
) : Parcelable
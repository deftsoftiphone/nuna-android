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
    var followers: String = "",
    @SerializedName("followings")
    var followings: Int = 0,
    @SerializedName("noOfLikes")
    var noOfLikes: String = "",
    @SerializedName("notificationAllowed")
    var notificationAllowed: Boolean? = true,
    @SerializedName("noOfMyHashTags")
    var noOfMyHashTags: String = "0",
    @SerializedName("noOfPostCreated")
    var noOfPostCreated: String = "",
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
    @SerializedName("myHashTags")
    val myHashTags: List<HashTag>? = null,
    @SerializedName("followedByMe")
    var followedByMe: Boolean = false,

    @SerializedName("phoneNumber")
    var phoneNumber: String? = null,

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

    @SerializedName("commentList")
    val commentList: ArrayList<Comment>? = ArrayList(),

    @SerializedName("postCategoryList")
    val postCategoryList: ArrayList<PostCategory>? = ArrayList(),

    @SerializedName("commentDetails")
    val commentDetails: Comment,

    @SerializedName("videoPosts")
    val videoPosts: ArrayList<PostAssociated>? = ArrayList(),

    @SerializedName("userProfileBoard")
    val userProfileBoard: ArrayList<PostAssociated>? = ArrayList(),

    @SerializedName("postDetails")
    val postDetails: PostAssociated,

    @SerializedName("userDetails")
    val userProfile: UserProfile,

    @SerializedName("isAvailable")
    val isAvailable: Boolean,

    @SerializedName("followedByMe")
    val followedByMe: Boolean,

    @SerializedName("noOfPost")
    val noOfPost: Int,

    @SerializedName("following")
    val followingPosts: ArrayList<PostAssociated>,

    @SerializedName("post")
    val posts: ArrayList<PostAssociated>,

    @SerializedName("postDetail")
    val postDetail: PostAssociated,

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
/*
    @field:SerializedName("postCategoryList")
    var categories: ArrayList<Category>? = null,*/

    @field:SerializedName("reportedByUser")
    var reportedByUser: Int? = null,

    @SerializedName("tolalNumberOfNotifications")
    var tolalNumberOfNotifications: Int? = 0,

    @SerializedName("noOfFollowers")
    var noOfFollowers: String? = null,

    @field:SerializedName("hashTagImages")
    val hashTagImages: List<HashTagImagesItem>? = null,

    @SerializedName("paging")
    var paging: Paging? = null

) : Parcelable

@Parcelize
data class Comment(
    @SerializedName("childComments")
    val childComments: List<ChildComment>?,
    @SerializedName("commentContent")
    val commentContent: String,
    @SerializedName("commentedBy")
    val commentedBy: CreatedBy,
    @SerializedName("description")
    val description: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("noOfLikes")
    var noOfLikes: String,
    @SerializedName("noOfRepliesToComment")
    val noOfRepliesToComment: String,
    @SerializedName("noOfReported")
    val noOfReported: String,
    @SerializedName("noOfViews")
    val noOfViews: String,
    @SerializedName("parentCommentId")
    val parentCommentId: String,
    @SerializedName("likedByUser")
    var likedByUser: Boolean? = false
) : Parcelable

@Parcelize
data class ChildComment(

    @field:SerializedName("reportedByUser")
    val reportedByUser: String? = null,

    @field:SerializedName("noOfLikes")
    val noOfLikes: String? = null,

    @field:SerializedName("noOfRepliesToComment")
    val noOfRepliesToComment: String? = null,

    @field:SerializedName("noOfViews")
    val noOfViews: String? = null,

    @field:SerializedName("parentCommentId")
    val parentCommentId: String? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("commentContent")
    val commentContent: String? = null,

    @field:SerializedName("media")
    val media: String? = null,

    @field:SerializedName("commentedBy")
    val commentedBy: CreatedBy? = null
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

    @field:SerializedName("notificationId")
    var notificationId: Int? = 0,

    @field:SerializedName("fullName")
    var fullName: String? = null,

    @field:SerializedName("userName")
    var userName: String? = null,

    @field:SerializedName("noOfFollowers")
    var noOfFollowers: Int? = 0,

    @field:SerializedName("noOfPostsCreated")
    var noOfPostsCreated: Int? = 0,

    @field:SerializedName("noOfPostCreated")
    var noOfPostCreated: Int? = 0,

    @field:SerializedName("timeAgo")
    var timeAgo: String? = null,

    @SerializedName("profilePicture")
    var profilePicture: ProfilePicture? = null,

    @field:SerializedName("followedByMe")
    var followedByMe: Boolean? = false,

    @field:SerializedName("notificationDescription")
    var notificationDescription: FCMNotification? = null,

    @field:SerializedName("onPostId")
//    var post: String? = null
    var post: PostAssociated? = null

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
    val categoryBased: Int? = 0,

    @field:SerializedName("commentsBased")
    val commentsBased: Int? = 0,

    @field:SerializedName("language")
    val language: Language? = null,

    @field:SerializedName("postAssociated")
    val postAssociated: List<PostAssociated>? = null,

    @field:SerializedName("tagName")
    val tagName: String? = "",

    @field:SerializedName("viewsBased")
    val viewsBased: Int? = 0,

    @field:SerializedName("viewsAssociated")
    val viewsAssociated: List<String?>? = null,

    @field:SerializedName("postsBased")
    val postsBased: Int? = 0,

    @field:SerializedName("sharesBased")
    val sharesBased: Int? = 0,

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
    var followedByMe: Boolean? = false
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
    val noOfPostCreated: Int? = null,

    @SerializedName("profilePicture")
    var profilePicture: ProfilePicture? = null
) : Parcelable

@Parcelize
data class Language(
    @field:SerializedName("_id")
    var _id: String? = null,

    @field:SerializedName("languageName")
    var languageName: String? = null,

    @field:SerializedName("languageIntId")
    var languageIntId: Int? = 0,

    @field:SerializedName("description")
    var description: String? = null


) : Parcelable {
    override fun equals(other: Any?): Boolean {
        return if (other is Language) {
            (_id.equals(other._id) && languageName.equals(other.languageName) && languageIntId == other.languageIntId && description.equals(
                other.description
            ))
        } else {
            false
        }
    }
}

@Parcelize
data class Category(
    @field:SerializedName("likesAssociated")
    var likesAssociated: ArrayList<String>? = null,

    @field:SerializedName("commentsBased")
    var commentsBased: Int? = null,

    @field:SerializedName("kannada")
    var categoryNameKannada: String? = null,

    @field:SerializedName("postAssociated")
    var postAssociated: ArrayList<PostAssociated>? = null,

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
    var viewsAssociated: ArrayList<String>? = null,

    @field:SerializedName("isDeactivated")
    var isDeactivated: Boolean? = null,

    @field:SerializedName("postsBased")
    var postsBased: Int? = null,

    @field:SerializedName("hashTagAssociated")
    var hashTagAssociated: ArrayList<String>? = null,

    @field:SerializedName("__v")
    var V: Int? = null,

    @field:SerializedName("sharesBased")
    var sharesBased: Int? = null,

    @field:SerializedName("sharesAssociated")
    var sharesAssociated: ArrayList<String>? = null,

    @field:SerializedName("bengali")
    var categoryNameBengali: String? = null,

    @field:SerializedName("english")
    var categoryNameEnglish: String? = null,

    @field:SerializedName("hindi")
    var categoryNameHindi: String? = null,

    @field:SerializedName("likesBased")
    var likesBased: Int? = null,

    @field:SerializedName("noOfPostAssociated")
    var noOfPostAssociated: String? = "0",

    @field:SerializedName("_id")
    var id: String? = null,

    @field:SerializedName("telugu")
    var categoryNameTelugu: String? = null,

    @field:SerializedName("commentsAssociated")
    var commentsAssociated: ArrayList<String>? = null,

    @field:SerializedName("isSelected")
    var isSelected: Boolean = false,

    @field:SerializedName("isOther")
    var isOther: Boolean? = false

) : Parcelable


@Parcelize
data class PostAssociated(

    @field:SerializedName("comments")
    var comments: String? = null,

    @field:SerializedName("language")
    var language: Language? = null,

    @field:SerializedName("reportedByUser")
    var reportedByUser: String? = null,

    @field:SerializedName("shares")
    var shares: String? = null,

    @field:SerializedName("hashTags")
    var hashTags: List<HashTag>? = null,

    @field:SerializedName("views")
    var views: String? = null,

    @field:SerializedName("noOfShares")
    var noOfShares: String? = "0",

    @field:SerializedName("medias")
    var medias: List<Media>? = null,

    @field:SerializedName("score")
    var score: Double? = null,

    @field:SerializedName("noOfLikes")
    var noOfLikes: String? = "0",

    @field:SerializedName("noOfViews")
    var noOfViews: String? = "0",

    @field:SerializedName("noOfComments")
    var noOfComments: String? = "0",

    @field:SerializedName("likedByUser")
    var likedByUser: Boolean? = false,

    @field:SerializedName("description")
    var description: String? = null,

    @field:SerializedName("_id")
    var id: String? = null,

    @field:SerializedName("savedByUser")
    var savedByUser: Boolean? = false,

    @field:SerializedName("createdBy")
    var createdBy: CreatedBy? = null,

    @field:SerializedName("followedByMe")
    var followedByMe: Boolean? = false

) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (other is PostAssociated) {
            return (noOfShares.equals(other.noOfShares) &&
                    noOfLikes.equals(other.noOfLikes) &&
                    noOfViews.equals(other.noOfViews) &&
                    shares.equals(other.shares) &&
                    noOfComments.equals(other.noOfComments) &&
                    likedByUser == other.likedByUser &&
                    description.equals(other.description) &&
                    id.equals(other.id) &&
                    savedByUser == other.savedByUser &&
                    followedByMe == other.followedByMe) &&
                    createdBy?.id.equals(other.createdBy?.id)
        } else {
            return false
        }
    }
}

@Parcelize
data class Post(
    @field:SerializedName("medias")
    val medias: ArrayList<Media>? = ArrayList(),

    @field:SerializedName("language")
    var languages: Language? = Language(),

    @field:SerializedName("noOfLikes")
    val noOfLikes: Int? = 0,

    @field:SerializedName("createdBy")
    val createdBy: CreatedBy? = null,

    @field:SerializedName("description")
    val description: String? = "",

    @field:SerializedName("score")
    var score: String? = "",

    @field:SerializedName("reportedByUser")
    var reportedByUser: String? = "",

    @field:SerializedName("_id")
    var id: String? = "",

    @field:SerializedName("views")
    var views: Int? = 0,

    @field:SerializedName("hashTags")
    var hashTag: ArrayList<HashTag>? = ArrayList(),

    @field:SerializedName("likes")
    var likes: Int? = 0,
    @field:SerializedName("comments")
    var comments: Int? = 0,

    @field:SerializedName("shares")
    var shares: Int? = 0
) : Parcelable

@Parcelize
data class Media(

    @field:SerializedName("isImage")
    var isImage: Boolean? = null,

    @field:SerializedName("mediaUrl")
    var mediaUrl: String? = null,

    @field:SerializedName("createdBy")
    var createdBy: CreatedBy? = null,

    @field:SerializedName("isVideo")
    var isVideo: Boolean? = null,

    @field:SerializedName("_id")
    var id: String? = null
) : Parcelable

@Parcelize
data class CreatedBy(
    @SerializedName("fullName")
    val fullName: String? = "",
    @SerializedName("userName")
    val userName: String? = "",
    @SerializedName("_id")
    val id: String? = "",
    @SerializedName("profileImage")
    val profileImage: String? = "",
    @SerializedName("profilePicture")
    val profilePicture: ProfilePicture? = null
) : Parcelable

@Parcelize
data class Paging(
    @SerializedName("count")
    val count: String,
    @SerializedName("page")
    val page: Page
) : Parcelable

@Parcelize
data class Page(
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("nextPage")
    val nextPage: Int,
    @SerializedName("prevPage")
    val prevPage: Int,
    @SerializedName("totalCount")
    val totalCount: Int
) : Parcelable

@Parcelize
class QueryParams (
    @field:SerializedName("offset")
    var offset: Int? = null,

    @field:SerializedName("limit")
    var limit: Int? = null,

    @field:SerializedName("search")
    var search: String? = null,

    @field:SerializedName("startsWith")
    var startsWith: String? = null,

    @field:SerializedName("searchText")
    var searchText: String? = null,

    @field:SerializedName("languageId")
    var languageId: String? = Prefs.init().selectedLang._id!!,

    @field:SerializedName("id")
    var id: String? = null,

    @field:SerializedName("userId")
    var userId: String? = null,

    @field:SerializedName("postId")
    var postId: String? = null,

    @field:SerializedName("categoryId")
    var categoryId: String? = null,

    @field:SerializedName("postOffset")
    var postOffset: Int? = null,

    @field:SerializedName("postLimit")
    var postLimit: Int? = null,

    @field:SerializedName("hashTagOffset")
    var hashTagOffset: Int? = null,

    @field:SerializedName("hashTagLimit")
    var hashTagLimit: Int? = null,

    @field:SerializedName("token")
    var token: String? = null,

    @field:SerializedName("tab")
    var tab: String? = null,

    @field:SerializedName("type")
    var type: String? = null,

    @field:SerializedName("suggestedOffset")
    var suggestedOffset: Int? = null,

    @field:SerializedName("suggestedLimit")
    var suggestedLimit: Int? = null,

    @field:SerializedName("notificationOffset")
    var notificationOffset: Int? = null,

    @field:SerializedName("notificationLimit")
    var notificationLimit: Int? = null,

    @field:SerializedName("authDeviceToken")
    var authDeviceToken: String? = "",

    @field:SerializedName("newCategory")
    var newCategory: String? = null,

    @field:SerializedName("recombee")
    var useRecombee: Boolean? = null,

    @field:SerializedName("for")
    var forDashboard: String? = null

): Parcelable

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
    val medias: List<MediasItem>? = null,

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


interface BaseViewPostRequest {
    var linkedTo: String?
}

@Parcelize
data class LikePostRequest(

    @field:SerializedName("post")
    val post: String? = null,

    @field:SerializedName("linkedTo")
    override var linkedTo: String? = "post"
) : Parcelable, BaseViewPostRequest


@Parcelize
data class LikeCommentRequest(

    @field:SerializedName("comment")
    var comment: String? = null,

    @field:SerializedName("linkedTo")
    override var linkedTo: String? = "comment"
) : Parcelable, BaseViewPostRequest

@Parcelize
data class AddCommentRequest(

    @field:SerializedName("postId")
    var postId: String? = null,


    @field:SerializedName("description")
    var description: String? = null,

    @field:SerializedName("parentCommentId")
    var parentCommentId: String? = null
) : Parcelable

@Parcelize
data class SavePostRequest(

    @field:SerializedName("postId")
    var postId: String? = null

) : Parcelable

@Parcelize
data class UpdateNotificationStatusRequest(

    @field:SerializedName("notificationStatus")
    var notificationStatus: Boolean? = null

) : Parcelable


@Parcelize
data class ReportPostRequest(

    @field:SerializedName("postId")
    var post: String? = null,

    @field:SerializedName("linkedTo")
    override var linkedTo: String? = "post"

) : Parcelable, BaseViewPostRequest

@Parcelize
data class UpdatePostEvent(
    @field:SerializedName("refreshPost")
    var post: PostAssociated? = null,
    @field:SerializedName("position")
    var position: Int? = 0
) : Parcelable

@Parcelize
data class PostDetailRequest(
    @field:SerializedName("id")
    var postId: String
) : Parcelable

@Parcelize
data class SharePostRequest(

    @field:SerializedName("post")
    var post: String? = null,

    @field:SerializedName("linkedTo")
    override var linkedTo: String? = "post"

) : Parcelable, BaseViewPostRequest

@Parcelize
data class HashTagImagesItem(

    @field:SerializedName("sortOrder")
    val sortOrder: Int? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("media")
    val media: Media? = null,

    @field:SerializedName("hashtag")
    val hashtag: HashTag? = null

) : Parcelable


@Parcelize
data class FCMNotification(

    @field:SerializedName("description")
    var description: String? = null,

    @field:SerializedName("from")
    var from: String? = null,

    ) : Parcelable

@Parcelize
data class PostCategory(

    @field:SerializedName("dayIncreasePercentage")
    var dayIncreasePercentage: String? = null,

    @field:SerializedName("weekIncreasePercentage")
    var weekIncreasePercentage: String? = null,

    @field:SerializedName("noOfPostAssociated")
    var noOfPostAssociated: String? = null,

    @field:SerializedName("kannada")
    var kannada: String? = null,

    @field:SerializedName("postAssociated")
    var postAssociated: List<PostAssociated>? = null,

    @field:SerializedName("categoryName")
    var categoryName: String? = null,

    @field:SerializedName("noOfViewsAssociated")
    var noOfViewsAssociated: String? = null,

    @field:SerializedName("gujrati")
    var gujrati: String? = null,

    @field:SerializedName("marathi")
    var marathi: String? = null,

    @field:SerializedName("tamil")
    var tamil: String? = null,

    @field:SerializedName("noOfCommentsAssociated")
    var noOfCommentsAssociated: String? = null,

    @field:SerializedName("noOfHashTagsAssociated")
    var noOfHashTagsAssociated: String? = null,

    @field:SerializedName("noOfSharesAssociated")
    var noOfSharesAssociated: String? = null,

    @field:SerializedName("english")
    var english: String? = null,

    @field:SerializedName("bengali")
    var bengali: String? = null,

    @field:SerializedName("hindi")
    var hindi: String? = null,

    @field:SerializedName("_id")
    var id: String? = null,

    @field:SerializedName("telugu")
    var telugu: String? = null,

    @field:SerializedName("noOfLikesAssociated")
    var noOfLikesAssociated: String? = null,

    @field:SerializedName("isOther")
    var isOther: Boolean? = null
) : Parcelable


@Parcelize
data class AddPostRequest(

    @field:SerializedName("videoUrl")
    var videoUrl: String? = null,

    @field:SerializedName("imageUrl")
    var imageUrl: String? = null,

    @field:SerializedName("languageId")
    var languageId: String? = null,

    @field:SerializedName("description")
    var description: String? = "",

    @field:SerializedName("categoryId")
    var categoryId: String? = null
) : Parcelable


@Parcelize
data class UpdatePostDurationRequest(

    @field:SerializedName("postId")
    var postId: String? = null,

    @field:SerializedName("viewPortion")
    var viewPortion: Double? = null,
) : Parcelable


enum class DataHolder {
    INSTANCE;

    private var mObjectList: ArrayList<PostAssociated>? = null

    companion object {
        fun hasData(): Boolean {
            return INSTANCE.mObjectList.isNullOrEmpty()
        }

        fun clearData() {
            INSTANCE.mObjectList?.clear()
        }

        var data: ArrayList<PostAssociated>?
            get() {
                val retList = INSTANCE.mObjectList
                INSTANCE.mObjectList = null
                return retList
            }
            set(objectList) {
                clearData()
                INSTANCE.mObjectList = objectList
            }
    }
}
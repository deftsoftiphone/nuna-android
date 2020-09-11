package com.demo.webservice


import com.demo.model.Pinboard
import com.demo.model.UserPostWrapper
import com.demo.model.request.RequestFollowUnfollowUser
import com.demo.model.response.*
import com.google.gson.reflect.TypeToken

object ApiRegister {

    /**
     * UAT server
     */
   const val URL_UAT = "https://d92fq5zee3.execute-api.ap-south-1.amazonaws.com/uat/"
    /***
     * Development Server
     */
    const val URL_DAV = "https://4x755iso3a.execute-api.ap-south-1.amazonaws.com/dev/"

    /***
     * Production server
     */

    const val URL_LIVE = "https://y90azhw6dg.execute-api.ap-south-1.amazonaws.com/prod/"


    //Local
//    const val BASE_URL = "http://192.168.3.98:3000/"


    const val BASE_URL = URL_UAT



    const val loginWithMobileNumber = "api/users/loginWithMobileNumber"















    const val sharePost = "sharePost"

    const val setUserDefaultLanguage = "setUserDefaultLanguage"
    const val USERREGISTER = "userRegister"
    const val USERAUTH = "userAuth"
    const val VALIDATEOTP = "validateOtp"

    const val CHANGE_PASSWORD = "changePassword"
    const val FORGOT_PASSWORD = "userForgotPassword"
    const val GETUSERPROFILE = "getUserProfile"
    const val SAVEUSERPROGILE = "saveUserProfile"
    const val GETCATEGORY = "getCategory"
    const val ADDUSERCATEGORY = "addUserCategory"
    const val SAVEPOST = "savePost"
    const val LIKEUNLIKEPOST = "likeUnLikePost"
    const val FOLLOWUNFOLLOWUSER = "followUnfollowUser"
    const val GETFOLLOWPOPULARUSERSSCREENDATA = "getFollowPopularUsersScreenData"

    /***
     * post
     */
    const val GETPOSTLIST = "getPostList"
    const val GETPOSTDETAILS = "getPostDetails"
    const val SAVECOMMENT = "saveComment"
    const val LIKEUNLIKECOMMENT = "likeUnLikeComment"
    const val GETCOMMENTSLIST = "getCommentsList"
    const val GETCOMMENTSREPLYLIST = "getCommentsReplyList"

    const val GET_USER_PINBOARD_LIST = "getUserPinboardList"
    const val GET_PINBOARD_POST_LIST = "getPinboardPostList"
    const val ADD_PINBOARD = "addPinboard"
    const val DELETE_PINBOARD = "deletePinboard"
    const val ADD_OR_REMOVE_PINBOARD_POST = "addOrRemovePinboardPost"
    const val ACTION_ON_POST = "actionOnPost"

    const val GET_USER_NOTIFICATION = "getUserNotification"

    const val GET_FOLLOWING_FOLLOWER_USERS_LIST = "getFollowingFollowerUsersList"
    const val GET_MY_CATEGORY = "getMyCategory"


    /**
     * IOS Key
     */
    //rnBeBdU89d8i86ZCvpxL6aJvCzyt3muY35hdIQDm

    fun getApiRequestType(url: String): ApiRequestType {

        val result = ApiRequestType()
        when (url) {
            sharePost-> {
                result.responseType = object : TypeToken<MasterResponse<Object>>() {}.type
                result.url = BASE_URL + "post"
                result.action = sharePost
                result.requestType = RequestType.POST
                return result
            }

            setUserDefaultLanguage-> {
                result.responseType = object : TypeToken<MasterResponse<ResponseLanguage>>() {}.type
                result.url = BASE_URL + "user"
                result.action = setUserDefaultLanguage
                result.requestType = RequestType.POST
                return result
            }


            setUserDefaultLanguage-> {
                result.responseType = object : TypeToken<MasterResponse<ResponseLanguage>>() {}.type
                result.url = BASE_URL + "user"
                result.action = setUserDefaultLanguage
                result.requestType = RequestType.POST
                return result
            }

            USERREGISTER -> {
                result.responseType = object : TypeToken<MasterResponse<ResponseLogin>>() {}.type
                result.url = BASE_URL + "user"
                result.action = USERREGISTER
                result.requestType = RequestType.POST
                return result
            }

            VALIDATEOTP -> {
                result.responseType = object : TypeToken<MasterResponse<ResponseLogin>>() {}.type
                result.url = BASE_URL + "user"
                result.action = VALIDATEOTP
                result.requestType = RequestType.POST
                return result
            }

            USERAUTH -> {
                result.responseType = object : TypeToken<MasterResponse<ResponseLogin>>() {}.type
                result.url = BASE_URL + "user"
                result.action = USERAUTH
                result.requestType = RequestType.POST
                return result
            }

            GET_FOLLOWING_FOLLOWER_USERS_LIST -> {
                result.responseType = object :
                    TypeToken<MasterResponse<List<ResponseFollowingFollowerUsersList>>>() {}.type
                result.url = BASE_URL + "user"
                result.action = GET_FOLLOWING_FOLLOWER_USERS_LIST
                result.requestType = RequestType.POST
                return result
            }

            GET_USER_NOTIFICATION -> {
                result.responseType =
                    object : TypeToken<MasterResponse<List<ResponseNotificationComment>>>() {}.type
                result.url = BASE_URL + "user"
                result.action = GET_USER_NOTIFICATION
                result.requestType = RequestType.POST
                return result
            }


            FORGOT_PASSWORD -> {
                result.responseType = object : TypeToken<MasterResponse<ResponseLogin>>() {}.type
                result.url = BASE_URL + "user"
                result.action = FORGOT_PASSWORD
                result.requestType = RequestType.POST
                return result
            }

            GETUSERPROFILE -> {
                result.responseType =
                    object : TypeToken<MasterResponse<ResponseGetProfile>>() {}.type
                result.url = BASE_URL + "user"
                result.action = GETUSERPROFILE
                result.requestType = RequestType.POST
                return result
            }

            SAVEUSERPROGILE -> {
                result.responseType =
                    object : TypeToken<MasterResponse<ResponseSaveUserProfile>>() {}.type
                result.url = BASE_URL + "user"
                result.action = SAVEUSERPROGILE
                result.requestType = RequestType.POST
                return result
            }


            GETCATEGORY -> {
                result.responseType =
                    object : TypeToken<MasterResponse<List<GetCategory>>>() {}.type
                result.url = BASE_URL + "user"
                result.action = GETCATEGORY
                result.requestType = RequestType.POST
                return result
            }


            ADDUSERCATEGORY -> {
                result.responseType =
                    object : TypeToken<MasterResponse<ResponseAddUserCategory>>() {}.type
                result.url = BASE_URL + "user"
                result.action = ADDUSERCATEGORY
                result.requestType = RequestType.POST
                return result
            }

            SAVEPOST -> {
                result.responseType = object : TypeToken<MasterResponse<ResponseSavePost>>() {}.type
                result.url = BASE_URL + "post"
                result.action = SAVEPOST
                result.requestType = RequestType.POST
                return result
            }

            LIKEUNLIKEPOST -> {
                result.responseType =
                    object : TypeToken<MasterResponse<ResponseLikeUnLikePost>>() {}.type
                result.url = BASE_URL + "post"
                result.action = LIKEUNLIKEPOST
                result.requestType = RequestType.POST

                return result
            }
            FOLLOWUNFOLLOWUSER -> {
                result.responseType =
                    object : TypeToken<MasterResponse<RequestFollowUnfollowUser>>() {}.type
                result.url = BASE_URL + "user"
                result.action = FOLLOWUNFOLLOWUSER
                result.requestType = RequestType.POST
                return result
            }
            GETFOLLOWPOPULARUSERSSCREENDATA -> {
                result.responseType =
                    object : TypeToken<MasterResponse<List<UserPostWrapper>>>() {}.type
                result.url = BASE_URL + "user"
                result.action = GETFOLLOWPOPULARUSERSSCREENDATA
                result.requestType = RequestType.POST
                return result
            }

            GETPOSTLIST -> {
                result.responseType = object : TypeToken<MasterResponse<List<Post>>>() {}.type
                result.url = BASE_URL + "post"
                result.action = GETPOSTLIST
                result.requestType = RequestType.POST
                return result
            }
            GETPOSTDETAILS -> {
                result.responseType =
                    object : TypeToken<MasterResponse<ResponseGetPostDetails>>() {}.type
                result.url = BASE_URL + "post"
                result.action = GETPOSTDETAILS
                result.requestType = RequestType.POST
                return result
            }
            SAVECOMMENT -> {
                result.responseType =
                    object : TypeToken<MasterResponse<ResponseSaveComment>>() {}.type
                result.url = BASE_URL + "post"
                result.action = SAVECOMMENT
                result.requestType = RequestType.POST
                return result
            }
            LIKEUNLIKECOMMENT -> {
                result.responseType =
                    object : TypeToken<MasterResponse<ResponseLikeUnLikeComment>>() {}.type
                result.url = BASE_URL + "post"
                result.action = LIKEUNLIKECOMMENT
                result.requestType = RequestType.POST
                return result
            }
            GETCOMMENTSLIST -> {
                result.responseType =
                    object : TypeToken<MasterResponse<List<UserComment>>>() {}.type
                result.url = BASE_URL + "post"
                result.action = GETCOMMENTSLIST
                result.requestType = RequestType.POST
                return result
            }
            GETCOMMENTSREPLYLIST -> {
                result.responseType =
                    object : TypeToken<MasterResponse<List<UserComment>>>() {}.type
                result.url = BASE_URL + "post"
                result.action = GETCOMMENTSREPLYLIST
                result.requestType = RequestType.POST
                return result
            }

            GET_USER_PINBOARD_LIST -> {
                result.responseType = object : TypeToken<MasterResponse<List<Pinboard>>>() {}.type
                result.url = BASE_URL + "post"
                result.action = GET_USER_PINBOARD_LIST
                result.requestType = RequestType.POST
                return result
            }

            GET_PINBOARD_POST_LIST -> {
                result.responseType = object : TypeToken<MasterResponse<UserComment>>() {}.type
                result.url = BASE_URL + "post"
                result.action = GET_PINBOARD_POST_LIST
                result.requestType = RequestType.POST
                return result
            }

            ADD_PINBOARD -> {
                result.responseType = object : TypeToken<MasterResponse<Pinboard>>() {}.type
                result.url = BASE_URL + "post"
                result.action = ADD_PINBOARD
                result.requestType = RequestType.POST
                return result
            }

            DELETE_PINBOARD -> {
                result.responseType = object : TypeToken<MasterResponse<UserComment>>() {}.type
                result.url = BASE_URL + "post"
                result.action = DELETE_PINBOARD
                result.requestType = RequestType.POST
                return result
            }


            ADD_OR_REMOVE_PINBOARD_POST -> {
                result.responseType = object : TypeToken<MasterResponse<Any>>() {}.type
                result.url = BASE_URL + "post"
                result.action = ADD_OR_REMOVE_PINBOARD_POST
                result.requestType = RequestType.POST
                return result
            }

            ACTION_ON_POST -> {
                result.responseType =
                    object : TypeToken<MasterResponse<ResponseActionOnPost>>() {}.type
                result.url = BASE_URL + "post"
                result.action = ACTION_ON_POST
                result.requestType = RequestType.POST
                return result
            }

            GET_MY_CATEGORY -> {
                result.responseType =
                    object : TypeToken<MasterResponse<List<GetCategory>>>() {}.type
                result.url = BASE_URL + "user"
                result.action = GET_MY_CATEGORY
                result.requestType = RequestType.POST
                return result
            }


        }
        throw IllegalStateException("API is not registered")
    }
}

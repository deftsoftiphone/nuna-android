package com.demo.webservice

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.demo.BuildConfig
import com.demo.R
import com.demo.activity.AccountHandlerActivity
import com.demo.base.MainApplication
import com.demo.model.request.FollowHashTagRequest
import com.demo.model.request.Language
import com.demo.model.request.RequestUserRegister
import com.demo.model.request.RequestValidate
import com.demo.model.response.baseResponse.*
import com.demo.providers.resources.ResourcesProvider
import com.demo.util.NoInternetException
import com.demo.util.Prefs
import com.demo.util.clearNotification
import com.demo.util.runWithDelay
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

interface APIService {

    //Login
    @GET("api/language/listLanguage")
    suspend fun getLanguages(): BaseResponse

    @POST("api/users/loginWithMobileNumber")
    suspend fun login(@Body payload: RequestUserRegister): BaseResponse

    @POST("api/users/verifyOTP")
    suspend fun verifyOTP(@Body payload: RequestValidate): BaseResponse

    @POST("api/users/logOut")
    suspend fun logout(): BaseResponse

    @GET("api/hashtag/listHashTag")
    fun getHashTags(
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
        @Query("search") search: String?,
        @Query("startsWith") startsWith: String?,
        @Query("languageId") languageId: String,
        @Query("categoryId") categoryId: String?
    ): Deferred<BaseResponse>

    @GET("api/hashtag/listHashTag")
    fun getHashTag(
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
        @Query("search") search: String?,
        @Query("startsWith") startsWith: String?,
        @Query("languageId") languageId: String,
        @Query("categoryId") categoryId: String?
    ): Deferred<BaseResponse>

    @GET("api/dashboard/discover/following")
    fun getDiscoverFollowing(
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
        @Query("authDeviceToken") authDeviceToken: String?
    ): Deferred<BaseResponse>

    @GET("api/dashboard/discover/popular")
    fun getDiscoverPopular(
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
        @Query("recombee") useRecombee: Boolean?,
        @Query("authDeviceToken") authDeviceToken: String?,
    ): Deferred<BaseResponse>

    @GET("api/dashboard/discover/popular")
    fun getDiscoverPopularCateggories(
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
        @Query("categoryId") id: String?,
        @Query("recombee") useRecombee: Boolean?,
        @Query("authDeviceToken") authDeviceToken: String?
    ): Deferred<BaseResponse>

    @GET("api/users/detail")
    fun getUserDetails(): Deferred<BaseResponse>

    @GET("api/users/detail/")
    fun getOtherUserDetails(
        @Query("userId") id: String?
    ): Deferred<BaseResponse>

    @GET("api/posts/listPost")
    fun getOtherUserPosts(
        @Query("userId") id: String?,
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?
    ): Deferred<BaseResponse>

    @GET("api/posts/listPost")
    fun getUserPosts(
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?
    ): Deferred<BaseResponse>

    @Multipart
    @POST("api/posts/addPost")
    fun createPost(
        @Part languageId: MultipartBody.Part,
        @Part categoryId: MultipartBody.Part,
        @Part description: MultipartBody.Part,
        @Part image: MultipartBody.Part?,
        @Part video: MultipartBody.Part
    ): Deferred<BaseResponse>

    @POST("api/posts/addPost")
    fun createPostV2(@Body request: AddPostRequest): Deferred<BaseResponse>


    @Multipart
    @PUT("api/users/uploadProfile")
    fun updateProfilePicture(
        @Part image: MultipartBody.Part?
    ): Deferred<BaseResponse>

    @POST("api/users/checkUserName")
    suspend fun checkUserNameAvailability(@Body payload: User): BaseResponse

    @PUT("api/users/updateUser")
    suspend fun updateUserDetails(@Body payload: PutUserProfile): BaseResponse

    @PUT("api/users/updateLanguage")
    suspend fun updateLanguage(@Body payload: Language): BaseResponse

    @PUT("api/users/updateUserName")
    suspend fun updateUserName(@Body payload: PutUserProfile): BaseResponse

    @GET("api/hashtag/overView")
    suspend fun getHashTagOverview(
        @Query("postOffset") postOffset: Int?,
        @Query("postLimit") postLimit: Int?,
        @Query("hashTagOffset") hashTagOffset: Int?,
        @Query("hashTagLimit") hashTagLimit: Int?,
        @Query("categoryId") categoryId: String?
    ): BaseResponse

    @GET("api/hashtag/details/{id}")
    suspend fun getAllPostOfAHashTag(
        @Path(value = "id", encoded = true) hashTagId: String,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?,
        @Query("categoryId") categoryId: String?
    ): BaseResponse

    @POST("api/hashtag/followHashTag")
    suspend fun followHashTag(@Body request: FollowHashTagRequest): BaseResponse

    @POST("api/users/followUser")
    suspend fun followUser(@Body request: User): BaseResponse

    @GET("api/notification/list")
    suspend fun getNotifications(
        @Query("tab") tab: String?,
        @Query("notificationLimit") notificationLimit: Int?,
        @Query("notificationOffset") notificationOffset: Int?,
        @Query("suggestionLimit") suggestionLimit: Int?,
        @Query("suggestionOffset") suggestionOffset: Int?
    ): BaseResponse

    @GET("api/dashboard/search")
    suspend fun search(
        @Query("tab") tab: String,
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
        @Query("startsWith") startsWith: String?,
        @Query("searchText") searchText: String?,
        @Query("for") forDashboard: String?
    ): BaseResponse

    @GET("api/users/userData/{tab}")
    suspend fun getProfileData(
        @Path("tab") tab: String,
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
        @Query("userId") userId: String?
    ): BaseResponse

    @DELETE("api/posts/removePost/{id}")
    suspend fun removePost(
        @Path(value = "id", encoded = true) postId: String
    ): BaseResponse

    @POST("api/like")
    suspend fun likePost(@Body request: LikePostRequest): BaseResponse

    @GET("api/comments/listComment")
    suspend fun getPostComments(
        @Query("postId") postId: String?,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): BaseResponse

    @POST("api/like")
    suspend fun likeComment(@Body request: LikeCommentRequest): BaseResponse

    @Multipart
    @POST("api/comments/addComment")
    suspend fun addComment(
        @Part postId: MultipartBody.Part,
        @Part description: MultipartBody.Part
    ): BaseResponse

    @PUT("api/posts/reportPost")
    suspend fun reportPost(@Body request: ReportPostRequest): BaseResponse

    @POST("api/userProfileBoard/create")
    suspend fun savePost(@Body request: SavePostRequest): BaseResponse

    @GET("api/userProfileBoard/details")
    suspend fun getUserBoard(
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): BaseResponse

    @GET("api/posts/postDetails/{id}")
    suspend fun getPostDetail(
        @Path("id") id: String
    ): BaseResponse

    @POST("api/share")
    suspend fun sharePost(@Body request: SharePostRequest): BaseResponse

    @GET("api/hashTagImage/listImages")
    suspend fun getHashTagBanner(
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?
    ): BaseResponse

    @GET("api/notification/totalNotifications")
    suspend fun getTotalNoOfNotification(): BaseResponse

    @POST("api/notification/read/{id}")
    suspend fun readNotification(
        @Path("id") id: String
    ): BaseResponse

    @PUT("api/users/notificationStatus")
    suspend fun updateNotificationStatus(
        @Body request: UpdateNotificationStatusRequest
    ): BaseResponse

    @GET("api/category/list")
    suspend fun getPostCategories(
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
    ): BaseResponse

    @GET("api/category/listAllPostCategories")
    suspend fun getDashboardCategories(
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
    ): BaseResponse

    @POST("api/posts/addViewPortion")
    suspend fun updatePostViewDuration(@Body request: UpdatePostDurationRequest): BaseResponse

    companion object {
        //        const val BASE_URL = "http://13.127.69.151:4000/"         //Live Server
//        const val BASE_URL = "http://192.168.3.136:4000/"        //Local Test

        //        const val BASE_URL = "http://192.168.3.152:4001/"        //Local Testa
//        const val BASE_URL = "http://192.168.3.152:4004/"        //Local Test
//        const val BASE_URL = "http://192.168.3.141:4000/"        //Local Test
//         const val BASE_URL = "http://192.168.3.173:4000/"        //Local Test
//        const val BASE_URL = "http://192.168.3.173:5002/"        //Local Test
//        const val BASE_URL = "http://1.6.98.142:3000/"        //Local Test
//        const val BASE_URL = "http://65.0.108.28:3000/"         //Live - 2
//        const val BASE_URL = "http://13.127.69.151:4000/"       //Live -3
//        const val BASE_URL = "http://52.66.228.155:4000/"       //Live - 4
//        const val BASE_URL = "http://13.232.18.204:3000/"           //Staging Dev Environment

//        const val BASE_URL = "http://1.6.98.142:3000/"           //Dev Environment
        const val BASE_URL = "http://nuna.app:4000/"       //Live - 4

        //        const val API_VERSION = "v2/"
//        const val API_VERSION = "v3/"
        const val API_VERSION = "v3.1/"
//        const val BASE_URL = "http://192.168.3.173:5009/"        //Local Test
//        const val API_VERSION = "v4/"

        fun getErrorMessageFromGenericResponse(
            exception: Exception,
            resources: ResourcesProvider
        ): Error? {
            val error = Error()
            try {
                when (exception) {
                    is NoInternetException -> {
                        error.message = resources.getString(R.string.connectErr)
                    }
                    is HttpException -> {
                        val body = exception.response()?.errorBody()
                        val adapter = Gson().getAdapter(BaseResponse::class.java)
                        val errorParser = adapter.fromJson(body?.string())
                        error.message = errorParser.message ?: exception.message()
                        if (error.message.isNullOrEmpty()) {
                            error.message = exception.message()
                            error.status = exception.code()
                        }
                    }
                    is ConnectException -> {
                        error.message = resources.getString(R.string.connectErr)
                    }
                    is SocketTimeoutException -> {
                        error.message = resources.getString(R.string.timeoutErr)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                return error
            }
        }

        private fun getAPIService(): APIService {
            val client =
                OkHttpClient.Builder().addInterceptor(provideHeaderInterceptor()!!)
                    .addInterceptor(ForbiddenInterceptor())
                    .addInterceptor(provideHttpLoggingInterceptor()!!)
                    .addInterceptor(ExceptionInterceptor())
                    .connectTimeout(90, TimeUnit.SECONDS)
                    .readTimeout(90, TimeUnit.SECONDS)
                    .writeTimeout(90, TimeUnit.SECONDS)
                    .build()

            return Retrofit.Builder()
                .baseUrl("${BASE_URL}${API_VERSION}")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build().create(APIService::class.java)
        }

        private fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor? {
            val httpLoggingInterceptor =
                HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                    override fun log(message: String) {
                        Log.d("API Logging", "response => $message")
                    }
                })
            httpLoggingInterceptor.setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)
            return httpLoggingInterceptor
        }

        private fun provideHeaderInterceptor(): Interceptor? {
            return object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    //*Code for request with auth token
                    val accessToken: String = Prefs.init().currentToken
                    return if (!TextUtils.isEmpty(accessToken)) {
                        val request: Request = chain.request().newBuilder()
                            .addHeader("Accept", "application/json")
                            .addHeader("x-access-token", "Bearer $accessToken").build()
                        chain.proceed(request)
                    } else {
                        val request: Request = chain.request().newBuilder()
                            .addHeader("Accept", "application/json").build()
                        chain.proceed(request)
                    }
                }
            }
        }

        class ForbiddenInterceptor : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val request: Request = chain.request()
                return chain.proceed(request)
            }
        }

        class ExceptionInterceptor : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request()
                val response = chain.proceed(request)
                if (response.code == 500) {
                    return response
                } else if (response.code == 402) {
                    return response
                } else if (response.code == 403) {
                    runWithDelay(1000) {
                        val app = MainApplication.get()
                        if (app.getCurrentActivity()?.localClassName != AccountHandlerActivity::class.java.name) {
                            val context = app.getContext()
                            app.offlineDeactivatedUser()
                            Prefs.init().clear()
                            context.clearNotification()
                            context.startActivity(
                                Intent(context, AccountHandlerActivity::class.java).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                })
                            app.getCurrentActivity()?.finish()
                        }
                    }
                    return response
                } else if (response.code == 451) {
                    runWithDelay(1000) {
                        val app = MainApplication.get()
                        if (app.getCurrentActivity()?.localClassName != AccountHandlerActivity::class.java.name) {
                            val context = app.getContext()
                            app.offlineDeactivatedUser()
                            Prefs.init().clear()
                            context.clearNotification()
                            context.startActivity(
                                Intent(context, AccountHandlerActivity::class.java).apply {
                                    putExtra(
                                        context.getString(R.string.key_user_blocked),
                                        context.getString(R.string.account_blocked_by_admin)
                                    )
                                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                })
                            app.getCurrentActivity()?.finish()
                        }
                    }
                } else if (response.code == 423) {
                    runWithDelay(1000) {
                        val app = MainApplication.get()
                        if (app.getCurrentActivity()?.localClassName != AccountHandlerActivity::class.java.name) {
                            val context = app.getContext()
                            app.offlineDeactivatedUser()
                            Prefs.init().clear()
                            context.clearNotification()
                            context.startActivity(
                                Intent(context, AccountHandlerActivity::class.java).apply {
                                    putExtra(
                                        context.getString(R.string.key_user_blocked),
                                        context.getString(R.string.account_logged_on_another_device)
                                    )
                                        .flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                })
                            app.getCurrentActivity()?.finish()
                        }
                    }
                }
                return response
            }

        }

        operator fun invoke() = getAPIService()
    }
}
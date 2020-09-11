package com.demo.webservice

import android.text.TextUtils
import android.util.Log
import com.demo.BuildConfig
import com.demo.R
import com.demo.model.request.FollowHashTagRequest
import com.demo.model.request.Language
import com.demo.model.request.RequestUserRegister
import com.demo.model.request.RequestValidate
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.Error
import com.demo.model.response.baseResponse.PutUserProfile
import com.demo.model.response.baseResponse.User
import com.demo.providers.resources.ResourcesProvider
import com.demo.util.Prefs
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


interface APIService {

    //Login
    @GET("api/language/listLanguage")
    suspend fun getLanguages(): BaseResponse

    @POST("api/users/loginWithMobileNumber")
    suspend fun login(@Body payload: RequestUserRegister): BaseResponse

    @POST("api/users/verifyOTP")
    suspend fun verifyOTP(@Body payload: RequestValidate): BaseResponse

    @POST("/api/users/logOut")
    suspend fun logout(): BaseResponse

    @GET("api/category/listCategories")
    fun getCategories(
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
        @Query("search") search: String?,
        @Query("languageId") languageId: String?
    ): Deferred<BaseResponse>

    @GET("api/hashtag/listHashTag")
    fun getHashTags(
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
        @Query("limit") limit: Int?
    ): Deferred<BaseResponse>

    @GET("api/dashboard/discover/popular")
    fun getDiscoverPopular(
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?
    ): Deferred<BaseResponse>

    @GET("api/dashboard/discover/popular")
    fun getDiscoverPopularCateggories(
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
        @Query("categoryId") id: String?
    ): Deferred<BaseResponse>

    @GET("api/users/detail")
    fun getUserDetails(
    ): Deferred<BaseResponse>

    @GET("api/users/detail/")
    fun getOtherUserDetails(
        @Query("userId") id: String?
    ): Deferred<BaseResponse>

    @GET("/api/posts/listPost")
    fun getOtherUserPosts(
        @Query("userId") id: String?
    ): Deferred<BaseResponse>

    @GET("/api/posts/listPost")
    fun getUserPosts(
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

    @Multipart
    @PUT("/api/users/uploadProfile")
    fun updateProfilePicture(
        @Part image: MultipartBody.Part?
    ): Deferred<BaseResponse>

    @POST("/api/users/checkUserName")
    suspend fun checkUserNameAvailability(@Body payload: User): BaseResponse

    @PUT("/api/users/updateUser")
    suspend fun updateUserDetails(@Body payload: PutUserProfile): BaseResponse

    @PUT("/api/users/updateLanguage")
    suspend fun updateLanguage(@Body payload: Language): BaseResponse

    @PUT("/api/users/updateUserName")
    suspend fun updateUserName(@Body payload: PutUserProfile): BaseResponse

    @GET("api/hashtag/overView")
    suspend fun getHashTagOverview(
        @Query("postOffset") postOffset: Int?,
        @Query("postLimit") postLimit: Int?,
        @Query("hashTagOffset") hashTagOffset: Int?,
        @Query("hashTagLimit") hashTagLimit: Int?,
        @Query("categoryId") categoryId: String?
    ): BaseResponse

    @GET("/api/hashtag/details/{id}")
    suspend fun getAllPostOfAHashTag(
        @Path(value = "id", encoded = true) hashTagId: String,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?,
        @Query("categoryId") categoryId: String?
    ): BaseResponse

    @POST("/api/hashtag/followHashTag")
    suspend fun followHashTag(@Body request: FollowHashTagRequest): BaseResponse

    @POST("/api/users/followUser")
    suspend fun followUser(@Body request: User): BaseResponse

    @GET("/api/notification/list")
    suspend fun getNotifications(
        @Query("tab") tab: String?,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): BaseResponse

    @GET("/api/dashboard/search")
    suspend fun search(
        @Query("tab") tab: String,
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
        @Query("startsWith") startsWith: String?,
        @Query("searchText") searchText: String?
    ): BaseResponse

    @GET("/api/users/userData/{tab}")
    suspend fun getProfileData(
        @Path("tab") tab: String,
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
        @Query("userId") userId: String?
    ): BaseResponse


    companion object {
        //Local
//        const val BASE_URL = "http://192.168.3.98:3000/"        //Test
        const val BASE_URL = "http://1.6.98.142:3000/"            //Local
//         const val BASE_URL = "http://1.6.98.140:3000/"         //Local Web
//         const val BASE_URL = " http://13.127.69.151:4000/"      //Live Server

        fun getErrorMessageFromGenericResponse(
            exception: Exception,
            resources: ResourcesProvider
        ): Error? {
            val error = Error()
            try {
                when (exception) {
                    is HttpException -> {
                        val body = exception.response()?.errorBody()
                        val adapter = Gson().getAdapter(BaseResponse::class.java)
                        val errorParser = adapter.fromJson(body?.string())
                        error.message = errorParser.message ?: exception.message()
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
                    .addInterceptor(ExceptionInterceptor()).build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
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
                }

                return response
            }

        }

        operator fun invoke() = getAPIService()
    }
}
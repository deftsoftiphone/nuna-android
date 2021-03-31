package com.demo.muebert.network

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
import com.demo.muebert.modal.BaseRequest
import com.demo.providers.resources.ResourcesProvider
import com.demo.util.Prefs
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.*
import okhttp3.Response
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
    /*Muebert APIs*/

    @POST("GetServiceAccess")
    suspend fun getAccess(@Body request: BaseRequest): com.demo.muebert.modal.BaseResponse

    @POST("GetPlayMusic")
    suspend fun getMusic(@Body request: BaseRequest): com.demo.muebert.modal.BaseResponse

    @POST("RecordTrack")
    suspend fun recordTrack(@Body request: BaseRequest): com.demo.muebert.modal.BaseResponse

    @POST("GetTrack")
    suspend fun getTrack(@Body request: BaseRequest): com.demo.muebert.modal.BaseResponse


    companion object {

        /*Muebert Base URLs*/
        const val BASE_URL = "https://api-b2b.mubert.com/v2/"      //Local Test

        fun getErrorMessageFromGenericResponse(
            exception: Exception,
            resources: ResourcesProvider
        ): com.demo.muebert.modal.Error? {
            val error = com.demo.muebert.modal.Error()
            try {
                when (exception) {
                    is HttpException -> {
                        val body = exception.response()?.errorBody()
                        val adapter = Gson().getAdapter(com.demo.muebert.modal.BaseResponse::class.java)
                        val errorParser = adapter.fromJson(body?.string())
                        error.text = errorParser.error?.text ?: exception.message()
                        if (error.text.isNullOrEmpty()) {
                            error.text = exception.message()
                        }
                    }
                    is ConnectException -> {
                        error.text = resources.getString(R.string.connectErr)
                    }
                    is SocketTimeoutException -> {
                        error.text = resources.getString(R.string.timeoutErr)
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
                } else if (response.code == 403) {
                    Prefs.init().clear()
                    MainApplication.get().getContext().startActivity(
                        Intent(
                            MainApplication.get().getContext(),
                            AccountHandlerActivity::class.java
                        ).apply {
                            flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                }
                return response
            }

        }

        operator fun invoke() = getAPIService()
    }
}
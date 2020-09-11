package com.demo.webservice

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.MutableLiveData
import com.demo.BuildConfig
import com.demo.base.AsyncViewController
import com.demo.base.MainApplication
import com.demo.model.request.WrapperMultiPartRequest
import com.demo.model.response.MasterResponse
import com.demo.util.Prefs
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class RestClient() {

    val CONNECTION_TIMEOUT = 300
    val RESULT_FAILED = 7
    val RESULT_NO_INTERNET = 8
    val RESULT_UNKNOWN = 9

    var asyncViewController: AsyncViewController? = null
    var apiResponseListener: ApiResponseListener? = null
    private var apiInterface: ApiInterface?
    var apiService: APIService?
    private val activeApiCalls = ArrayList<Call<*>>()
    private val headers = HashMap<String, String>()
    var gson: Gson

    init {


        apiInterface = getApiInterface()
        apiService = getAPICalls()
        headers["Content-Type"] = "application/json"
        // headers["ApiServiceToken"] = "48DQHBv9yIY"
        headers["Offset"] = "19800"
        headers["AppVersion"] = "1.0"
        gson = Gson()

        gson = Gson()
        Prefs.init().loginData?.apply {
            headers["Authorization"] = "$authToken"
        }
    }


    /**
     * provides retrofit client with proxy implemented api interface
     *
     * @return
     */
    private fun getApiInterface(): ApiInterface? {

        if (apiInterface == null) {
            val client = getOkHttpClient() ?: return null
            val gson = GsonBuilder().setLenient().create()
            val builder = Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
            builder.baseUrl(ApiRegister.BASE_URL)
            return builder.build().create(ApiInterface::class.java)
        } else {
            return apiInterface
        }
    }


    private fun getAPICalls(): APIService? {
        if (apiService == null) {
            val client = getOkHttpClient() ?: return null
            val builder = Retrofit.Builder()
                .client(client)
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder().setLenient().create()
                    )
                )
            builder.baseUrl(ApiRegister.BASE_URL)
            return builder.build().create(APIService::class.java)
        } else {
            return apiService
        }
    }

    /**
     * get OkHttpClient
     *
     * @return OkHttpClient
     */
    private fun getOkHttpClient(): OkHttpClient? {
        try {
            val okClientBuilder = OkHttpClient.Builder()

            okClientBuilder.connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
            okClientBuilder.readTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
            okClientBuilder.writeTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)

            if (BuildConfig.DEBUG) {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                okClientBuilder.addInterceptor(httpLoggingInterceptor)
                // okClientBuilder.addInterceptor(ChuckInterceptor(MainApplication.get().getContext()))
            }
            return okClientBuilder.build()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * format error string here
     *
     * @param rawError
     * @return
     */
    private fun getError(rawError: String?): String {
        if (rawError == null) {
            return "Error Occurred"
        }
        val formulatedError = MainApplication.get().getContext()
            .getString(com.demo.R.string.bad_response)
        return if (rawError.contains("JsonReader.setLenient")) {
            formulatedError
        } else if (rawError.contains("Unable to resolve host")) {
            "Couldn't connect to server"
        } else
            rawError
    }

    /**
     * checks network connectivity
     *
     * @return
     */
    private fun isConnectedToNetwork(): Boolean {
        val cm =
            MainApplication.get().getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val ni = cm.activeNetworkInfo
        return ni != null
    }

    /**
     * showProgressDialog while api call is active
     */
    private fun showProgressDialog() {
        asyncViewController?.showProgressDialog()
    }

    /**
     * hide progress after api calling
     */
    private fun hideProgressDialog() {
        asyncViewController?.hideProgressDialog()
    }


    /**
     * common checks before any api calling
     *
     * @param StringApiInterface
     * @return
     */
    private fun passChecks(): Boolean {
        if (!isConnectedToNetwork()) {
            asyncViewController?.onNoInternet()
            return false
        }

        return apiInterface != null
    }

    fun getListener(): ApiResponseListener? {
        return apiResponseListener
    }

    fun callApi(String: String, requestPojo: Any?, dataCarrier: MutableLiveData<*>) {
        callApi(String, requestPojo, dataCarrier, true)
    }

    fun callApi(
        String: String,
        requestPojo: Any?,
        dataCarrier: MutableLiveData<*>?,
        showProgressDialog: Boolean
    ) {

        if (!passChecks()) {
            return
        }

        val apiRequestType = ApiRegister.getApiRequestType(String)

        headers["action"] = apiRequestType.action

        var jsonRequest: JsonObject? = null

        if (apiRequestType.requestType !== RequestType.MULTIPART) {
            if (requestPojo != null) {
                val requestStr = gson.toJson(requestPojo)
                jsonRequest = JsonParser().parse(requestStr).asJsonObject
            } else {
                jsonRequest = JsonObject()
            }
        }

        var call: Call<ResponseBody>? = null

        if (apiRequestType.requestType === RequestType.POST) {
            if (apiRequestType.url.contains("\$param")) {
                apiRequestType.url.replace(
                    "\$param",
                    jsonRequest!!.get("_param").asString
                )
            }
            call = getApiInterface()!!.postApi(headers, apiRequestType.url, jsonRequest)

        } else if (apiRequestType.requestType === RequestType.MULTIPART) {

            val requestData = requestPojo as WrapperMultiPartRequest
            val multipartSpecificHeader = HashMap(headers)
            multipartSpecificHeader.remove("Content-Type")
            call = getApiInterface()!!.postMultipart(
                multipartSpecificHeader,
                apiRequestType.url,
                requestData.getMultipartBody()
            )

        } else if (apiRequestType.requestType === RequestType.GET) {

            if (apiRequestType.url.contains("\$param")) {
                apiRequestType.url.replace("\$param", jsonRequest!!.get("_param").asString)
            }
            call = getApiInterface()!!.getApi(headers, apiRequestType.url)

        }

        if (showProgressDialog) {
            showProgressDialog()
        }

        call!!.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                activeApiCalls.remove(call)

                val responseBody = response.body()
                if (responseBody != null) {
                    val responseString: String
                    try {
                        responseString = responseBody.string()


                        val master: MasterResponse<*> =
                            gson.fromJson(responseString, apiRequestType.responseType)

                        if (master.responseCode == 200) {
                            dataCarrier?.value = master

                        } else if (master.responseCode == 403) {

                            val wrapperApiError = WrapperApiError(
                                String,
                                master.responseCode,
                                master.successMsg,
                                master.validationErrors
                            )
                            dispatchError(wrapperApiError)

                        } else {

                            val wrapperApiError = WrapperApiError(
                                String,
                                master.responseCode,
                                master.successMsg,
                                master.validationErrors
                            )
                            dispatchError(wrapperApiError)
                            // Validator.showCustomToast(master.successMsg.toString())//lks

                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                } else {

                    var wrapperApiError: WrapperApiError? = null
                    val errBody = response.errorBody()
                    if (errBody != null) {
                        try {
                            val errBodyStr = errBody.string()
                            val errInPojo: MasterResponse<*> =
                                gson.fromJson(errBodyStr, MasterResponse::class.java)
                            wrapperApiError = WrapperApiError(
                                String,
                                errInPojo.responseCode,
                                errInPojo.successMsg,
                                errInPojo.validationErrors
                            )

                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            hideProgressDialog()
                            wrapperApiError = WrapperApiError(
                                String,
                                RESULT_UNKNOWN,
                                MainApplication.get()
                                    .getString(com.demo.R.string.something_went_wrong)
                            )
                        }
                    }
                    dispatchError(wrapperApiError!!)
                }
                hideProgressDialog()

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                activeApiCalls.remove(call)
                val err = getError(t.message)
                val wrapperApiError = WrapperApiError(
                    String,
                    RESULT_FAILED,
                    err
                )
                dispatchError(wrapperApiError)
                hideProgressDialog()
            }
        })
    }

    private fun dispatchError(apiErr: WrapperApiError) {

        var result = apiErr.msg

        if (apiErr.msg.equals("validation error", true)) {
            result += ":\n"
            apiErr.validationErr.forEach {
                result += "\n\n $it"
            }
        }

        getListener()?.onApiCallFailed(apiErr.apiUrl, apiErr.resultCode, result)
            ?: asyncViewController?.onApiCallFailed(apiErr.apiUrl, apiErr.resultCode, result)
    }
}

/*
private fun getRequestPart(text: String): RequestBody {
    return RequestBody.create(MediaType.parse("text/plain"), text)
}

private fun getEmptyMultiPart(): MultipartBody.Part {
    val mFile = RequestBody.create(MediaType.parse("image/*"), "")
    return MultipartBody.Part.createFormData("attachment", "", mFile)
}
}

private fun getRequestBodyOfFile(file: File?): RequestBody {
return if (file == null) RequestBody.create(MediaType.parse("image/*"), "")
else RequestBody.create(MediaType.parse("image/*"), file)
}

private fun getMultipartObject(file: File?): MultipartBody.Part {
file?.apply {
    val multipartRequestData = RequestBody.create(MediaType.parse("image/*"), file)
    return MultipartBody.Part.createFormData("File", file.name, multipartRequestData)
}
return getEmptyMultiPart()
}*/
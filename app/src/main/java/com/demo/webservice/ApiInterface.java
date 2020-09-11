package com.demo.webservice;


import com.demo.model.request.RequestUserRegister;
import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiInterface {

    @POST
    Call<ResponseBody> postApi(@HeaderMap Map<String, String> headers, @Url String url, @Body JsonObject request);

    @GET
    Call<ResponseBody> getApi(@HeaderMap Map<String, String> headers, @Url String url);

    /*@Multipart
    @POST
    Call<ResponseBody> postMultipart(
            @HeaderMap Map<String, String> headers,
            @Url String url,
            @Part("JsonModel") RequestBody dataMap,
            @Part MultipartBody.Part file
    );*/

    @POST
    Call<ResponseBody> postMultipart(
            @HeaderMap Map<String, String> headers,
            @Url String url,
            @Body RequestBody body
    );




}

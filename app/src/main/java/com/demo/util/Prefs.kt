package com.demo.util

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.text.TextUtils
import com.demo.base.MainApplication
import com.demo.model.request.RequestLogin
import com.demo.model.response.NotificationListShow
import com.demo.model.response.ResponseGetProfile
import com.demo.model.response.ResponseLogin
import com.demo.model.response.ResponseUploadDocuments
import com.demo.model.response.baseResponse.Language
import com.demo.model.response.baseResponse.User
import com.demo.model.response.baseResponse.UserProfile

import com.google.gson.Gson

class Prefs {

   private val DEVICE_DISPLAY_WIDTH = "DEVICE_DISPLAY_WIDTH"
   private val PREF_AUTH_TOKEN = "_PREF_AUTH_TOKEN"
   private val PREF_LOGIN_DATA = "_PREF_LOGIN_DATA"
   private val PREF_CURRENT_USER = "_PREF_CURRENT_USER"
   private val PREF_NOTI_LIST = "_PREF_NOTI_LIST"
   private val PREF_PROFILE_DATA = "_PREF_PROFILE_DATA"
   private val PREF_NAME_GLOBAL = "_global_pref"
   private val PREF_SELECTED_LANG_ID = "_PREF_SELECTED_LANG_ID"
   private val PREF_SELECTED_LANG = "_PREF_SELECTED_LANG"
   private val CURRENT_TOKEN = "_CURRENT_TOKEN"
   private val AWSACCESSKEYID = "accesskey"
   private val AWSSECRETKEY = "secretKey"
   private val PREF_VIDEOSELECT = "_isVideoSelect"
   private val PREF_NOTIFICATION = "_notification"
   private val PREF_NOTIFICATION_DATA = "_notification_data"
   private val PREF_SAVE_CREDENTAILS = "_PREF_SAVE_CREDENTAILS"
   private val PREF_CREDENTAILS = "_PREF_CREDS"
   private val PREF_TEMP_UPLOAD_RESPONSE = "_PREF_TEMP_UPLOAD_RESPONSE"

    private var sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(MainApplication.get().getContext())

    init {
        instance = this
    }

    val gson = Gson()

    companion object {
        private var instance: Prefs? = null
        fun init(): Prefs {
            if (instance == null) {
                instance = Prefs()
            }
            return instance!!
        }
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    /*Authentication Token String for API Authentication*/
    /*var authenticationToken : String
    get() {return sharedPreferences.getString(PREF_AUTH_TOKEN,"") ?: ""}
    set(value) {sharedPreferences.edit().putString(PREF_AUTH_TOKEN,value).apply()}*/


    //Device Token, saved in separate SharedPreferences {PREF_NAME_GLOBAL} to persist data on user logout
    var deviceToken: String
        get() {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            return sF.getString(PREF_AUTH_TOKEN, "") ?: ""
        }
        set(value) {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            sF.edit().putString(PREF_AUTH_TOKEN, value).apply()
        }

    var deviceDisplayWidth: String
        get() {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            return sF.getString(DEVICE_DISPLAY_WIDTH, "") ?: ""
        }
        set(value) {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            sF.edit().putString(DEVICE_DISPLAY_WIDTH, value).apply()
        }


    var currentToken: String
        get() {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            return sF.getString(CURRENT_TOKEN, "") ?: ""
        }
        set(value) {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            sF.edit().putString(CURRENT_TOKEN, value).apply()
        }


    var accesskeyId: String
        get() {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            return sF.getString(AWSACCESSKEYID, "") ?: ""
        }
        set(value) {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            sF.edit().putString(AWSACCESSKEYID, value).apply()
        }


    var secretKey: String
        get() {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            return sF.getString(AWSSECRETKEY, "") ?: ""
        }
        set(value) {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            sF.edit().putString(AWSSECRETKEY, value).apply()
        }


    var selectedLangId: Int
        get() {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            return sF.getInt(PREF_SELECTED_LANG_ID, 0) ?: 0
        }
        set(value) {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            sF.edit().putInt(PREF_SELECTED_LANG_ID, value).apply()
        }

    var selectedLang: Language
        get() {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            val value = sF.getString(PREF_SELECTED_LANG, "") ?: ""
            return if (!TextUtils.isEmpty(value)) {
                gson.fromJson(value, Language::class.java)
            } else {
                Language()
            }
        }
        set(value) {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            val language = gson.toJson(value)
            sF.edit().putString(PREF_SELECTED_LANG, language).apply()
        }




    /*User Login Data received on successful Login*/
    var loginData: ResponseLogin?
        get() {
            val str = sharedPreferences.getString(PREF_LOGIN_DATA, "") ?: ""
            if (!str.isBlank()) return gson.fromJson(str, ResponseLogin::class.java)
            return null
        }
        set(value) {
            sharedPreferences.edit().putString(PREF_LOGIN_DATA, gson.toJson(value)).apply()
        }

    var currentUser: User?
        get() {
            val str = sharedPreferences.getString(PREF_CURRENT_USER, "") ?: ""
            if (!TextUtils.isEmpty(str)) return gson.fromJson(str, User::class.java)
            return null
        }
        set(value) {
            sharedPreferences.edit().putString(PREF_CURRENT_USER, gson.toJson(value)).apply()
        }


    var notificationListShow: NotificationListShow?
        get() {
            val str = sharedPreferences.getString(PREF_NOTI_LIST, "") ?: ""
            if (!str.isBlank()) return gson.fromJson(str, NotificationListShow::class.java)
            return null
        }
        set(value) {
            sharedPreferences.edit().putString(PREF_NOTI_LIST, gson.toJson(value)).apply()
        }


    /*User Login Data received on successful Login*/
    var profileData: UserProfile?
        get() {
            val str = sharedPreferences.getString(PREF_PROFILE_DATA, "") ?: ""
            if (!str.isBlank()) return gson.fromJson(str, UserProfile::class.java)
            return null
        }
        set(value) {
            sharedPreferences.edit().putString(PREF_PROFILE_DATA, gson.toJson(value)).apply()
        }


    /*Flag to save User Credentials for AutoFill in Login*/
    var rememberMe: Boolean
        get() {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            return sF.getBoolean(PREF_SAVE_CREDENTAILS, false)
        }
        set(value) {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            sF.edit().putBoolean(PREF_SAVE_CREDENTAILS, value).apply()
        }


    var notificationAll: Boolean
        get() {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            return sF.getBoolean(PREF_NOTIFICATION, true)
        }
        set(value) {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            sF.edit().putBoolean(PREF_NOTIFICATION, value).apply()
        }

    var isVideoSelect: Boolean
        get() {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            return sF.getBoolean(PREF_VIDEOSELECT, false)
        }
        set(value) {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            sF.edit().putBoolean(PREF_VIDEOSELECT, value).apply()
        }

    var notificationData: String?
        get() {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            return sF.getString(PREF_NOTIFICATION_DATA, "") ?: ""
        }
        set(value) {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            sF.edit().putString(PREF_NOTIFICATION_DATA, value).apply()
        }


    /*User Creds*/
    var userCreds: RequestLogin?
        get() {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            val str = sF.getString(PREF_CREDENTAILS, "") ?: ""
            if (!str.isBlank()) return gson.fromJson(str, RequestLogin::class.java)
            return null
        }
        set(value) {
            val sF = MainApplication.get().getContext()
                .getSharedPreferences(PREF_NAME_GLOBAL, MODE_PRIVATE)
            sF.edit().putString(PREF_CREDENTAILS, gson.toJson(value)).apply()
        }

    var tempUploadResponse: ResponseUploadDocuments?
        get() {
            val str = sharedPreferences.getString(PREF_TEMP_UPLOAD_RESPONSE, "") ?: ""
            if (!str.isBlank()) return gson.fromJson(str, ResponseUploadDocuments::class.java)
            return null
        }
        set(value) {
            sharedPreferences.edit().putString(PREF_TEMP_UPLOAD_RESPONSE, gson.toJson(value))
                .apply()
        }

}
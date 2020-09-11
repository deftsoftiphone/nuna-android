package com.demo.followPopularUsers

import android.os.Handler
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.demo.R
import com.demo.base.AsyncViewController
import com.demo.base.BaseViewModel
import com.demo.base.MainApplication
import com.demo.model.UserPostWrapper
import com.demo.model.request.RequestFollowUnfollowUser
import com.demo.model.request.RequestGetPopularUsers
import com.demo.model.response.MasterResponse
import com.demo.model.response.ResponseGetDocuments
import com.demo.util.Prefs
import com.demo.webservice.ApiRegister
import java.util.*


class FollowPopularUsersViewModel (controller: AsyncViewController) : BaseViewModel(controller){

    val responseGetPopularUsers_NEW = MutableLiveData<MasterResponse<List<UserPostWrapper>>>()
    val responseFollowUnfollowUser = MutableLiveData<MasterResponse<RequestFollowUnfollowUser>>()
    val responseDocumentList_PAGING = MutableLiveData<MasterResponse<ResponseGetDocuments>>()

    private val searchHandler = Handler()

    private var lastRequest = RequestGetPopularUsers()
    var lastSearchKeyword = ObservableField<String>("")
    var textNoData = ObservableField<String>(MainApplication.get().getContext().getString(R.string.error_add_text_pos))
    var selectedDate = ObservableField<Date>()
    var totalPages = 1

    fun clearDateFilter(){
        selectedDate.set(null)
    }

    private val searchRunnable = Runnable {
        //initiateSearchQuery()
    }

    /*
    * attached directly with layout via binding
    * waits for 400 millis before executing search api
    * cancels previous pending api calls if new search key appears
    * */
    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (lastSearchKeyword.get()?.isEmpty() == true && s.isEmpty()) return
        searchHandler.removeCallbacksAndMessages(null)
        lastSearchKeyword.set(s.toString())
        searchHandler.postDelayed(searchRunnable,400)
    }

    /*
    * calls api to get document list based on text search or date search
    * */
    private fun initiateSearchQuery(){
      //  val request = RequestGetDocumentList(1,lastSearchKeyword.get()!!, 10, selectedDate.get()?.format(Constant.API_DATE_FORMAT_3)?:"")
       /// callGetDocumentListApi(request, responseDocumentList_NEW)
    }

    /*
    * calls api with same params as last , but with next page query
    * result should be added in adapter
    * */
  /*  fun loadNextPage(){
        lastRequest.pageNo ++
        callGetPopularUsersApi(lastRequest, responseDocumentList_PAGING)
    }*/

    fun callGetPopularUsersApi(request : RequestGetPopularUsers?, responseHolder : MutableLiveData<MasterResponse<List<UserPostWrapper>>>){
        var newRequest = request
        if (newRequest == null){
            newRequest = RequestGetPopularUsers()
        }
        lastRequest = newRequest
        baseRepo.restClient.callApi(ApiRegister.GETFOLLOWPOPULARUSERSSCREENDATA, newRequest, responseHolder)
    }

    fun updateError(clearText : Boolean){
        if (clearText){
            textNoData.set("")
        }else{
            textNoData.set("No Documents found")
        }

    }

    fun callFollowUnFollowUserApi(otherUserId : Int, followUser : Boolean){
        val request = RequestFollowUnfollowUser(otherUserId,followUser)

        request.followerUserId= Prefs.init().loginData!!.userId
        request.userId= otherUserId


        baseRepo.restClient.callApi(ApiRegister.FOLLOWUNFOLLOWUSER, request, responseFollowUnfollowUser)
    }
}
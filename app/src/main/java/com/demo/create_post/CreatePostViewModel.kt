package com.demo.create_post

import android.location.Location
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.demo.R
import com.demo.model.request.RequestGetCategory
import com.demo.model.request.RequestSavePost
import com.demo.model.response.*
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.Category
import com.demo.model.response.baseResponse.HashTag
import com.demo.model.response.baseResponse.QueryParams
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.post.PostRepository
import com.demo.util.Validator
import java.io.File

class CreatePostViewModel(
//    controller: AsyncViewController,
    private val postRepository: PostRepository,
    private val resources: ResourcesProvider
) : ViewModel() {

    var searchQuery: String = ""
    var startingIndex: Int = 0
    var hashTags = mutableListOf<HashTag>()
    var searchTags = mutableListOf<HashTag>()
    var categories = mutableListOf<Category>()

    var description = ObservableField<String>()
    var selectedCategory = ObservableField<Category>()
    val languageId = ObservableField<String>()
    private val errTitle = ObservableField<String>()
    var isEditAllowed = MutableLiveData<Boolean>(true)

    //  var chipsData = arrayListOf<GetCategory>()
    val responseGetPostDetails = MutableLiveData<MasterResponse<ResponseGetPostDetails>>()

    var latestLocation: Location? = null
    val requestSavePost = ObservableField<RequestSavePost>()
    var params = QueryParams()

    val requestGetCategory = ObservableField<RequestGetCategory>()
    val responseGetCategory = MutableLiveData<MasterResponse<List<GetCategory>>>()

    val responseSavePost = MutableLiveData<MasterResponse<ResponseSavePost>>()
    var uploadingImageUrl = ""

    init {
        description.set("")
        requestSavePost.set(RequestSavePost())
        params.offset = 0
        params.limit = 100
    }


    fun isSavePostValid(): Boolean {
        /* if (!Validator.isSavePostTitleValid(description.value!!.trim(), errTitle)) {
             MasterResponse<ResponseLogin>()
             return false
         }
 */
        if (!Validator.isSavePostCategoryValid(
                if (selectedCategory.get() != null) selectedCategory.get()!!.id else "",
                errTitle
            )
        ) {
            MasterResponse<ResponseLogin>()
            return false
        }
        return true
    }

    fun getSearchQuery(s: String): String {
        var query = ""
        if (s.contains("#")) {
            val lastHash = s.lastIndexOf('#')
            if (s.lastIndexOf(' ') < lastHash) {
                val endIndex = if (lastHash == s.length - 1) lastHash else s.length
                query = "#${s.substring(lastHash, endIndex)}"
            }/*else {
                Log.e("startIndex",start.toString())
                if(start<= s.length){
                    var a=s.substring(0,start)
                    Log.e("***((","@@@$a")
                    var substring =a.substring(a.lastIndexOf("#"))
                    Log.e("))))","%%%${substring}")

                    if(substring.startsWith("#") && !substring.contains("\n")){
                        query=substring
                    }
                }

            }*/


        }
//        println("startingIndex = ${startingIndex}")
//        if (s.contains("#")) {
//            var endIndex = s.nextIndexOf(startingIndex, " ")
//            println("endIndex1 = ${endIndex}")
//            endIndex = if (endIndex == startingIndex) s.length else endIndex
//            println("endIndex2 = ${endIndex}")
//
//            if (endIndex < startingIndex) {
//                query = "#${s.substring(startingIndex, endIndex)}"
//            }
//        }

        return query
    }

    suspend fun getCategories(): LiveData<BaseResponse> {
        params.offset = categories.size
        return postRepository.getCategories(params)
    }

    suspend fun getHashTags(): LiveData<BaseResponse> {
        params.offset = hashTags.size
        return postRepository.getHashTags(params)
    }

    suspend fun searchHashTag(): LiveData<BaseResponse> {
        params.startsWith = searchQuery
        return postRepository.getHashTags(params)
    }

    suspend fun createPost(): LiveData<BaseResponse> {
        return postRepository.createPost(requestSavePost.get()!!)

    }

    fun clearSearch() {
        searchQuery = ""
        searchTags.clear()
    }

    fun sortCategories() {
        categories.sortByDescending { it.postAssociated?.size }
    }

    fun deleteSampleVideo(path: String) {
        val file = File(path, resources.getString(R.string.sample_video))
        if (file.exists()) {
            if (file.delete()) {
            }
        }
    }
//    fun hashTagAlreadyExistWhenSelect(tag: String): Boolean {
//        if (!TextUtils.isEmpty(description.get()!!.trim())) {
//            val str = description.get()!!.trim()
//            val regexPattern = "(#\\w+)";
//            val p = Pattern.compile(regexPattern);
//            val m = p.matcher(str);
//            while (m.find()) {
//                val ht = m.group(1)
//                if (ht == "#$tag") {
//                    return true
//                }
//
//            }
//        }
//        return false
//    }

//    fun hashTagAlreadyExistWhenType(tag: String): Boolean {
//        if (!TextUtils.isEmpty(description.get()!!.trim())) {
//            val savedTags = ArrayList<String>()
//            val str = description.get()!!.trim()
//            val regexPattern = "(#\\w+)"
//            val p = Pattern.compile(regexPattern)
//            val m = p.matcher(str)
//            while (m.find()) {
//                val ht = m.group(1)
//                savedTags.add(ht)
//            }
//            if (savedTags.size > 0) savedTags.removeAt(savedTags.size - 1)
//            if (savedTags.contains("#$tag")) {
//                return true
//            }
//        }
//        return false
//    }

//    fun sortHashTags() {
//        hashTags.sortByDescending { it.postAssociated?.size }
//    }

//    fun sortSearchTags() {
//        searchTags.sortByDescending { it.postAssociated?.size }
//    }
}

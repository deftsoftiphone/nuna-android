package com.demo.model.response

import com.demo.dashboard_search.categoryFilter.CategoryFilterStatus
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetCategory(
    @field:SerializedName("categoryNameMarathi")
    var categoryNameMarathi: String? = null,

    @field:SerializedName("categoryNameGujarati")
    var categoryNameGujarati: String? = null,

    @field:SerializedName("description")
    var description: String? = null,

    @field:SerializedName("active")
    var active: Boolean? = null,

    @field:SerializedName("categoryNameHindi")
    var categoryNameHindi: String? = null,

    @field:SerializedName("categoryName")
    var categoryName: String? = null,

    @field:SerializedName("myFavorite")
    var myFavorite: Boolean? = null,

    @field:SerializedName("categoryNameTamil")
    var categoryNameTamil: String? = null,

    @field:SerializedName("hashTags")
    var hashTags: Any? = null,

    @field:SerializedName("createdDate")
    var createdDate: Any? = null,

    @field:SerializedName("deleted")
    var deleted: Boolean? = null,

    @field:SerializedName("userCount")
    var userCount: Int? = null,

    @field:SerializedName("categoryNameKannada")
    var categoryNameKannada: String? = null,

    @field:SerializedName("imageUrl")
    var imageUrl: String? = null,

    @field:SerializedName("categoryNameBengali")
    var categoryNameBengali: String? = null,

    @field:SerializedName("categoryId")
    var categoryId: Int? = 0,

    @field:SerializedName("categoryNameTelugu")
    var categoryNameTelugu: String? = ""


) : Serializable {

    var filterStatus = CategoryFilterStatus.UNSELECTED
}
package com.demo.notifications

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import com.demo.R
import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.SuggestionItemBinding
import com.demo.providers.resources.ResourcesProvider

class NotificationsSuggestionsAdapter(
    override val layoutId: Int,
    private val context: Context,
    val resourcesProvider: ResourcesProvider
) :
    BaseRecyclerAdapter<SuggestionItemBinding, com.demo.model.response.baseResponse.Activity>() {
    var click: OnItemClickPosts? = null
    var face1: Typeface? = null
    var face2: Typeface? = null


    fun updateTypeface(face1: Typeface?, face2: Typeface?) {
        face1?.let { this.face1 = it }
        face2?.let { this.face2 = it }
    }

    override fun bind(
        holder: ViewHolder,
        item: com.demo.model.response.baseResponse.Activity,
        position: Int
    ) {

        holder.binding.apply {
            suggestion = item
            btnFollow.isSelected = true
            userName.text = "@${item.userName}"
            followers.text = "${item.noOfFollowers} ${context.getString(R.string.followers)}"
            postsCount.text = "${item.noOfPostsCreated} ${context.getString(R.string.posts)}"

            btnFollow.apply {
                if (item.followedByMe == true) {
                    isSelected = false
                    text = context.getString(R.string.following)
                    typeface = face1
                } else {
                    isSelected = true
                    text = context.getString(R.string.follow)
                    typeface = face2
                }
            }


            btnFollow.setOnClickListener {
                click?.onFollowClick(position, item.id, item.followedByMe!!)
            }

            clUser.setOnClickListener {
                click?.openScreen(
                    item.id!!,
                    null
                )
            }

        }






    }

    interface OnItemClickPosts {
        fun onFollowClick(position: Int, id: String?, isFollowed: Boolean)
        fun openScreen(id: String, categoryId: String?)
    }
}
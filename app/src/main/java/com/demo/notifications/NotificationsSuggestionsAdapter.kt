package com.demo.notifications

import android.graphics.Typeface
import com.demo.R
import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.SuggestionItemBinding
import com.demo.model.response.baseResponse.Activity
import com.demo.model.response.baseResponse.PostAssociated
import com.demo.providers.resources.ResourcesProvider

class NotificationsSuggestionsAdapter(
    override val layoutId: Int,
    val resources: ResourcesProvider,
    vararg typefaces: Typeface
) :
    BaseRecyclerAdapter<SuggestionItemBinding, Activity>() {
    private val face1 = typefaces[0]
    private val face2 = typefaces[1]
    var click: OnItemClickPosts? = null
    var title: String = ""

    override fun bind(
        holder: ViewHolder,
        item: Activity,
        position: Int
    ) {

        holder.binding.apply {
            suggestion = item
//            btnFollow.isSelected = true
//            userName.text = "@${item.userName}"
//            followers.text = "${item.noOfFollowers} ${resources.getString(R.string.followersText)}"
//            postsCount.text = "${item.noOfPostsCreated} ${resources.getString(R.string.postsText)}"

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
                    null,
                    null,
                    position
                )
            }
        }
    }

    interface OnItemClickPosts {
        fun onFollowClick(position: Int, id: String?, isFollowed: Boolean)
        fun openScreen(id: String, categoryId: String?, postId: String?, position: Int?)
        fun openPost(id: String, post: PostAssociated, position: Int)
        fun readAdminNotification(id: String, position: Int)
    }
}
package com.demo.notifications

import android.app.Activity
import android.content.Context
import android.util.Log
import com.demo.R
import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.NotificationsItemBinding

class NotificationsAllActivityAdapter(override val layoutId: Int, val type: String,val context: Context) :
    BaseRecyclerAdapter<NotificationsItemBinding, com.demo.model.response.baseResponse.Activity>() {
    var click: NotificationsSuggestionsAdapter.OnItemClickPosts? = null

    override fun bind(
        holder: ViewHolder,
        item: com.demo.model.response.baseResponse.Activity,
        position: Int
    ) {

        holder.binding.apply {
            userDetails = item.user
            activity = item
            if (type == context.getString(R.string.like)) {
                notificationMessage.text= context.getString(R.string.liked_post)

            } else if (type == context.getString(R.string.comments)) {
                notificationMessage.text=context.getString(R.string.comment_on_post)


            } else if (type== context.getString(R.string.followers)) {
                notificationMessage.text=context.getString(R.string.follow_you)

            }else{
                notificationMessage.text=context.getString(R.string.share_post)

            }


            userImage.setOnClickListener {
                click?.openScreen(
                    item.id!!,
                    null
                )
            }

            userName.setOnClickListener { userImage.performClick() }

        }


    }


}
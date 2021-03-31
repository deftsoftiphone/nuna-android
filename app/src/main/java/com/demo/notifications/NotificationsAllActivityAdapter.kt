package com.demo.notifications

import android.graphics.Typeface
import androidx.core.app.NotificationManagerCompat
import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.NotificationsItemBinding
import com.demo.model.response.baseResponse.Activity
import com.demo.model.response.baseResponse.FCMNotification
import com.demo.util.ClickGuard
import com.google.gson.Gson


class NotificationsAllActivityAdapter(
    override val layoutId: Int,
    var message: String
) :
    BaseRecyclerAdapter<NotificationsItemBinding, Activity>() {
    var click: NotificationsSuggestionsAdapter.OnItemClickPosts? = null
    var face1: Typeface? = null
    var face2: Typeface? = null

    fun updateTypeface(face1: Typeface?, face2: Typeface?) {
        face1?.let { this.face1 = it }
        face2?.let { this.face2 = it }
    }

    fun updateMessage(msg: String) {
        this.message = msg
    }

//    override fun onViewRecycled(holder: ViewHolder) {
//        super.onViewRecycled(holder)
//        Glide.get(holder.binding.root.context).clearMemory()
//    }

    override fun bind(
        holder: ViewHolder,
        item: Activity,
        position: Int
    ) {

        holder.binding.apply {
            userDetails = item.user
            activity = item
            try {
                item.notificationDescription =
                    Gson().fromJson(item.description, FCMNotification::class.java)
                notificationMessage.text = item.notificationDescription!!.description
            } catch (e: Exception) {
                notificationMessage.text = item.description
            }

            holder.binding.root.setOnClickListener {
                item.notificationId?.let { id ->
                    NotificationManagerCompat.from(it.context).cancel(id)
                }
                when {
                    item.post != null -> click?.openPost(item.id!!, item.post!!, position)
                    item.user != null -> click?.openScreen(
                        item.user?.id!!, null, item.id, position
                    )
                    else -> click?.readAdminNotification(item.id!!, position)
                }
            }

            ClickGuard.guard(holder.binding.root)
        }
    }
}
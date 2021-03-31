package com.demo.profile

import android.view.ViewGroup
import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.DashboardScreenItemBinding
import com.demo.model.response.baseResponse.PostAssociated
import com.demo.util.ClickGuard


class CommonPostsAdapter(override val layoutId: Int) :
    BaseRecyclerAdapter<DashboardScreenItemBinding, PostAssociated>() {

    var click: OnItemClickPosts? = null

    fun getItem(position: Int): PostAssociated {
        return list[position]
    }

    override fun bind(holder: ViewHolder, item: PostAssociated, position: Int) {
        holder.binding.post = item
        holder.binding.createdBy = item.createdBy
        for (media in item.medias!!) {
            if (media.isImage!!) {
                holder.binding.subPost = media
                break
            }
        }
        val marginParams: ViewGroup.MarginLayoutParams =
            holder.binding.mainPost.layoutParams as ViewGroup.MarginLayoutParams
        if (position % 2 == 0) {
            marginParams.setMargins(0, 0, 1, 0)
        } else marginParams.setMargins(1, 0, 0, 0)


        holder.binding?.let {
            it.userImage.setOnClickListener {
                holder.binding.createdBy?.id?.let { it1 -> click?.onUserClick(position, it1) }
            }

            it.userName.setOnClickListener {
                holder.binding.createdBy?.let {
                    click?.onUserClick(position, it.id!!)
                }
            }
            it.image.setOnClickListener { click?.onPostClick(position, item.id!!) }
            try {
                ClickGuard.guard(it.userImage, it.userName, it.image)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    interface OnItemClickPosts {
        fun onPostClick(position: Int, id: String)
        fun onUserClick(position: Int, id: String)
    }
}


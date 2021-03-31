package com.demo.search.adapters

import android.view.ViewGroup
import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.LayoutSearchVideoItemBinding
import com.demo.model.response.baseResponse.PostAssociated
import com.demo.search.SearchFragment


class VideosPostsAdapter(override val layoutId: Int) :
    BaseRecyclerAdapter<LayoutSearchVideoItemBinding, PostAssociated>() {

    var click: SearchFragment.ClickHandler? = null

    fun getItem(position: Int): PostAssociated {
        return list[position]
    }

    override fun bind(holder: ViewHolder, item: PostAssociated, position: Int) {
        holder.binding.post = item
        if (item.medias!!.isNotEmpty()) {
            holder.binding.createdBy = item.createdBy
        }
        for (media in item.medias!!) {
            if (!media.isVideo!!) {
                holder.binding.subPost = media
                break
            }
        }
        val marginParams: ViewGroup.MarginLayoutParams =
            holder.binding.mainPost.layoutParams as ViewGroup.MarginLayoutParams
        if (position % 2 == 0) {
            marginParams.setMargins(0, 0, 1, 0)
        } else marginParams.setMargins(1, 0, 0, 0)


        holder.binding.apply {

            image.setOnClickListener {
                click?.onPostClick(position, item.id!!)
            }

            userImage.setOnClickListener {
                createdBy!!.id?.let { it1 -> click?.onUserClick(position, it1) }
            }

            userName.setOnClickListener {
                userImage.performClick()
            }
        }
    }
}
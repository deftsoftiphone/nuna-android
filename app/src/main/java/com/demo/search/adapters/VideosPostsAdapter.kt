package com.demo.search.adapters

import android.view.ViewGroup
import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.LayoutSearchVideoItemBinding
import com.demo.model.response.baseResponse.VideoPostsItem
import com.demo.search.SearchFragment


class VideosPostsAdapter(override val layoutId: Int) :
    BaseRecyclerAdapter<LayoutSearchVideoItemBinding, VideoPostsItem>() {

    var click: SearchFragment.ClickHandler? = null

    fun getItem(position: Int): VideoPostsItem {
        return list[position]
    }

    override fun bind(holder: ViewHolder, item: VideoPostsItem, position: Int) {
        holder.binding.post = item
        if (item.medias!!.isNotEmpty()) {
            holder.binding.createdBy = item.createdBy!![0]
        }
        val marginParams: ViewGroup.MarginLayoutParams =
            holder.binding.image.layoutParams as ViewGroup.MarginLayoutParams
        if (position % 2 == 0) {
            marginParams.setMargins(0, 0, 1, 0)
        } else marginParams.setMargins(1, 0, 0, 0)


        holder.binding.userImage.setOnClickListener {
            click?.onPostClick(position, holder.binding.createdBy!!.id)
        }
        holder.binding.userName.setOnClickListener {
            holder.binding.createdBy?.let {
                click?.onPostClick(position, it.id)
            }
        }

    }
}
package com.demo.create_post

import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.LayoutCreatePostHashtagBinding
import com.demo.model.response.baseResponse.HashTag
import java.util.*

class CreatePostHashTagRecyclerAdapter(
    override val layoutId: Int,
    private val clickListener: CreatePostRecyclerItemClickListener
) :
    BaseRecyclerAdapter<LayoutCreatePostHashtagBinding, HashTag>() {
    override fun bind(holder: ViewHolder, item: HashTag, position: Int) {
        holder.binding.hashtag = item

        holder.binding.root.setOnClickListener {
            item.tagName?.let {
                clickListener.selectedHashTag(it.toLowerCase(Locale.ROOT))
            }
        }
    }
}
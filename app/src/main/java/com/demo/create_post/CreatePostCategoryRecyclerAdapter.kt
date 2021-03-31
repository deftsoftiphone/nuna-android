package com.demo.create_post

import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.LayoutCreatePostCategoryBinding
import com.demo.model.response.baseResponse.Category
import com.demo.model.response.baseResponse.PostCategory

class CreatePostCategoryRecyclerAdapter(
    override val layoutId: Int,
    private val clickListener: CreatePostRecyclerItemClickListener
) :
    BaseRecyclerAdapter<LayoutCreatePostCategoryBinding, PostCategory>() {
    override fun bind(holder: ViewHolder, item: PostCategory, position: Int) {
        holder.binding.category = item

        holder.binding.root.setOnClickListener {
            clickListener.selectedCategory(item)
        }
    }
}
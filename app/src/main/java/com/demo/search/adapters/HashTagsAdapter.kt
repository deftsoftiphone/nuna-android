package com.demo.search.adapters

import android.graphics.Typeface
import com.demo.R
import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.SearchHashtagItemBinding
import com.demo.model.response.baseResponse.HashTag
import com.demo.search.clickListeners.FollowClickListener

class HashTagsAdapter(
    override val layoutId: Int,
    private val click: FollowClickListener,
    vararg typefaces: Typeface
) :
    BaseRecyclerAdapter<SearchHashtagItemBinding, HashTag>() {
    private val face1 = typefaces[0]
    private val face2 = typefaces[1]

    override fun bind(
        holder: ViewHolder,
        item: HashTag,
        position: Int
    ) {
        holder.binding.hashTag = item
        holder.binding.btnFollow.apply {
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

        holder.binding.apply {
            clTag.setOnClickListener {
                click.openScreen(
                    item.id!!,
                    item.category?.get(0)?.id!!
                )
            }

            btnFollow.setOnClickListener {
                click.onFollowClick(position, item.id, item.followedByMe!!)
            }
        }
    }
}
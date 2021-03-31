package com.demo.hashtag_tab

import android.text.TextUtils
import android.view.View
import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.HashtagPostItemBinding
import com.demo.model.response.baseResponse.PostAssociated
import com.demo.util.ClickGuard
import kotlinx.android.synthetic.main.hashtag_post_item.view.*


class HashTagTabPostItemAdapter(
    override val layoutId: Int,
    private val clickListener: HashTagPostClickListener
) :
    BaseRecyclerAdapter<HashtagPostItemBinding, PostAssociated>() {

    fun getItem(position: Int): PostAssociated {
        return list[position]
    }

    fun updatePost(post: PostAssociated, position: Int) {
        list[position] = post
        notifyItemChanged(position)
    }

    override fun bind(holder: ViewHolder, item: PostAssociated, position: Int) {
        if (position == list.size - 1 && list.size > 4) {
            holder.binding.root.ivPost.visibility = View.GONE
            holder.binding.root.clSeeMore.visibility = View.VISIBLE
        }

        item.medias?.let {
            if (item.medias?.isNotEmpty()!!) {
                for (media in item.medias!!) {
                    if (media.isImage!!) {
                        holder.binding.subPost = media
                        break
                    }
                }
            }
        }


        holder.binding.root.setOnClickListener {
            if (TextUtils.isEmpty(item.id))
                clickListener.hashTagClicked("", item, position)
            else clickListener.postClicked("", list, position)
        }

        ClickGuard.guard(holder.binding.root)
    }
}
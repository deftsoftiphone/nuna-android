package com.demo.profile

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.ProfilePostItemBinding
import com.demo.model.response.baseResponse.Post


class ProfilePostsAdapter(override val layoutId: Int) :
    BaseRecyclerAdapter<ProfilePostItemBinding, Post>() {


    fun getItem(position: Int): Post {
        return list[position]
    }

    override fun bind(holder: ViewHolder, item: Post, position: Int) {

        holder.binding.post = item
        holder.binding.createdBy = item.createdBy

        if (item.medias!!.isNotEmpty()) {
            for (media in item.medias){
                if(media.isImage){
                    holder.binding.subPost=media
                    break
                }
            }
        }

        val marginParams: ViewGroup.MarginLayoutParams =
            holder.binding.image.layoutParams as ViewGroup.MarginLayoutParams
        if (position % 2 == 0) marginParams.setMargins(0, 0, 1, 0)
        else marginParams.setMargins(1, 0, 0, 0)

    }

}
package com.demo.profile

import android.view.ViewGroup
import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.DashboardScreenItemBinding
import com.demo.model.response.baseResponse.PostAssociated


class CommonPostsAdapter(override val layoutId: Int) :
    BaseRecyclerAdapter<DashboardScreenItemBinding, PostAssociated>() {

    var click: OnItemClickPosts? = null

    fun getItem(position: Int): PostAssociated {
        return list[position]
    }

    override fun bind(holder: ViewHolder, item: PostAssociated, position: Int) {
        holder.binding.post = item
        if (item.medias!!.isNotEmpty()) {
            holder.binding.createdBy = item.medias[0].createdBy
        }

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
        if (position % 2 == 0) {
            marginParams.setMargins(0, 0, 1, 0)
        } else marginParams.setMargins(1, 0, 0, 0)


        holder.binding?.let {
            it.userImage.setOnClickListener {
                holder.binding.createdBy?.id?.let { it1 -> click?.onPostClick(position, it1) }
            }

            it.userName.setOnClickListener {
                holder.binding.createdBy?.let {
                    click?.onPostClick(position, it.id)
                }
            }
        }
    }


    interface OnItemClickPosts {
        fun onPostClick(position: Int, id: String)
    }
}


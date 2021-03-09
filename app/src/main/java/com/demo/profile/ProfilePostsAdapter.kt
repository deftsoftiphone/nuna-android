package com.demo.profile

import android.view.View.*
import android.view.ViewGroup
import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.ProfilePostItemBinding
import com.demo.model.response.baseResponse.PostAssociated
import com.demo.util.ClickGuard
import com.demo.util.Prefs
import com.demo.view_others_profile.OthersProfileFragment
import com.demo.view_others_profile.ProfileClickListener


class ProfilePostsAdapter(override val layoutId: Int) :
    BaseRecyclerAdapter<ProfilePostItemBinding, PostAssociated>() {
    private val currentUser = Prefs.init().currentUser
    private var clickHandler: ProfileClickListener? = null
    private var selectedIndex = -1

    fun setupClickHandler(clickHandler: NewProfileFragment.ClickHandler) {
        this.clickHandler = clickHandler
    }

    fun setupClickHandler(clickHandler: OthersProfileFragment.ClickHandler) {
        this.clickHandler = clickHandler
    }

    fun getItem(position: Int): PostAssociated {
        return list[position]
    }

    override fun bind(holder: ViewHolder, item: PostAssociated, position: Int) {
        holder.binding.apply {
            post = item
            createdBy = item.createdBy
            showDropDown = false
            if (item.medias!!.isNotEmpty()) {
                for (media in item.medias!!) {
                    if (media.isImage!!) {
                        holder.binding.subPost = media
                        break
                    }
                }
            }

            if (item.createdBy?.id == Prefs.init().currentUser?.id) {
                ivPostViews.visibility = VISIBLE
                userLikesImage.visibility = INVISIBLE
                userLikesCount.text = "${item.noOfViews}"
            } else {
                ivPostViews.visibility = GONE
                userLikesImage.visibility = VISIBLE
                userLikesCount.text = "${item.noOfLikes}"
            }
        }


        val marginParams: ViewGroup.MarginLayoutParams =
            holder.binding.mainPost.layoutParams as ViewGroup.MarginLayoutParams
        if (position % 2 == 0) marginParams.setMargins(0, 0, 1, 0)
        else marginParams.setMargins(1, 0, 0, 0)

        holder.binding.apply {

            image.setOnClickListener {
                clickHandler?.showPost(position)
            }

            userImage.setOnClickListener {
                holder.binding.createdBy?.id?.let { it1 ->
                    clickHandler?.onUserClick(position, it1)
                }
            }

            userName.setOnClickListener {
                holder.binding.createdBy?.let {
                    clickHandler?.onUserClick(position, it.id!!)
                }
            }

            ClickGuard.guard(userName)
            ClickGuard.guard(userImage)
            ClickGuard.guard(image)
        }
    }
}
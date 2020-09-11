package com.demo.search.adapters

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import com.demo.R
import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.SearchUsersItemBinding
import com.demo.model.response.baseResponse.Activity
import com.demo.search.clickListeners.FollowClickListener
import com.demo.util.Prefs

class UsersRecyclerAdapter(
    override val layoutId: Int,
    private val click: FollowClickListener,
    vararg typefaces: Typeface
) :
    BaseRecyclerAdapter<SearchUsersItemBinding, Activity>() {
    private val face1 = typefaces[0]
    private val face2 = typefaces[1]

    @SuppressLint("SetTextI18n")
    override fun bind(
        holder: ViewHolder,
        item: Activity,
        position: Int
    ) {
        holder.binding.user = item

        if(Prefs.init().profileData?.id==item.id){
            holder.binding.btnFollow.visibility=GONE
        }else{
            holder.binding.btnFollow.visibility= VISIBLE

        }
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
            btnFollow.setOnClickListener {
                click.onFollowClick(position, item.id, item.followedByMe!!)
            }

            clUser.setOnClickListener {
                click.openScreen(
                    item.id!!,
                    null
                )
            }

        }

    }

}
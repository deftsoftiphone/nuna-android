package com.demo.user_profile_details

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.View.GONE
import android.view.View.VISIBLE
import com.demo.R
import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.FollowersUsersItemBinding
import com.demo.model.response.baseResponse.Activity
import com.demo.search.clickListeners.FollowClickListener
import com.demo.util.ClickGuard
import com.demo.util.Prefs
import com.demo.util.runWithDelay

class FollowersUsersRecyclerAdapter(
    override val layoutId: Int,
    private val click: FollowClickListener,
    vararg typefaces: Typeface
) :
    BaseRecyclerAdapter<FollowersUsersItemBinding, Activity>() {
    private val face1 = typefaces[0]
    private val face2 = typefaces[1]

    @SuppressLint("SetTextI18n")
    override fun bind(
        holder: ViewHolder,
        item: Activity,
        position: Int
    ) {
        holder.binding.user = item
        if (Prefs.init().profileData?.id == item.id) {
            holder.binding.btnFollow.visibility = GONE
        } else {
            holder.binding.btnFollow.visibility = VISIBLE

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
                btnFollow.isEnabled = false
                runWithDelay(1000)
                {
                    btnFollow.isEnabled = true
                }
            }

            clUser.setOnClickListener {
                click.openScreen(
                    item.id!!,
                    null
                )
            }

            ClickGuard.guard(btnFollow, clUser)

        }

    }

}
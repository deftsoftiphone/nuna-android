package com.demo.followPopularUsers

import android.app.Activity
import com.demo.R
import com.demo.adapter.MiniPostAdapter
import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.LayoutPopularUserBinding
import com.demo.model.UserPostWrapper
import com.demo.model.request.RequestFollowUnfollowUser
import com.demo.model.response.Post

class FollowPopularUsersAdapter(
    override val layoutId: Int,
    val mClickHandler: FollowPopularUsersFragment.ClickHandler
) : BaseRecyclerAdapter<LayoutPopularUserBinding, UserPostWrapper>() {

    lateinit var context: Activity


    override fun bind(holder: ViewHolder, item: UserPostWrapper, position: Int) {
        val newItem = list[position]
        holder.binding.cardUser.setOnClickListener {
            val exp = holder.binding.expandableLayout
            if (exp.isExpanded) {
                exp.collapse()
            } else {
                exp.expand()
            }
        }
        with(holder.binding) {
            this.item = newItem
            setupRecycler(holder.binding, newItem.postList)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        val newItem = list[position]
        if (payloads.isNotEmpty() && payloads[0] == 1) {
            with(holder.binding) {
                this.item = newItem
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    fun updateRow(data: RequestFollowUnfollowUser) {
        val oldItemIndex = list.indexOfFirst { it.user.userId == data.userId }
        if (oldItemIndex != -1) {
            val oldItem = list[oldItemIndex]
            oldItem.user.apply {
                followStatus = if (data.follow) FollowStatus.FOLLOWED else FollowStatus.UNFOLLOWED
            }
        }
        notifyItemChanged(oldItemIndex, listOf(1))
    }

    private fun setupRecycler(binding: LayoutPopularUserBinding, posts: List<Post>) {
        val adapter: MiniPostAdapter?
        var firstBind = false
        if (binding.recyclerPosts.adapter == null) {
            adapter = MiniPostAdapter(R.layout.layout_post_mini_item)
            firstBind = true
        } else {
            adapter = binding.recyclerPosts.adapter as? MiniPostAdapter
        }

        adapter.apply {
            //context = activity as Activity


        }
        if (adapter == null) return
        if (posts.isEmpty()) {
            adapter.clearData()
        } else {
            adapter.addClickEventWithView(R.id.card_parent, mClickHandler::onClickUserPost)
            adapter.setNewItems(posts)
            if (firstBind) binding.recyclerPosts.adapter = adapter
        }
    }
}

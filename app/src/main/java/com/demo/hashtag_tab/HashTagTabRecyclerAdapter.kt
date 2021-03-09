package com.demo.hashtag_tab

import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.R
import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.HashtagPostsBinding
import com.demo.model.response.baseResponse.HashTag
import com.demo.model.response.baseResponse.PostAssociated
import com.demo.util.Prefs


class HashTagTabRecyclerAdapter(
    override val layoutId: Int,
    private val clickListener: HashTagPostClickListener
) :
    BaseRecyclerAdapter<HashtagPostsBinding, HashTag>() {
    //    lateinit var context: Activity

    override fun bind(holder: ViewHolder, item: HashTag, position: Int) {
        holder.binding.apply {
            clHeader.setOnClickListener {
                clickListener.hashTagClicked(
                    item.id!!,
                    PostAssociated(),
                    position
                )
            }
            val width = Prefs.init().deviceDisplayWidth.toFloat() / 3.0
            val height =
                (Prefs.init().deviceDisplayWidth.toFloat() / 3.0 + Prefs.init().deviceDisplayWidth.toFloat())

            hImage.maxHeight = height.toInt()
            hImage.maxWidth = width.toInt()
            hashTag = item

            hashtagPosts.layoutManager =
                LinearLayoutManager(
                    holder.binding.root.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )

            val adapter = setupPostAdapter(item)
            item.postAssociated.let {
                if (it?.isNotEmpty()!!) {
                    val temp = ArrayList<PostAssociated>().apply {
                        addAll(it)
                        if (it.size > 4)
                            add(PostAssociated())
                    }
                    adapter.setNewItems(temp)
                }
            }
            hashtagPosts.adapter = adapter
//            (hashtagPosts.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }

    private fun setupPostAdapter(item: HashTag): HashTagTabPostItemAdapter {
//        if (!::hashTagTabPostItemAdapter.isInitialized)
        return HashTagTabPostItemAdapter(
            R.layout.hashtag_post_item,
            object : HashTagPostClickListener {
                override fun hashTagClicked(
                    hashTagId: String,
                    post: PostAssociated,
                    position: Int
                ) {
                    clickListener.hashTagClicked(item.id!!, post, position)
                }

                override fun postClicked(
                    hashTagId: String,
                    posts: ArrayList<PostAssociated>,
                    position: Int
                ) {
                    clickListener.postClicked(item.id!!, posts, position)
                }
            })
    }

    override fun getItemCount(): Int {
        return list.size
    }

}

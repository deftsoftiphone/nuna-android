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
    private lateinit var hashTagTabPostItemAdapter: HashTagTabPostItemAdapter

    fun updatePost(post: HashTag, position: Int) {
        list[position] = post
        notifyItemChanged(position)
    }

    fun updateHashTags(hashTags: ArrayList<HashTag>) {
        list.clear()
        list.addAll(hashTags)
        notifyDataSetChanged()
    }

    override fun bind(holder: ViewHolder, item: HashTag, position: Int) {
        holder.binding.apply {
            clHeader.setOnClickListener {
                clickListener.postClicked(
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

//            if (item.postAssociated!!.isNotEmpty())
            hashTag = item

//            println("item = ${item.tagName} - Id - ${item.id}")
            hashtagPosts.layoutManager =
                LinearLayoutManager(
                    holder.binding.root.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )

            setupPostAdapter(item)
        }

        holder.binding.hashtagPosts.adapter = hashTagTabPostItemAdapter
        item.postAssociated.let {
            if (it?.isNotEmpty()!!) {
                val temp = ArrayList<PostAssociated>()
                temp.addAll(it)
                if (it.size > 4)
                    temp.add(PostAssociated())
                hashTagTabPostItemAdapter.addNewItems(temp)
            }
        }
    }

    private fun setupPostAdapter(item: HashTag) {
        hashTagTabPostItemAdapter =
            HashTagTabPostItemAdapter(
                R.layout.hashtag_post_item,
                object : HashTagPostClickListener {
                    override fun postClicked(
                        hashTagId: String,
                        post: PostAssociated,
                        position: Int
                    ) {
                        clickListener.postClicked(item.id!!, post, position)
                    }
                })
    }

    override fun getItemCount(): Int {
        return list.size
    }

}

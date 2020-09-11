package com.demo.boardList

import com.demo.R
import com.demo.adapter.MiniPostAdapter
import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.LayoutMyBoardItemBinding
import com.demo.model.Pinboard
import com.demo.model.response.Post

class MyBoardsAdapter(
        override val layoutId: Int,
        val mClickHandler: MyBoardsFragment.ClickHandler
) :
        BaseRecyclerAdapter<LayoutMyBoardItemBinding, Pinboard>() {

    override fun bind(holder: ViewHolder, item: Pinboard, position: Int) {
        with(holder.binding) {
            board = item
            if (item.postList != null && item.postList!!.isNotEmpty()) {
                setupRecycler(this, item.postList!!, item, position)
            }else{
                holder.binding.recyclerPosts.apply {
                    (adapter as? MiniPostAdapter)?.run {
                        list.clear()
                        this@apply.post{notifyDataSetChanged()}
                    }

                }
            }
        }
    }

    private fun setupRecycler(
            binding: LayoutMyBoardItemBinding,
            posts: List<Post>,
            pinboard: Pinboard,
            position: Int
    ) {

        listOf<String>().isNullOrEmpty()
        val adapter: MiniPostAdapter?
        var firstBind = false
        if (binding.recyclerPosts.adapter == null) {
            adapter = MiniPostAdapter(R.layout.layout_post_mini_item)
            adapter.showDelete = true
            firstBind = true
        } else {
            adapter = binding.recyclerPosts.adapter as? MiniPostAdapter
        }

        if (adapter == null) return

        if (posts.isEmpty()) {
            adapter.clearData()
        } else {
            //relatedPostsAdapter
            adapter.addClickEventWithView(R.id.card_parent, mClickHandler::onClickBoardPost)
            adapter.addClickEventWithView(R.id.iv_remove_post) { pos, clickedBoard ->
                mClickHandler.onClickDeleteBoardPost(pos, clickedBoard, position, pinboard)
            }

           // adapter.addClickEventWithView(R.id.iv_fav, mClickHandler::onFavClick)
            adapter.setNewItems(posts)
            if (firstBind) binding.recyclerPosts.adapter = adapter
        }
    }

    fun removePostFromPinboard(postId: Int, pinBoardId: Int) {
        list.find { pinBoardId == it.pinboardId }?.apply {
            postList = postList?.let {
                val index = it.indexOfFirst { postId == it.postId }
                val newData = ArrayList(it)
                newData.removeAt(index)
                newData
            }.also { notifyDataSetChanged() }
        }
    }

    fun updateBoardName(newName:String, boardId : Int){
        val index = list.indexOfFirst { boardId == it.pinboardId }
        if (index!=-1){
            list[index].pinboardName = newName
            notifyItemChanged(index)
        }
    }
}
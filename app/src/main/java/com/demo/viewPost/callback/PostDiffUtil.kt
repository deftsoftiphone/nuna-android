package com.demo.viewPost.callback

import androidx.recyclerview.widget.DiffUtil
import com.demo.model.response.baseResponse.PostAssociated

class PostDiffUtil(
    private var oldPostList: ArrayList<PostAssociated>,
    private var newPostList: ArrayList<PostAssociated>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldPostList.size
    }

    override fun getNewListSize(): Int {
        return newPostList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        println("Diffutil areItemsTheSame = ${oldPostList[oldItemPosition].id.equals(newPostList[newItemPosition].id)}")
        return oldPostList[oldItemPosition].id.equals(newPostList[newItemPosition].id)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        println("Diffutil areContentsTheSame = ${oldPostList[oldItemPosition].equals(newPostList[newItemPosition])}")
        return oldPostList[oldItemPosition].equals(newPostList[newItemPosition])
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        println("Diffutil getChangePayload = ${newPostList[newItemPosition]}")
        return newPostList[newItemPosition]
    }
}
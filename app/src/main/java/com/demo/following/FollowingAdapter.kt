package com.demo.following

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.databinding.LayoutItemDocumentLegacyBinding
import com.demo.model.response.Document

class FollowingAdapter(val mClickHandler: FollowingFragment.ClickHandler) :
    RecyclerView.Adapter<FollowingAdapter.ViewHolder>() {

    val list = ArrayList<Document>()

    fun updateDocument(updatedDoc: Document) {
        var pos = -1
        var counter = 0
        for (i in list) {
            if (updatedDoc.documentId == i.documentId) {
                pos = counter
                break
            }
            counter++
        }
        if (pos != -1) {
            list[pos] = updatedDoc
            notifyItemChanged(pos)
        }
    }

    fun setNewData(newItems: ArrayList<Document>) {
        list.clear()
        list.addAll(newItems)
        notifyDataSetChanged()
    }

    fun addNewData(newItems: ArrayList<Document>) {
        list.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemDocumentLegacyBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(list[position])
    }

    inner class ViewHolder(val binding: LayoutItemDocumentLegacyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Document) {
            binding.item = item
            binding.clickHandler = ClickHandler()
        }

        inner class ClickHandler {
            fun onClickRow() {
                mClickHandler.onClickDocument(list[adapterPosition])
            }
        }
    }
}
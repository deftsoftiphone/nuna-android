/*
package com.demo.create_post

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.R
import com.demo.databinding.LayoutCreatePostDataBinding
import com.demo.model.response.baseResponse.Category
import com.demo.model.response.baseResponse.HashTag
import com.demo.util.Prefs


class CreatePostRecyclerAdaptor(
    private var data: MutableList<Any>,
    private val clickListener: CreatePostRecyclerItemClickListener
) : RecyclerView.Adapter<CreatePostRecyclerAdaptor.ViewHolder>() {

    fun updateData(data: List<Any>) {
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun changeData(data: List<Any>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: LayoutCreatePostDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val context: Context = binding.root.context

        init {
            binding.root.setOnClickListener {
                val value = data[adapterPosition]
                if (value is Category) {
                    clickListener.selectedCategory(value)
                } else if (value is HashTag) {
                    clickListener.selectedHashTag(value.tagName!!)
                }
            }
        }

        fun bind(value: Any) {
            if (value is Category) {
                binding.name = fetchCurrentLanguageCategoryName(value)
                binding.postValue = value.noOfPostAssociated

            } else if (value is HashTag) {
                binding.name = "#${value.tagName}"
                binding.postValue = calculatePostsValue(value.postAssociated?.size)
            }
        }
    }

    fun fetchCurrentLanguageCategoryName(value: Category): String {
        return when (Prefs.init().selectedLang.languageIntId) {
            1 -> value.categoryNameHindi!!
            2 -> value.categoryName!!
            3 -> value.categoryNameMarathi!!
            4 -> value.categoryNameBengali!!
            5 -> value.categoryNameTamil!!
            6 -> value.categoryNameTelugu!!
            7 -> value.categoryNameGujrati!!
            8 -> value.categoryNameKannada!!
            else -> "Category"
        }
    }

    private fun calculatePostsValue(postsBased: Int?): String {
        var posts = "0 posts"
        if (postsBased != null) {
            posts = when {
                postsBased > 1000 -> "${postsBased / 1000}k posts"
                postsBased == 1 -> "$postsBased post"
                else -> "$postsBased posts"
            }
        }
        return posts
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(LayoutCreatePostDataBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == data.size && holder.context.resources.getInteger(R.integer.query_param_limit_value) <= data.size)
            clickListener.fetchData(data[data.size - 1])
        holder.bind(data[position])
    }

 */
/*   private fun findTag(
        title: String
    ): ArrayList<HashTag>? {
        var tags = ArrayList<HashTag>()
        (data as MutableList<HashTag>).forEach {
            if (it.tagName!!.contains(title)) {
                tags.add(it)
            }
        }
        return tags
    }*//*


}*/

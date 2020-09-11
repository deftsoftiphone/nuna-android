package com.demo.hashtag_tab

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.demo.create_post.CategoryClickedListener
import com.demo.databinding.LayoutHashtagCategoryRecyclerItemBinding
import com.demo.model.response.baseResponse.Category
import com.demo.util.fetchCurrentLanguageCategoryName
import com.demo.util.toArrayList

class CategoriesRecyclerAdapter(
    private var selectedPosition: MutableLiveData<Int>,
    private val clickedListener: CategoryClickedListener
) :
    RecyclerView.Adapter<CategoriesRecyclerAdapter.ViewHolder>() {
    //    private var current = 0
    private var categories = arrayListOf<Category>()

    inner class ViewHolder(val binding: LayoutHashtagCategoryRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.rbCategory.setOnClickListener {
                if (adapterPosition < categories.size && adapterPosition != selectedPosition.value && adapterPosition > -1) {
                    selectedPosition.postValue(adapterPosition)
                    clickedListener.selectedCategory(categories[adapterPosition])
                    notifyDataSetChanged()
                }
            }
        }

        fun bind(category: Category) {
            category.id?.let {
                category.categoryName = category.fetchCurrentLanguageCategoryName()
            }
            binding.category = category
        }
    }

    fun addCategories(categories: List<Category>) {
        val oldPos = this.categories.size + 1
        this.categories.addAll(categories)
        notifyItemRangeInserted(oldPos, categories.size)
    }

    fun updateCategories(categories: List<Category>) {
        this.categories.clear()
        this.categories.addAll(categories.toArrayList())
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(LayoutHashtagCategoryRecyclerItemBinding.inflate(inflater))
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            updateCheckBox(holder, position)
            holder.bind(categories[position])
    }

    private fun updateCheckBox(holder: ViewHolder, position: Int) {
        holder.binding.rbCategory.apply {
            if (selectedPosition.value == position) {
                isChecked = true
                isEnabled = false
                setTypeface(null, Typeface.BOLD);
            } else {
                isChecked = false
                isEnabled = true
                setTypeface(null, Typeface.NORMAL);
            }
        }
    }
}
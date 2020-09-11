package com.demo.category_select

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.databinding.LayoutItemCategoryBinding
import com.demo.model.response.GetCategory

class CategoryAdapter(
    private val items: List<GetCategory>,
    val mClickHandler: CategoryFragment.ClickHandler
) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    val selectedCategoriesIds = kotlin.collections.HashSet<Int>()
    val selectedCategoriesName = kotlin.collections.HashSet<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemCategoryBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(val binding: LayoutItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GetCategory) {
            binding.item = item
            binding.userCount.text = item.userCount.toString()
            binding.clickHandler = ClickHandler()

            if (selectedCategoriesIds.contains(item.categoryId)) {
                binding.selectionOverlay.visibility = View.VISIBLE
            } else {
                binding.selectionOverlay.visibility = View.GONE
            }
        }

        inner class ClickHandler {
            fun onClickRow() {
                val clickedCategoryId = binding.item!!.categoryId
                if (selectedCategoriesIds.contains(clickedCategoryId)) {
                    selectedCategoriesIds.remove(clickedCategoryId)
                    selectedCategoriesName.remove(binding.item!!.categoryName)
                } else {
                    if (clickedCategoryId != null) {
                        selectedCategoriesIds.add(clickedCategoryId)
                    }
                    selectedCategoriesName.add(binding.item!!.categoryName!!)
                }
                notifyItemChanged(adapterPosition)
            }
        }
    }
}
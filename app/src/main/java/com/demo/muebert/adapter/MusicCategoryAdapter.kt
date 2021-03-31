package com.demo.muebert.adapter

import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.MusicCategoriesRecyclerViewItemBinding
import com.demo.muebert.modal.CategoriesItem
import com.demo.muebert.viewmodel.AwesomeAudioContentViewModel

class MusicCategoryAdapter(
    override val layoutId: Int,
    private var mClickHandler: AwesomeAudioContentViewModel.ClickHandler
) : BaseRecyclerAdapter<MusicCategoriesRecyclerViewItemBinding, CategoriesItem>() {

    private var selectedIndex = -1

    override fun bind(holder: ViewHolder, item: CategoriesItem, position: Int) {
        holder.binding.apply {
            data = item
            root.setOnClickListener {
                mClickHandler.onSelectCategories(item, position)
            }
            ivCategory.isSelected = selectedIndex == position
        }
    }

    fun setItemSelected(position: Int) {
        selectedIndex = position
        notifyDataSetChanged()
    }

}
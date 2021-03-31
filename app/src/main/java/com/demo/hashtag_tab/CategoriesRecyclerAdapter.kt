package com.demo.hashtag_tab

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.demo.base.BaseRecyclerAdapter
import com.demo.create_post.CategoryClickedListener
import com.demo.databinding.LayoutHashtagCategoryRecyclerItemBinding
import com.demo.model.response.baseResponse.Category
import com.demo.model.response.baseResponse.PostCategory
import com.demo.util.fetchCurrentLanguageCategoryName

class CategoriesRecyclerAdapter(
    override val layoutId: Int,
    private var selectedPosition: MutableLiveData<Int>,
    private val clickedListener: CategoryClickedListener
) :
    BaseRecyclerAdapter<LayoutHashtagCategoryRecyclerItemBinding, PostCategory>() {

     override fun bind(
        holder: BaseRecyclerAdapter<LayoutHashtagCategoryRecyclerItemBinding, PostCategory>.ViewHolder,
        item: PostCategory,
        position: Int
    ) {
        holder.binding.category = item
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

        holder.binding.rbCategory.setOnClickListener {
            if (position < itemCount && position != selectedPosition.value && position > -1) {
                selectedPosition.postValue(position)
                clickedListener.selectedCategory(item)
                notifyDataSetChanged()
            }
        }
    }
}
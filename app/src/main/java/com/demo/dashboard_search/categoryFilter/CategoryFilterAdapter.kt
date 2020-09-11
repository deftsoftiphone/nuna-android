package com.demo.dashboard_search.categoryFilter

import com.demo.base.BaseRecyclerAdapter
import com.demo.databinding.LayoutCategoryFilterItemBinding
import com.demo.model.response.GetCategory
import com.demo.util.Prefs


class CategoryFilterAdapter(override val layoutId: Int) :
    BaseRecyclerAdapter<LayoutCategoryFilterItemBinding, GetCategory>() {

    override fun bind(holder: ViewHolder, item: GetCategory, position: Int) {
        holder.binding.item = list[position]
        holder.binding.position = position
        holder.binding.categoryName.setBackgroundResource(item.filterStatus.parentBg)


        /**
         *   if (selectedLanguage == 1) {
        (activity as LocalizationActivity).setLanguage("en")
        } else if (selectedLanguage == 2) {
        (activity as LocalizationActivity).setLanguage("hi")
        } else if (selectedLanguage == 5) {
        (activity as LocalizationActivity).setLanguage("bn")
        } else if (selectedLanguage == 4) {
        (activity as LocalizationActivity).setLanguage("mr")
        } else if (selectedLanguage == 8) {
        (activity as LocalizationActivity).setLanguage("te")
        } else if (selectedLanguage == 6) {
        (activity as LocalizationActivity).setLanguage("ta")
        } else if (selectedLanguage == 3) {
        (activity as LocalizationActivity).setLanguage("gu")
        } else if (selectedLanguage == 7) {
        (activity as LocalizationActivity).setLanguage("kn")
        }
         */

        if (position==0){
            holder.binding.categoryName.setText(item.categoryName)
        }else {

            if (Prefs.init().selectedLangId == 2) {
                holder.binding.categoryName.setText(item.categoryNameHindi)

            } else if (Prefs.init().selectedLangId == 5) {
                holder.binding.categoryName.setText(item.categoryNameBengali)

            } else if (Prefs.init().selectedLangId == 4) {
                holder.binding.categoryName.setText(item.categoryNameMarathi)


            } else if (Prefs.init().selectedLangId == 8) {
                holder.binding.categoryName.setText(item.categoryNameTelugu)


            } else if (Prefs.init().selectedLangId == 6) {
                holder.binding.categoryName.setText(item.categoryNameTamil)


            } else if (Prefs.init().selectedLangId == 3) {
                holder.binding.categoryName.setText(item.categoryNameGujarati)


            } else if (Prefs.init().selectedLangId == 7) {
                holder.binding.categoryName.setText(item.categoryNameKannada)


            } else if (Prefs.init().selectedLangId == 1) {
                holder.binding.categoryName.setText(item.categoryName)


            }
        }

//        if (position == 0){
//            holder.binding.rightSpace.visibility == View.VISIBLE
//        }else{
//            holder.binding.rightSpace.visibility == View.GONE
//        }
    }

}
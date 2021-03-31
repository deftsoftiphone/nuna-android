package com.demo.muebert.listener

import com.demo.muebert.modal.CategoriesItem

interface OnCategoriesClickHandler {
    fun onSelectCategories(item: CategoriesItem, position: Int)
}
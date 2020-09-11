package com.demo.create_post

import com.demo.model.response.baseResponse.Category

interface CategoryClickedListener {
    fun selectedCategory(value: Category)
}
package com.demo.create_post

import com.demo.model.response.baseResponse.Category
import com.demo.model.response.baseResponse.PostCategory

interface CategoryClickedListener {
    fun selectedCategory(value: PostCategory)
}
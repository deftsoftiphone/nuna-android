package com.demo.create_post

interface CreatePostRecyclerItemClickListener : CategoryClickedListener {
    fun selectedHashTag(value: String)
    fun fetchData(value: Any)
}
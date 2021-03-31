package com.demo.search.clickListeners

interface FollowClickListener {
    fun onFollowClick(position: Int, id: String?, isFollowed: Boolean)

    fun openScreen(id: String, categoryId: String?)
}


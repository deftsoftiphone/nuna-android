package com.demo.view_others_profile

interface ProfileClickListener {
    fun onBackPressed()
    fun onClickFollow()
    fun onClickFollowers()
    fun showPost(position: Int)
    fun removePost(id: String, index: Int)
    fun onUserClick(position: Int, id: String)
}
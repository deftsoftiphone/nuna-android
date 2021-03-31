package com.demo.notifications

import com.demo.model.response.baseResponse.PostAssociated

interface NotificationClickListener {
    fun openUser(id: String)
    fun openPost(post: PostAssociated)
}
package com.demo.model.response

import com.demo.fcm.NotificationBean
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NotificationListShow(
	
	var notificationObject: List<NotificationBean>? = emptyList()


	) : Serializable
package com.demo.model.request

import com.demo.util.Prefs

data class RequestGetPopularUsers(

    var userId : Int = Prefs.init().loginData?.userId ?: 0
	/*var pageNo: Int = 1,
	var searchFilter: String = "",
	var pageSize: Int =10,
	var createdDate: String = ""*/
)
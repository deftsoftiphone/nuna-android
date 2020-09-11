package com.demo.base

import com.demo.webservice.RestClient

open class BaseRepository(asyncViewController: AsyncViewController?) {

    val restClient : RestClient = RestClient()

    init {
        restClient.asyncViewController = asyncViewController
    }
}
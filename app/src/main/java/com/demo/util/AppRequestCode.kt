package com.demo.util

import java.util.jar.Manifest

object AppRequestCode {

    val PERMISSION_LOCATION = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)

    const val REQUEST_LOCATION_PERMISSION = 2
    const val REQUEST_CAMERA_PERMISSION = 3
    const val RC_PICK_FROM_GALLERY = 4
    const val RC_PICK_FROM_CAMERA = 5
    const val RC_GOOGLE_SIGN_IN=6
    const val RC_PLACE_AUTOCOMPLETE_REQUEST_CODE = 7
    const val RC_AR_FRAGMENT = 8

    const val RC_FILTER = 9

}
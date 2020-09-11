package com.demo.util.location

import android.app.IntentService
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import com.demo.R

import java.io.IOException
import java.util.*

class FetchAddressIntentService : IntentService("") {

    private var receiver: ResultReceiver? = null
    private val TAG = "location"

    // ...

    private fun deliverResultToReceiver(resultCode: Int, message: String) {
        val bundle = Bundle().apply { putString(Constants.RESULT_DATA_KEY, message) }
        receiver?.send(resultCode, bundle)
    }


    override fun onHandleIntent(intent: Intent?) {
        intent ?: return
        val geocoder = Geocoder(this, Locale.getDefault())
        var errorMessage = ""

        // Get the location passed to this service through an extra.
        val latitude=intent.getDoubleExtra(Constants.LOCATION_LATITUDE,0.0)
        val longitude=intent.getDoubleExtra(Constants.LOCATION_LONGITUDE,0.0)

        receiver = intent.getParcelableExtra(Constants.RECEIVER)
        // ...

        var addresses: List<Address> = emptyList()

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1)
        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available)
            Log.e(TAG, errorMessage, ioException)
        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used)
        }

        // Handle case where no address was found.
        if (addresses.isEmpty()) {
            if (errorMessage.isEmpty()) {
                //errorMessage = getString(R.string.no_address_found)
                Log.e(TAG, errorMessage)
            }
            //deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage)
        } else {
            val address = addresses[0]
            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            val addressFragments = with(address) { (0..maxAddressLineIndex).map { getAddressLine(it) } }
            Log.i(TAG, getString(R.string.address_found))
            deliverResultToReceiver(Constants.SUCCESS_RESULT, addressFragments.joinToString(separator = "\n"))
        }
    }

}
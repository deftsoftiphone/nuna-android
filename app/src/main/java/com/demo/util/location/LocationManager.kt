package com.demo.util.location

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.demo.util.AlertDialogUtils
import com.google.android.gms.location.*

import io.reactivex.disposables.Disposable


class LocationManager(private val mContext: Context) : DialogInterface.OnClickListener {


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mLocation = MutableLiveData<Location?>()
    var mLocationResponse: LiveData<Location?> = mLocation

    private lateinit var resultReceiver: AddressResultReceiver
    var mAddressOutput = MutableLiveData<String>()
    private lateinit var locationCallback: LocationCallback
    // Location updates intervals in sec
    private val UPDATE_INTERVAL: Long = 10*60*1000 // 10 min
    private val FATEST_INTERVAL: Long = 5*60*1000 // 5 min
    private val DISPLACEMENT: Float = 500f // 500 meters
    private var mDisposable: Disposable? = null


    fun getFusedClient() {
        isGPSOn()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
        createLocationCallBack()
        getLastLocation()
    }

    private fun createLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    mLocation.value = location
                    //stopLocationUpdates()
                }
            }

            override fun onLocationAvailability(p0: LocationAvailability?) {
                super.onLocationAvailability(p0)
                Log.d("location avaliable", "location avaliable")
            }
        }
    }

    private fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                mLocation.value = location

            } else
                getLocationUpdate()
            Log.d("location", "value set")
        }
    }


    private fun getLocationUpdate() {
        fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, Looper.myLooper())
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    fun getAddressFromLatLang(latitude:Double,longitude:Double): LiveData<String> {
        startIntentService(latitude,longitude)
        return mAddressOutput
    }

    private fun startIntentService(latitude:Double,longitude:Double) {
        resultReceiver = AddressResultReceiver(Handler())
        val intent = Intent(mContext, FetchAddressIntentService::class.java).apply {
            putExtra(Constants.RECEIVER, resultReceiver)
            putExtra(Constants.LOCATION_LATITUDE,latitude)
            putExtra(Constants.LOCATION_LONGITUDE, longitude)
        }
        mContext.startService(intent)
    }


    inner class AddressResultReceiver(handler: Handler) : ResultReceiver(handler) {

        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {

            if (resultData == null) {
                return
            }
            // Display the address string
            // or an error message sent from the intent service.
            val address = resultData.getString(Constants.RESULT_DATA_KEY) ?: ""

            mAddressOutput.value = address
            Log.d("address", address)

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //showToast(getString(R.string.address_found));
            }

        }
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest().apply {
            interval = UPDATE_INTERVAL
            fastestInterval = FATEST_INTERVAL
            smallestDisplacement = DISPLACEMENT
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        }
    }

    private fun isGPSOn() {

        val lm = mContext.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        var gps_enabled = false
        var network_enabled = false

        try {
            gps_enabled = lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialogUtils.showDialog(
                mContext,
                "",
                mContext.getString(com.demo.R.string.gps_network_not_enabled),
                mContext.getString(com.demo.R.string.open_location_settings),
                mContext.getString(com.demo.R.string.Cancel),
                this
            )
        }
    }


    override fun onClick(dialog: DialogInterface?, id: Int) {
        when (id) {
            DialogInterface.BUTTON_POSITIVE -> {
                mContext.startActivity( Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }

            DialogInterface.BUTTON_NEGATIVE -> {

            }
        }
        dialog?.dismiss()

    }


}
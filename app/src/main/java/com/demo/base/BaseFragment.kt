package com.demo.base


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.demo.util.Validator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext


open class BaseFragment : ScopedFragment(), CoroutineScope{
//    override val kodein by lazy { (context?.applicationContext as KodeinAware).kodein }

    var hasInitializedRootView = false
    private var rootView: View? = null


    fun getPersistentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?, layout: Int): View? {
        if (rootView == null) {
            // Inflate the layout for this fragment
            rootView = inflater?.inflate(layout,container,false)
        } else {
            // Do not inflate the layout again.
            // The returned View of onCreateView will be added into the fragment.
            // However it is not allowed to be added twice even if the parent is same.
            // So we must remove rootView from the existing parent view group
            // (it will be added back).
            (rootView?.getParent() as? ViewGroup)?.removeView(rootView)
        }

        return rootView
    }
    var commonCallbacks: CommonCallbacks? = null
    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onAttach(context: Context) {
        commonCallbacks = context as CommonCallbacks
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onDetach() {
        commonCallbacks = null
        super.onDetach()
    }

    fun showProgress() {
        commonCallbacks?.showProgressDialog()
    }

    fun hideProgress() {
        commonCallbacks?.hideProgressDialog()
    }

    fun showAlertDialog(msg: String, btnListener: DialogInterface.OnClickListener?) {
        commonCallbacks?.showAlertDialog(msg, btnListener)
    }

    fun hideAlertDialog() {
        commonCallbacks?.hideAlertDialog()
    }

    open fun onFragBack(): Boolean {
        return findNavController().navigateUp()
    }

    open fun onFilePicked(pickedFileUri: String) {}

    open fun onApiRequestFailed(apiUrl: String, errCode: Int, errorMessage: String): Boolean {
        return false
    }

    fun isNetworkConnected(): Boolean? {
      return commonCallbacks?.isNetworkAvailable()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun navigate(fragmentId: Int, vararg data: Pair<String, Any?>) {
        val b = bundleOf(*data)
        findNavController().navigate(fragmentId, b)
    }

    fun navigate(fragmentId: Int, data: Bundle) {
        findNavController().navigate(fragmentId, data)
    }

    fun getParcelOrNull(fragmentId: Int): Bundle? {
        return commonCallbacks!!.getSharedModel().parcels[fragmentId]
    }

    fun getParcelAndConsume(fragmentId: Int, consumer: (b: Bundle) -> Unit) {
        val parcelMap = commonCallbacks!!.getSharedModel().parcels
        parcelMap[fragmentId]?.apply {
            consumer(this)
            parcelMap.remove(fragmentId)
        }
    }

    open fun putParcel(parcel: Bundle, fragmentId: Int) {
        commonCallbacks!!.getSharedModel().parcels[fragmentId] = parcel
    }

    open fun onReceiveLocation(newLocation: Location?): Boolean {
        return false
    }


    fun handleAPIError(msg: String) {
        commonCallbacks?.hideProgressDialog()
        Log.e("ERROR", msg)
        Validator.showMessage(msg)
//        Validator.showCustomToast(getString(R.string.something_went_wrong))
    }




}
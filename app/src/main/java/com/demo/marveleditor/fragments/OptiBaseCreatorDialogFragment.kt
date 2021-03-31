/*
 *
 *  Created by Optisol on Aug 2019.
 *  Copyright © 2019 Optisol Business Solutions pvt ltd. All rights reserved.
 *
 */

package com.demo.marveleditor.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import com.demo.R
//import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import java.io.File

abstract class OptiBaseCreatorDialogFragment : DialogFragment() {
    private var permissionsRequired = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.share_dialog1)

        this.isCancelable = false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            130 -> {
                for (permission in permissions) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            activity as Activity,
                            permission
                        )
                    ) {
                        //denied
                        callPermissionSettings()
                        Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                        break
                    } else {
                        if (ActivityCompat.checkSelfPermission(
                                requireContext(),
                                permissionsRequired[0]
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            //SaveImage()
                        } else {
//                            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
//                            break
                            callPermissionSettings()
                        }
                    }
                }
                return
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        dialog?.let { super.onCancel(it) }
        stopRunningProcess()
    }

    private fun callPermissionSettings2() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", requireContext().applicationContext.packageName, null)
        intent.data = uri
        startActivityForResult(intent, 300)
    }

    private fun callPermissionSettings() {

        ///-----------------
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
            Toast.makeText(
                context,
                android.R.string.yes, Toast.LENGTH_SHORT
            ).show()
            //   openSettings()

            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri =
                Uri.fromParts("package", requireContext().applicationContext.packageName, null)
            intent.data = uri
            startActivityForResult(intent, 300)
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            Toast.makeText(
                context,
                android.R.string.no, Toast.LENGTH_SHORT
            ).show()
        }

        /* builder.setNeutralButton("Maybe") { dialog, which ->
             Toast.makeText(context,
                 "Maybe", Toast.LENGTH_SHORT).show()
         }*/
        builder.show()

    }

    override fun onResume() {
        super.onResume()


    }

    override fun onStart() {
        super.onStart()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                permissionsRequired[0]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(permissionsRequired, 130)
        }
    }

    interface CallBacks {

        fun onDidNothing()

        fun onFileProcessed(file: File)

        fun getFile(): File?

        fun reInitPlayer()

        fun onAudioFileProcessed(convertedAudioFile: File)

        fun showLoading(isShow: Boolean)

        fun openGallery()

        fun openCamera()
    }

    abstract fun permissionsBlocked()


    fun stopRunningProcess() {
//        FFmpeg.getInstance(activity).killRunningProcesses()
    }

    fun isRunning(): Boolean {
//        return FFmpeg.getInstance(activity).isFFmpegCommandRunning
        return false
    }

    fun showInProgressToast() {
        Toast.makeText(
            activity,
            "Operation already in progress! Try again in a while.",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun getMimeType(url: String): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }
}
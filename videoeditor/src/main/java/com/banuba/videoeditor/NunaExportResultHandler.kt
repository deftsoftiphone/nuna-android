package com.banuba.videoeditor

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.banuba.sdk.ve.flow.ExportResultHandler
import com.banuba.sdk.veui.ui.EXTRA_EXPORTED_SUCCESS
import com.banuba.sdk.veui.ui.ExportResult

class NunaExportResultHandler : ExportResultHandler {

    override fun doAction(activity: AppCompatActivity, result: ExportResult.Success?) {
        val intent = Intent().apply {
            result?.let { putExtra(EXTRA_EXPORTED_SUCCESS, it) }
        }
        activity.apply {
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}
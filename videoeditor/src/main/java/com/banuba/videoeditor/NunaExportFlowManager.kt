package com.banuba.videoeditor

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.banuba.sdk.ve.flow.ExportFlowManager
import com.banuba.sdk.veui.data.EditorSessionHelper
import com.banuba.sdk.veui.ui.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class NunaExportFlowManager(
    private val exportDataProvider: ExportDataProvider,
    private val editorSessionHelper: EditorSessionHelper,
    private val exportDir: Uri
) : ExportFlowManager {

    companion object {
        private const val TAG = "NunaExportManager"
    }

    override val provideExportInBackground: Boolean
        get() = false

    private val _resultData = MutableLiveData<ExportResult>(ExportResult.Inactive)
    override val resultData: LiveData<ExportResult>
        get() = _resultData

    private var parentJob: Job? = null
    private var scope = CoroutineScope(Dispatchers.IO)

    override fun startExport(exportTaskParams: ExportTaskParams) {
        parentJob?.cancel()
        parentJob = scope.launch {
            val previewResult = exportDataProvider.requestPreviewAsync(exportTaskParams).await()
            if (previewResult.isFailure) {
                withContext(Dispatchers.Main) {
                    _resultData.value = ExportResult.Error("Export failed!")
                }
                stopExport()
                return@launch
            }
            _resultData.postValue(ExportResult.Progress(preview = previewResult.getOrThrow().imageUri))
            val videoResult = exportDataProvider.requestExportAsync(exportTaskParams)
            when (val state = transformExport(
                videoResult.await(),
                previewResult
            )) {
                is ExportFlowState.Success -> {
                    val exportResult = prepareExportResult(state)
                    when (exportResult) {
                        is ExportResult.Success -> {
                            editorSessionHelper.clearAll()
                        }
                    }
                    withContext(Dispatchers.Main) {
                        _resultData.value = exportResult
                    }
                }
                is ExportFlowState.Failure -> {
                    Log.w(
                        TAG,
                        "Could not complete export video or preview!",
                        state.exportedTaskException
                    )
                    withContext(Dispatchers.Main) {
                        _resultData.value = ExportResult.Error("Export failed!")
                    }
                }
            }
            stopExport()
        }
    }

    override fun stopExport() {
        parentJob?.cancel()
        _resultData.postValue(ExportResult.Inactive)
    }

  /*  override fun releaseRawSessionData() {
        editorSessionHelper.clearAll()
    }
*/
    private fun transformExport(
        videoResult: List<Result<ExportedVideo>>,
        previewResult: Result<ExportedPreview>
    ): ExportFlowState {
        return if (videoResult.all { it.isSuccess } && previewResult.isSuccess) {
            ExportFlowState.Success(
                videoResult.map { it.getOrThrow() },
                previewResult.getOrThrow()
            )
        } else {
            val targetException = videoResult.first().exceptionOrNull()
                ?: previewResult.exceptionOrNull()
                ?: IllegalStateException("No exception in export video and preview tasks!")
            ExportFlowState.Failure(targetException)
        }
    }

    private fun prepareExportResult(state: ExportFlowState.Success): ExportResult {
        Log.d(TAG, "Export video and preview finished successfully")
        return try {
            val exportDirFile = exportDir.toFile().apply {
                if (!exists()) {
                    mkdirs()
                }
            }

            val metaFile = File(exportDirFile, "exported_meta.txt")
            val previewFile = File(exportDirFile, "exported_preview.png")

            ExportResult.Success(
                message = "Export finished successfully!",
                videoList = state.videoList,
                preview = saveFile(state.preview.imageUri, previewFile),
                metaUri = saveFile(editorSessionHelper.getSessionParamsFile().toUri(), metaFile)
            )
        } catch (e: Exception) {
            ExportResult.Error("Could not complete exporting!")
        }
    }

    private fun saveFile(sourceUri: Uri, destFile: File): Uri {
        return sourceUri.encodedPath?.let {
            File(it).copyTo(destFile, true)
        }?.toUri() ?: Uri.EMPTY
    }

    internal sealed class ExportFlowState {
        data class Success(
            val videoList: List<ExportedVideo>,
            val preview: ExportedPreview
        ) :
            ExportFlowState()

        data class Failure(val exportedTaskException: Throwable?) : ExportFlowState()
    }
}



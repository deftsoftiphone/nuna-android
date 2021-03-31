package com.demo.muebert.view

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.banuba.sdk.core.domain.ProvideTrackContract
import com.banuba.sdk.core.domain.TrackData
import com.demo.R
import com.demo.base.BaseActivity
import com.demo.databinding.ActivityAwesomeAudioContentBinding
import com.demo.muebert.listener.MusicClickHandler
import com.demo.muebert.modal.ChannelsItem
import com.demo.muebert.viewmodel.AwesomeAudioContentFactory
import com.demo.muebert.viewmodel.AwesomeAudioContentViewModel
import com.demo.providers.mediaPlayer.AudioStreamListener
import com.demo.providers.mediaPlayer.MediaPlayerProvider
import com.demo.util.Util
import com.demo.util.Validator
import com.demo.util.updateLanguage
import com.demo.viewPost.utils.ConstantObjects
import com.demo.webservice.DownloadFileService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.io.File
import java.util.*

class AwesomeAudioContentActivity : BaseActivity(), KodeinAware,
    MusicClickHandler, AudioStreamListener {
    override val kodein: Kodein by closestKodein()
    private val mViewModelFactory: AwesomeAudioContentFactory by instance()
    private val player: MediaPlayerProvider by instance()
    private lateinit var mBinding: ActivityAwesomeAudioContentBinding
    private lateinit var mViewModel: AwesomeAudioContentViewModel
    private var downLoadBroadcastReceiver: BroadcastReceiver = DownLoadBroadcast()
    private var trackFile: File? = null
    private var mediaTrack: String? = null
    private var trackTitle: String = ""
    private var filePath = "audioFile.MP3"
    private var isTrackReady: Boolean = false

    companion object {
        var TRACK_DURATION = 0
        fun buildPickMusicResourceIntent(
            context: Context,
            extras: Bundle
        ) =
            Intent(context, AwesomeAudioContentActivity::class.java).apply {
                putExtras(extras)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateLanguage()
        setStatusBarColor()
        setUpUI()
        setUpViewModel()
        setUpObserver()

    }

    private fun setUpObserver() {
        mViewModel.showLoading.observe(this@AwesomeAudioContentActivity, Observer {
            it?.let {
                if (it) {
                    showProgressDialog()
                } else {
                    hideProgressDialog()
                }
            }
        })

        mViewModel.trackData.observe(this@AwesomeAudioContentActivity, Observer {
            it?.let { taskItem ->
                taskItem.downloadLink?.let { track ->
                    mediaTrack = track
                    hideProgressDialog()
                    playMusic(track)
                    mBinding.apply {
                        if (!clMusicTile.isVisible) {
                            rvStream.setPadding(
                                0,
                                0,
                                0,
                                Util.pxToDp(this@AwesomeAudioContentActivity, 65)
                            )
                            ConstantObjects.slideUp(clMusicTile, clParent)
                        }
                    }
                }
            }
        })
    }

    private fun playMusic(track: String) {
        player.play(track)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(downLoadBroadcastReceiver)
        player.releasePlayer()
    }

    override fun setLang(strLang: String) {

    }

    private fun setUpViewModel() {
        mViewModel =
            ViewModelProvider(this, mViewModelFactory).get(AwesomeAudioContentViewModel::class.java)
        mViewModel.setMusicClickHandler(this@AwesomeAudioContentActivity)
        mBinding.rvCategories.adapter = mViewModel.mMusicCategoryAdapter
        mBinding.rvStream.adapter = mViewModel.mMusicStreamAdapter
        mBinding.viewModel = mViewModel
        mBinding.ivStream.isSelected = true
    }

    private fun setUpUI() {
        if (intent.hasExtra(ProvideTrackContract.EXTRA_TRACK_TYPE)) {
            intent.extras?.get(ProvideTrackContract.EXTRA_TRACK_TYPE).toString()?.let {
                if (it.equals("EFFECT")) {
                    TRACK_DURATION = 5
                } else {
                    TRACK_DURATION = 60
                }
            }
        }

        registerReceiver(
            downLoadBroadcastReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
        player.eventListener(this@AwesomeAudioContentActivity)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_awesome_audio_content)
        mBinding.apply {
            ivBack.setOnClickListener { onBackPressed() }
            rvCategories.layoutManager =
                GridLayoutManager(
                    this@AwesomeAudioContentActivity,
                    4,
                    GridLayoutManager.VERTICAL,
                    false
                )
            rvStream.layoutManager = LinearLayoutManager(this@AwesomeAudioContentActivity)
        }
    }

    override fun onTrackClick(item: ChannelsItem, position: Int) {
        mBinding.apply {
            mBinding.pbProgress.visibility = View.VISIBLE
            isTrackReady = false
            mViewModel.getTrack(item.playlist!!)
//            mViewModel.recordTrack(item.playlist!!)
            data = item
        }
    }

    override fun onClickDeleteTrack(item: ChannelsItem) {
        mBinding.apply {
            player.stop()
            rvStream.setPadding(
                0,
                0,
                0,
                Util.pxToDp(this@AwesomeAudioContentActivity, 10)
            )
            ConstantObjects.slideDown(clMusicTile, clParent)
        }
    }

    override fun onClickPlayPauseTrack(item: ChannelsItem, position: Int) {
        if (player.isPlaying()) player.pause()
        else player.resume()
    }

    override fun onClickRefreshTrack(item: ChannelsItem, position: Int, view: ImageView) {
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_anim))
        mBinding.apply {
            mBinding.pbProgress.visibility = View.VISIBLE
            isTrackReady = false
//            mViewModel.recordTrack(item.playlist!!)
            mViewModel.getTrack(item.playlist!!)
        }
    }

    override fun onClickSelectTrack(item: ChannelsItem, position: Int) {
        val file = File(
            getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path,
            filePath
        )
        MediaScannerConnection.scanFile(
            this,
            arrayOf(file.path),
            null
        ) { path, uri ->
            if (file.exists()) {
                file.delete()
            }
        }
        mediaTrack?.let {
            trackTitle = item.name!!
            if (!mediaTrack.isNullOrBlank() && isTrackReady) {
                showProgressDialog()
                GlobalScope.launch(Dispatchers.IO) {
                    startService(
                        Intent(
                            this@AwesomeAudioContentActivity,
                            DownloadFileService::class.java
                        ).apply {
                            putExtra("FILE_URL", mediaTrack)
                            putExtra("FILE_PATH", filePath)
                        }
                    )
                }
            } else {
                Validator.showMessage(getString(R.string.error_track_not_ready_yet))
            }
        }
    }

    inner class DownLoadBroadcast : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            hideProgressDialog()
            trackFile = File(
                getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path,
                filePath
            )

            trackFile?.let {
                if (it.exists()) {
                    val trackToApply =
                        TrackData(UUID.randomUUID(), trackTitle, Uri.fromFile(trackFile))
                    val resultIntent = Intent().apply {
                        putExtra(
                            ProvideTrackContract.EXTRA_RESULT_TRACK_DATA,
                            trackToApply
                        )
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (player.isPlaying())
            player.pause()
    }

    override fun onResume() {
        super.onResume()
        player.resume()
    }

    override fun onLoadComplete(loaded: Boolean) {
        if (loaded) {
            isTrackReady = true
            mBinding.pbProgress.visibility = View.GONE
        }
    }

}
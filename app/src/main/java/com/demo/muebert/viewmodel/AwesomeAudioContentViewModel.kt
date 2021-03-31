package com.demo.muebert.viewmodel

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.R
import com.demo.base.ParentViewModel
import com.demo.muebert.adapter.MusicCategoryAdapter
import com.demo.muebert.adapter.MusicStreamAdapter
import com.demo.muebert.listener.MusicClickHandler
import com.demo.muebert.listener.OnCategoriesClickHandler
import com.demo.muebert.listener.OnMusicTileClickListener
import com.demo.muebert.listener.OnTrackClickListener
import com.demo.muebert.modal.*
import com.demo.muebert.repository.MuebertRepository
import com.demo.muebert.view.AwesomeAudioContentActivity
import com.demo.providers.resources.ResourcesProvider
import com.demo.util.Prefs
import com.demo.util.parseCategories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AwesomeAudioContentViewModel(
        private val muebertRepository: MuebertRepository, private val resources: ResourcesProvider
) : ParentViewModel() {

    var mMusicStreamAdapter: MusicStreamAdapter
    var mMusicCategoryAdapter: MusicCategoryAdapter
    var mClickHandler = ClickHandler()
    var trackData = MutableLiveData<TasksItem>()
    private var musicClickHandler: MusicClickHandler? = null
    private var categoriesList = mutableListOf<CategoriesItem>()

    init {
        mMusicCategoryAdapter =
                MusicCategoryAdapter(R.layout.music_categories_recycler_view_item, mClickHandler)
        mMusicStreamAdapter =
                MusicStreamAdapter(R.layout.music_stream_recycler_view_item, mClickHandler)
        if (TextUtils.isEmpty(Prefs.init().muebertPAT)) getAccess() else getMusic()
    }

    private fun getAccess() {
        showLoading.postValue(true)
        muebertRepository.getAccess(getAccessRequest()) { isSuccess, response ->
            showLoading.postValue(false)
            if (isSuccess) {
                Prefs.init().muebertPAT = response.data?.pat!!
                getMusic()
            }
        }
    }

    fun recordTrack(playList: String) {
        muebertRepository.recordTrack(
                getRecordTrackRequest(
                        playList,
                        resources.getString(R.string.muebert_record_track)
                )
        ) { isSuccess, response ->
            if (isSuccess) {
                response.data?.tasks?.let {
                    trackData.value = it[0]
                }
            }
        }
    }

    fun getTrack(playList: String) {
        viewModelScope.launch(Dispatchers.IO) {
            muebertRepository.getTrack(
                    getRecordTrackRequest(
                            playList,
                            resources.getString(R.string.muebert_get_track)
                    )
            ) { isSuccess, response ->
                GlobalScope.launch(Dispatchers.Main) {
                    if (isSuccess) {
                        response.data?.download_link?.let {
                            trackData.value = TasksItem().apply { downloadLink = it }
                        }
                    }
                }
            }
        }
    }

    private fun getAccessRequest(): BaseRequest {
        return BaseRequest().apply {
            method = resources.getString(R.string.muebert_method_access)
            params = Params().apply {
                email = resources.getString(R.string.muebert_email)
                license = resources.getString(R.string.muebert_license)
                token = resources.getString(R.string.muebert_token)
            }
        }
    }

    private fun getMusic() {
        showLoading.postValue(true)
        muebertRepository.getMusic(getMusicRequest()) { isSuccess, response ->
            showLoading.postValue(false)
            if (isSuccess) {
                response.data?.categories?.parseCategories()?.let {
                    categoriesList = it
                    mMusicCategoryAdapter.setNewItems(categoriesList)
                    it[0].name?.let { it1 -> mMusicStreamAdapter.updateChannelName(it1) }
                    mMusicStreamAdapter.setNewItems(it[0].channels!!)
                    mMusicCategoryAdapter.setItemSelected(0)
                }
            }
        }
    }

    fun setMusicClickHandler(musicClickHandler: MusicClickHandler) {
        this.musicClickHandler = musicClickHandler
    }

    inner class ClickHandler : OnTrackClickListener, OnCategoriesClickHandler,
            OnMusicTileClickListener {

        override fun onTrackClick(item: ChannelsItem, position: Int) {
            if (mMusicStreamAdapter.newPosition != position || mMusicStreamAdapter.newItem == null || item.name!! != mMusicStreamAdapter.newItem!!.name
            ) {
                musicClickHandler?.onTrackClick(item, position)
                mMusicStreamAdapter.updateMusicController(item, position)
            }
        }

        override fun onClickPlayPauseTrack(item: ChannelsItem, position: Int) {
            if (mMusicStreamAdapter.newPosition == position && mMusicStreamAdapter.newItem != null && item.name!! == mMusicStreamAdapter.newItem!!.name
            ) {
                musicClickHandler?.onClickPlayPauseTrack(item, position)
            }
            if (mMusicStreamAdapter.newPosition != position || mMusicStreamAdapter.newItem == null || item.name!! != mMusicStreamAdapter.newItem!!.name
            ) {
                mMusicStreamAdapter.updateMusicController(item, position)
                musicClickHandler?.onTrackClick(item, position)
            }
        }

        override fun onClickRefreshTrack(item: ChannelsItem, position: Int, view: View) {
            musicClickHandler?.onClickRefreshTrack(item, position, view as ImageView)
        }

        override fun onClickSelectTrack(item: ChannelsItem, position: Int) {
            musicClickHandler?.onClickSelectTrack(item, position)
        }

        override fun onSelectCategories(item: CategoriesItem, position: Int) {
            item.channels?.let {
                item.name?.let { channelName -> mMusicStreamAdapter.updateChannelName(channelName) }
                mMusicStreamAdapter.setNewItems(it)
                mMusicCategoryAdapter.setItemSelected(position)
            }
        }

        override fun onClickDeleteTrack(item: ChannelsItem) {
            musicClickHandler?.onClickDeleteTrack(item)
            mMusicStreamAdapter.deleteStream(item)
        }
    }

    private fun getMusicRequest(): BaseRequest {
        return BaseRequest().apply {
            method = resources.getString(R.string.muebert_method_music)
            params = Params().apply {
                pat = Prefs.init().muebertPAT
            }
        }
    }

    private fun getRecordTrackRequest(playList: String, mtd: String): BaseRequest {
        return BaseRequest().apply {
            method = mtd
            params = Params().apply {
                pat = Prefs.init().muebertPAT
                playlist = playList
                duration = AwesomeAudioContentActivity.TRACK_DURATION.toString()
                format = resources.getString(R.string.muenert_file_format)
                intensity = resources.getString(R.string.muenert_track_intensity)
                bitrate = resources.getString(R.string.muenert_track_bitrate)
                mode = resources.getString(R.string.muenert_mode_track)
            }
        }
    }
}
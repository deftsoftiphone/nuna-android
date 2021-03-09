package com.demo.notifications

import android.graphics.Typeface
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.demo.R
import com.demo.base.ParentViewModel
import com.demo.model.response.baseResponse.*
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.home.HomeRepository
import com.demo.util.Prefs
import com.demo.util.listToArrayList
import com.demo.util.nullAdd

class NotificationsViewModel(
    private val homeRepository: HomeRepository,
    private val resources: ResourcesProvider
) : ParentViewModel() {
    var params = QueryParams()
    var mAllActivityAdapter: NotificationsAllActivityAdapter
    var mAllSuggestionsAdapter: NotificationsSuggestionsAdapter
    var activities = MutableLiveData<ArrayList<Activity>?>()
    var suggestions = MutableLiveData<ArrayList<Activity>?>()
    var showActivities = ObservableBoolean(false)
    var showSuggestions = ObservableBoolean(false)
    var allActivitiesLoaded = MutableLiveData(false)
    var allSuggestionsLoaded = true
    var title = ObservableField("")
    var notificationClickListener: NotificationClickListener? = null
    var showData = false
    var clickHandler: AllNotificationsFragment.ClickHandler? = null
    private var face1: Typeface
    private var face2: Typeface

    init {
        if (Prefs.init().selectedLangId == 2) {
            face1 = resources.getFont("fonts/gotham_pro.ttf")
            face2 = resources.getFont("fonts/gothampro_medium.ttf")
        } else {
            face1 = resources.getFont("fonts/nirmala.ttf")
            face2 = resources.getFont("fonts/nirmala_b.ttf")
        }

        mAllSuggestionsAdapter = NotificationsSuggestionsAdapter(
            R.layout.suggestion_item, resources, face1, face2
        )
        mAllSuggestionsAdapter.click = suggestionClickListener()


        mAllActivityAdapter = NotificationsAllActivityAdapter(
            R.layout.notifications_item, getNotificationMessage()
        )
        mAllActivityAdapter.apply {
            click = activityClickLister()
            updateTypeface(face1, face2)
        }
    }

    fun updateClickListener(notificationClickListener: NotificationClickListener) {
        this.notificationClickListener = notificationClickListener
    }


    fun updateTab(tab: String) {
        resetParams()
        params.tab = tab
        mAllActivityAdapter.updateMessage(getNotificationMessage())
        getActivities(0, true)
//        getSuggestions(0, true)
    }

    private fun suggestionClickListener(): NotificationsSuggestionsAdapter.OnItemClickPosts {
        return object : NotificationsSuggestionsAdapter.OnItemClickPosts {
            override fun onFollowClick(position: Int, id: String?, isFollowed: Boolean) {
                followUser(position, id!!, isFollowed)
            }

            override fun openScreen(
                id: String,
                categoryId: String?,
                postId: String?,
                position: Int?
            ) {
                notificationClickListener?.openUser(id)
            }

            override fun openPost(id: String, post: PostAssociated, position: Int) {
            }

            override fun readAdminNotification(id: String, position: Int) {}
        }
    }

    private fun activityClickLister(): NotificationsSuggestionsAdapter.OnItemClickPosts {
        return object : NotificationsSuggestionsAdapter.OnItemClickPosts {
            override fun onFollowClick(position: Int, id: String?, isFollowed: Boolean) {
            }

            override fun openScreen(
                id: String,
                categoryId: String?,
                postId: String?,
                position: Int?
            ) {
                notificationClickListener?.openUser(id)
                position?.let { readNotification(postId!!, it) }
            }

            override fun openPost(id: String, post: PostAssociated, position: Int) {
                readNotification(id, position)
                notificationClickListener?.openPost(post)
            }

            override fun readAdminNotification(id: String, position: Int) {
                readNotification(id, position)
//                notificationClickListener?.showPostForAdmin()
                getActivities(0, true)
            }
        }
    }


    fun getSuggestions(offset: Int, showProgress: Boolean) {
        params.suggestedOffset = offset
        if (showProgress) showLoading.postValue(true)
        homeRepository.getNotifications(params) { isSuccess, baseResponse ->
            showLoading.postValue(false)
            if (isSuccess) {
                baseResponse.data?.suggestions?.let {
                    if (it.isNotEmpty()) {
                        showSuggestions.set(true)
                        suggestions.nullAdd(baseResponse.data?.suggestions!!)

                        if (it.size < 10)
                            allSuggestionsLoaded = true
                    } else showSuggestions.set(false)
                }
            } else {
                toastMessage.postValue(baseResponse.error?.message)
                showSuggestions.set(false)
                showLoading.postValue(false)
            }
        }
    }

    private fun setupSuggestions(response: BaseResponse) {
        if (!response.data?.suggestions.isNullOrEmpty()) {
            suggestions.postValue(response.data?.suggestions!!.listToArrayList())
        } else suggestions.postValue(ArrayList())
        showSuggestions.set(true)
    }


    fun getActivities(offset: Int, showProgress: Boolean) {
        params.notificationOffset = offset
        if (showProgress) showLoading.postValue(true)
        if (offset == 0) {
            showData = false
        }
        homeRepository.getNotifications(params) { isSuccess, baseResponse ->
            if (isSuccess) {
                if (offset == 0) {
                    activities.value?.clear()
                    allActivitiesLoaded.postValue(false)
                    mAllActivityAdapter.clearData()
                }
                showData = true
                setupSuggestions(baseResponse)
                val act = baseResponse.data?.activity
                if (!act?.isNullOrEmpty()!!) {
                    allActivitiesLoaded.postValue(act.size < 10)
                    activities.nullAdd(act)
                } else {
                    activities.postValue(ArrayList())
                    allActivitiesLoaded.postValue(true)
                }

            } else {
                toastMessage.postValue(baseResponse.error?.message)
                allActivitiesLoaded.postValue(true)
            }

            showActivities.set(true)
            showLoading.postValue(false)
        }
    }

    private fun resetParams() {
        params = QueryParams().apply {
            suggestedLimit = 10
            suggestedOffset = 0
            notificationLimit = 10
            notificationOffset = 0
        }
        activities.postValue(ArrayList())
        suggestions.postValue(ArrayList())
    }

    fun followUser(position: Int, id: String, isFollowed: Boolean) {
        homeRepository.followUser(User(userId = id),
            onResult = { isSuccess: Boolean, response: BaseResponse ->
                showLoading.postValue(false)
                if (isSuccess) {
                    mAllSuggestionsAdapter.apply {
                        list[position].noOfFollowers = response.data?.noOfFollowers?.toInt()
                        list[position].followedByMe = !isFollowed
                        notifyItemChanged(position)
                    }
                } else toastMessage.postValue(response.error?.message)
            })
    }

    fun readNotification(id: String, position: Int) {
        homeRepository.readNotification(id) { isSuccess, response ->
            if (isSuccess) {
                if (mAllActivityAdapter.itemCount >= 2 && position < mAllActivityAdapter.itemCount) {
                    mAllActivityAdapter.removeItem(position)
//                    activities.value?.removeAt(position)
                } else {
                    mAllActivityAdapter.clearData()
                    activities.postValue(ArrayList())
                }

                allActivitiesLoaded.postValue(activities.value?.size!! < 10)
                clickHandler?.updateNotificationBadgeCount()
            }
        }
    }

    private fun getNotificationMessage() = when (params.tab) {
        resources.getString(R.string.like_notification) -> resources.getString(R.string.liked_post)
        resources.getString(R.string.comment_notification) -> resources.getString(R.string.comment_on_post)
        resources.getString(R.string.follow_notification) -> resources.getString(R.string.follow_you)
        resources.getString(R.string.share_notification) -> resources.getString(R.string.share_post)
        else -> resources.getString(R.string.notification)
    }

    fun getNotificationCount() {
        homeRepository.getNotificationCount { isSuccess, response ->
            if (isSuccess) {
                Prefs.init().notificationCount = response.data?.tolalNumberOfNotifications!!
            }
        }
    }
}


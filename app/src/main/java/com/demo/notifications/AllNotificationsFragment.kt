package com.demo.notifications

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.demo.R
import com.demo.activity.DashboardActivity
import com.demo.base.BaseFragment
import com.demo.databinding.FragmentAllNotificationsBinding
import com.demo.model.response.baseResponse.DataHolder
import com.demo.model.response.baseResponse.PostAssociated
import com.demo.util.*
import com.demo.util.Util.Companion.checkIfHasNetwork
import com.demo.util.Util.Companion.hasInternet
import com.demo.viewPost.home.ViewPostActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class AllNotificationsFragment : BaseFragment(), KodeinAware,
    NotificationClickListener {
    override val kodein: Kodein by closestKodein()
    lateinit var mBinding: FragmentAllNotificationsBinding
    lateinit var mViewModel: NotificationsViewModel
    private val viewModelFactory: NotificationsViewModelFactory by instance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel =
            ViewModelProvider(this, viewModelFactory).get(NotificationsViewModel::class.java)
        mViewModel.updateClickListener(this)
        mViewModel.clickHandler = ClickHandler()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentAllNotificationsBinding.inflate(inflater, container, false).apply {
            viewModel = mViewModel
            clickHandler = ClickHandler()
        }
        setupRecycler()
        setupObservers()
        setUpSwipeListeners()
        updateUIForNoInternet()
        EventBus.getDefault().unregister(this)
        EventBus.getDefault().register(this)
        mBinding.tvShowMore.setOnClickListener {
            ClickHandler().loadMoreNotifications()
        }
        ClickGuard.guard(mBinding.tvShowMore)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        mViewModel.updateTab(getString(R.string.all_notification))
        mBinding.nsvActivities.visibility = GONE
//        mViewModel.showActivities.set(false)
//        mViewModel.showSuggestions.set(false)
        requireContext().updateLanguage()
    }

    private fun setUpSwipeListeners() {
        mBinding.apply {
            srlActivities.setColorSchemeColors(resources.getColor(R.color.colortheme))
            srlActivities.setOnRefreshListener {
                mViewModel.getNotificationCount()
                srlActivities.isRefreshing = true
                mViewModel.activities.postValue(ArrayList())
//                mViewModel.mAllActivityAdapter.clearData()
                rvActivities.scrollToPosition(0)
                mViewModel.getActivities(0, false)

                mViewModel.suggestions.postValue(ArrayList())
//                mViewModel.mAllSuggestionsAdapter.clearData()
                nsvActivities.visibility = GONE
//                updateUIForNoInternet()
            }

            if (!checkIfHasNetwork()) {
                (requireActivity() as DashboardActivity).apply {
                    hideProgressDialog()
                }
            }
        }
    }


    private fun setupObservers() {
        mViewModel.apply {
            showLoading.observe(viewLifecycleOwner, { showLoading ->
                (requireActivity() as DashboardActivity).apply {
                    if (!checkIfHasNetwork()) {
                        hideProgressDialog()
                    } else {
                        if (showLoading) {
                            showProgressDialog()
//                            this@AllNotificationsFragment.mBinding.nsvActivities.visibility =
//                                GONE
                        } else {
                            hideProgressDialog()
//                            this@AllNotificationsFragment.mBinding.nsvActivities.visibility =
//                                VISIBLE
                        }
                    }
                }
            })

            toastMessage.observe(viewLifecycleOwner, {
                if (!TextUtils.isEmpty(it))
                    Validator.showMessage(it)
            })

            allActivitiesLoaded.observe(viewLifecycleOwner, {
                if (checkIfHasNetwork()) {
                    if (Prefs.init().notificationCount > mViewModel.activities.value?.size ?: 0) {
                        if (Prefs.init().notificationCount > 10)
                            mBinding.tvShowMore.visibility = VISIBLE
                        else mBinding.tvShowMore.visibility = GONE
                    } else mBinding.tvShowMore.visibility = GONE
                } else mBinding.tvShowMore.visibility = GONE
            })


            activities.observe(viewLifecycleOwner, { activities ->
                updateUIForNoInternet()
//                mBinding.srlActivities.isRefreshing = false
                activities?.let {
                    if (it.isNotEmpty()) {
                        mAllActivityAdapter.setNewItems(it)
                    }
                }

                mBinding.apply {
                    if (mAllActivityAdapter.itemCount < 1) {
                        textAllActivity.visibility = GONE
                        lineAllActivity.visibility = VISIBLE
                        lNoDataFoundActivity.visibility = VISIBLE
                        tvShowMore.visibility = GONE
                    } else {
                        textAllActivity.visibility = VISIBLE
//                            if (Prefs.init().notificationCount == 10) mBinding.tvShowMore.visibility =
//                                GONE
                        lNoDataFoundActivity.visibility = GONE
                    }
                }

                if (showActivities.get())
                    showData()

            })

            suggestions.observe(viewLifecycleOwner, { suggestions ->
//                suggestions?.let {
                if (suggestions?.isNotEmpty()!!) {
                    mAllSuggestionsAdapter.setNewItems(suggestions)
                }
//                }

                mBinding.apply {
                    if (mAllSuggestionsAdapter.itemCount < 1) {
                        lNoDataFoundSuggestion.visibility = VISIBLE
                        textSuggested.visibility = GONE
                    } else {
                        lNoDataFoundSuggestion.visibility = GONE
                        textSuggested.visibility = VISIBLE
                    }
                }

                if (showSuggestions.get())
                    showData()
            })
        }
    }

    private fun showData() {
        Handler(Looper.getMainLooper()).postDelayed({
            mBinding.srlActivities.isRefreshing = false
            mBinding.nsvActivities.visibility = VISIBLE
        }, if (checkIfHasNetwork()) 200 else 100)
    }


    inner class ClickHandler {
        //like/comment/follow
        fun onClickLike() {
            if (requireContext().hasInternet()) {
                findNavController().navigate(
                    R.id.particularNotificationFragment,
                    bundleOf(getString(R.string.title_notification) to getString(R.string.likes))
                )
            }
        }

        fun onClickComments() {
            if (requireContext().hasInternet()) {
                findNavController().navigate(
                    R.id.particularNotificationFragment,
                    bundleOf(getString(R.string.title_notification) to getString(R.string.comments))
                )
            }
        }

        fun onClickFollowers() {
            if (requireContext().hasInternet()) {
                findNavController().navigate(
                    R.id.particularNotificationFragment,
                    bundleOf(getString(R.string.title_notification) to getString(R.string.followers))
                )
            }
        }

        fun onClickShare() {
            if (requireContext().hasInternet()) {
                findNavController().navigate(
                    R.id.particularNotificationFragment,
                    bundleOf(getString(R.string.title_notification) to getString(R.string.share))
                )
            }
        }

        fun loadMoreNotifications() {
            mViewModel.getActivities(mViewModel.activities.value?.size!!, true)
        }

        fun updateNotificationBadgeCount() {
            if (activity != null && requireActivity() is DashboardActivity)
                (requireActivity() as DashboardActivity).updateNotificationCount()
        }
    }

    private fun setupRecycler() {
        mBinding.apply {
            rvActivities.adapter = mViewModel.mAllActivityAdapter
            rvSuggested.adapter = mViewModel.mAllSuggestionsAdapter
            rvSuggested.itemAnimator?.changeDuration = 0
        }
    }

    override fun openUser(id: String) {
        if (requireContext().hasInternet()) {
            EventBus.getDefault().postSticky(Bundle())
            findNavController().navigate(
                R.id.OthersProfileFragment,
                bundleOf(ParcelKeys.OTHER_USER_ID to id)
            )
        }
    }

    override fun openPost(post: PostAssociated) {
        if (requireContext().hasInternet()) {
            EventBus.getDefault().postSticky(Bundle())

            val bundle = Bundle()
            val temp = ArrayList<PostAssociated>()
            temp.add(post)
//            bundle.putSerializable(
//                getString(R.string.intent_key_post), temp
//            )
            DataHolder.data = temp
            bundle.putInt(getString(R.string.intent_key_post_position), 0)
            startActivity(
                Intent(
                    requireContext(),
                    ViewPostActivity::class.java
                ).putExtra(getString(R.string.intent_key_show_post), bundle)
            )
        }
    }

    private fun updateUIForNoInternet() {
        mBinding.apply {
            if (!checkIfHasNetwork()) {
                textAllActivity.visibility = GONE
                lineAllActivity.visibility = VISIBLE
                lNoDataFoundActivity.visibility = VISIBLE
                nsvActivities.visibility = VISIBLE
            } else {
//                textAllActivity.visibility = VISIBLE
//                tvShowMore.visibility = if (mViewModel.allActivitiesLoaded) GONE else VISIBLE
                lNoDataFoundActivity.visibility = GONE
            }

//            if (!Util.checkIfHasNetwork()) {
//                lNoDataFoundSuggestion.visibility = VISIBLE
//                textSuggested.visibility = GONE
//            } else {
//                lNoDataFoundSuggestion.visibility = GONE
//                textSuggested.visibility = VISIBLE
//            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true)
    fun onNotificationReceived(bundle: Bundle) {
        EventBus.getDefault().removeStickyEvent(bundle)
        mViewModel.getActivities(0, true)
    }

}

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.demo.R
import com.demo.activity.DashboardActivity
import com.demo.base.BaseFragment
import com.demo.databinding.FragmentParticularNotificationsBinding
import com.demo.model.response.baseResponse.DataHolder
import com.demo.model.response.baseResponse.PostAssociated
import com.demo.util.ClickGuard
import com.demo.util.ParcelKeys
import com.demo.util.Util
import com.demo.util.Util.Companion.hasInternet
import com.demo.util.Validator
import com.demo.viewPost.home.ViewPostActivity
import org.greenrobot.eventbus.EventBus
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class ParticularNotificationsFragment : BaseFragment(), KodeinAware,
    NotificationClickListener {
    override val kodein: Kodein by closestKodein()
    lateinit var mBinding: FragmentParticularNotificationsBinding
    lateinit var mViewModel: NotificationsViewModel
    private val viewModelFactory: NotificationsViewModelFactory by instance()
    var title = ""

    val mClickHandler: ParticularNotificationsFragment.ClickHandler by lazy { ClickHandler() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            FragmentParticularNotificationsBinding.inflate(inflater, container, false).apply {
                viewModel = mViewModel
                clickHandler = ClickHandler()
            }

        readArguments()
        setupRecycler()
        setupObservers()
        setUpSwipeListeners()
        updateUIForNoInternet()
        mBinding.tvShowMore.setOnClickListener {
            ClickHandler().loadMoreNotifications()
        }
        ClickGuard.guard(mBinding.tvShowMore)
        return mBinding.root
    }

    private fun setUpSwipeListeners() {
        mBinding.apply {
            srlActivities.setColorSchemeColors(resources.getColor(R.color.colortheme))

            srlActivities.setOnRefreshListener {
                srlActivities.isRefreshing = true
                mViewModel.activities.postValue(ArrayList())
                mViewModel.mAllActivityAdapter.clearData()
                rvActivities.scrollToPosition(0)
                mViewModel.getActivities(0, false)

                mViewModel.suggestions.postValue(ArrayList())
                mViewModel.mAllSuggestionsAdapter.clearData()
//                rvSuggested.scrollToPosition(0)
//                mViewModel.getSuggestions(0, false)
                nsvActivities.visibility = GONE
                updateUIForNoInternet()
            }
        }
    }


    private fun setupObservers() {
        mViewModel.apply {
            showLoading.observe(viewLifecycleOwner, Observer { showLoading ->
                (requireActivity() as DashboardActivity).apply {
                    if (!Util.checkIfHasNetwork()) {
                        hideProgressDialog()
                    } else {
                        if (showLoading) {
                            showProgressDialog()
                            this@ParticularNotificationsFragment.mBinding.nsvActivities.visibility =
                                GONE
                        } else {
                            hideProgressDialog()
                            this@ParticularNotificationsFragment.mBinding.nsvActivities.visibility =
                                VISIBLE
                        }
                    }
                }
            })

            toastMessage.observe(viewLifecycleOwner, Observer {
                if (!TextUtils.isEmpty(it))
                    Validator.showMessage(it)
            })

            allActivitiesLoaded.observe(viewLifecycleOwner, {
                mBinding.apply {
                    if (mAllActivityAdapter.itemCount > 1) {
                        tvShowMore.visibility = if (it) GONE else VISIBLE
                    }
                }
            })


            allActivitiesLoaded.observe(viewLifecycleOwner, {
                if (!it) mBinding.tvShowMore.visibility = VISIBLE
                else mBinding.tvShowMore.visibility = GONE
            })

            activities.observe(viewLifecycleOwner, { activities ->
                activities?.let {
                    if (activities.isNotEmpty())
                        mAllActivityAdapter.setNewItems(activities)

                    mBinding.apply {
                        if (mAllActivityAdapter.itemCount < 1) {
                            lineAllActivity.visibility = VISIBLE
                            lNoDataFoundActivity.visibility = VISIBLE
                            tvShowMore.visibility = GONE
                        } else {
                            tvShowMore.visibility =
                                if (allActivitiesLoaded.value!!) GONE else VISIBLE
//                            if (mViewModel.activities.value?.size == 10) mBinding.tvShowMore.visibility =
//                                GONE
                            lNoDataFoundActivity.visibility = GONE
                        }
                    }
                }
                showData()
            })

            suggestions.observe(viewLifecycleOwner, { suggestions ->
                suggestions?.let {
                    if (suggestions.isNotEmpty())
                        mAllSuggestionsAdapter.setNewItems(suggestions)

                    mBinding.apply {
                        if (mAllSuggestionsAdapter.itemCount < 1) {
                            lNoDataFoundSuggestion.visibility = VISIBLE
                            textSuggested.visibility = GONE
                        } else {
                            lNoDataFoundSuggestion.visibility = GONE
                            textSuggested.visibility = VISIBLE
                        }
                    }
                }
            })
        }

    }

    private fun showData() {
        Handler(Looper.getMainLooper()).postDelayed({
//            mBinding.nsvActivities.visibility = VISIBLE
            mBinding.srlActivities.isRefreshing = false
        }, if (Util.checkIfHasNetwork()) 200 else 100)

    }


    private fun readArguments() {
        val argument = arguments?.getString(getString(R.string.title_notification))
        when {
            argument.equals(getString(R.string.likes)) -> {
                mViewModel.title.set(getString(R.string.likes))
                mViewModel.updateTab(getString(R.string.like_notification))
            }
            argument.equals(getString(R.string.comments)) -> {
                mViewModel.title.set(getString(R.string.commentes))
                mViewModel.updateTab(getString(R.string.comment_notification))
            }
            argument.equals(getString(R.string.followers)) -> {
                mViewModel.title.set(getString(R.string.followers))
                mViewModel.updateTab(getString(R.string.follow_notification))
            }
            else -> {
                mViewModel.title.set(getString(R.string.share))
                mViewModel.updateTab(getString(R.string.share_notification))
            }
        }
    }

    private fun setupViewModel() {
        mViewModel =
            ViewModelProvider(this, viewModelFactory).get(NotificationsViewModel::class.java)
        mViewModel.updateClickListener(this)
    }

    inner class ClickHandler {
        fun onBackPressed() {
            onFragBack()
        }

        fun loadMoreNotifications() {
            mViewModel.getActivities(mViewModel.activities.value?.size!!, true)
        }
    }


    private fun setupRecycler() {
        mBinding.apply {
            rvActivities.apply {
                adapter = mViewModel.mAllActivityAdapter
                itemAnimator?.changeDuration = 0
            }

            rvSuggested.apply {
                adapter = mViewModel.mAllSuggestionsAdapter
                itemAnimator?.changeDuration = 0
            }
        }
    }


    override fun openUser(id: String) {
        if (requireContext().hasInternet()) {
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
            if (!Util.checkIfHasNetwork()) {
                lineAllActivity.visibility = VISIBLE
                lNoDataFoundActivity.visibility = VISIBLE
                nsvActivities.visibility = VISIBLE
            } else {
//                tvShowMore.visibility = if (mViewModel.allActivitiesLoaded) GONE else VISIBLE
                lNoDataFoundActivity.visibility = GONE
            }

            if (!Util.checkIfHasNetwork()) {
                lNoDataFoundSuggestion.visibility = VISIBLE
                textSuggested.visibility = GONE
            } else {
                lNoDataFoundSuggestion.visibility = GONE
//                textSuggested.visibility = VISIBLE
            }
        }
    }
}
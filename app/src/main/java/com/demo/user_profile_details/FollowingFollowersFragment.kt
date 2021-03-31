package com.demo.user_profile_details

import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.demo.BindingAdapters
import com.demo.R
import com.demo.activity.DashboardActivity
import com.demo.base.BaseFragment
import com.demo.databinding.FollowingFollowersFragmentBinding
import com.demo.search.FollowingFollowersViewModel
import com.demo.search.FollowingFollowersViewModelfactory
import com.demo.util.*
import com.google.android.material.tabs.TabLayout
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class FollowingFollowersFragment : BaseFragment(), KodeinAware {
    override val kodein: Kodein by closestKodein()
    private val viewModelFactory: FollowingFollowersViewModelfactory by instance()
    private val mClickHandler = ClickHandler()
    private lateinit var mBinding: FollowingFollowersFragmentBinding
    private lateinit var mViewModel: FollowingFollowersViewModel


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        mViewModel =
                ViewModelProvider(
                        this,
                        viewModelFactory
                ).get(FollowingFollowersViewModel::class.java)

        mBinding = FollowingFollowersFragmentBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@FollowingFollowersFragment
            viewModel = mViewModel
            clickHandler = mClickHandler

            tabs.setTabsHeight()
            BindingAdapters.fontAccordingToLanguage(
                    tabs.getTextViewFromTabItem(0), getString(R.string.key_bold_font),
                    bold = false, italic = false, padding = 2
            )

            BindingAdapters.fontAccordingToLanguage(
                    tabs.getTextViewFromTabItem(1), getString(R.string.key_bold_font),
                    bold = false, italic = false, padding = 2
            )

            BindingAdapters.fontAccordingToLanguage(
                    tabs.getTextViewFromTabItem(2),
                    getString(R.string.key_bold_font),
                    bold = false,
                    italic = false,
                    padding = if (Prefs.init().selectedLangId == 5 || Prefs.init().selectedLangId == 8) 6 else 2
            )

        }

        mViewModel.userId.set(arguments?.getString(getString(R.string.key_user)))
        mBinding.title.text = "${arguments?.getString(getString(R.string.key_user_name))}"
        mViewModel.setupClickHandler(mClickHandler)
        onTabSelectionListener()
        setupRecyclers()
        setupScrollListener()
        setupObserver()
        mViewModel.updateParamsAccordingToTab()

        return mBinding.root
    }

    private fun setupScrollListener() {
        mBinding.apply {
            rvFollowers.setOnScrollChangeListener { _, _, _, _, _ ->
                val view = rvFollowers.getChildAt(rvFollowers.childCount - 1)
                val diff = view.bottom - (rvFollowers.height + rvFollowers.scrollY)
                val offset = mViewModel.getCurrentTabListOffset()
                if (diff == 0 && offset!! % 20 == 0 && !mViewModel.allFollowersLoaded) {
                    mViewModel.getProfileData(offset)
                }
            }

            rvFollowing.setOnScrollChangeListener { _, _, _, _, _ ->
                val view = rvFollowing.getChildAt(rvFollowing.childCount - 1)
                val diff = view.bottom - (rvFollowing.height + rvFollowing.scrollY)
                val offset = mViewModel.getCurrentTabListOffset()
                if (diff == 0 && offset!! % 20 == 0 && !mViewModel.allFollowingsLoaded) {
                    mViewModel.getProfileData(offset)
                }
            }

            rvSuggested.setOnScrollChangeListener { _, _, _, _, _ ->
                val view = rvSuggested.getChildAt(rvSuggested.childCount - 1)
                val diff = view.bottom - (rvSuggested.height + rvSuggested.scrollY)
                val offset = mViewModel.getCurrentTabListOffset()
                if (diff == 0 && offset!! % 20 == 0 && !mViewModel.allSuggestedLoaded) {
                    mViewModel.getProfileData(offset)
                }
            }
        }
    }

    private fun onTabSelectionListener() {
        mBinding.tabs.selectTab(mBinding.tabs.getTabAt(mViewModel.currentTab.get()))

        mBinding.tabs.addOnTabSelectedListener(object :
                TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                Handler().postDelayed({
                    mViewModel.apply {
                        if (currentTab.get() != tab?.position) {
                            when (tab?.position) {
                                0 -> currentTab.set(0)
                                1 -> currentTab.set(1)
                                else -> currentTab.set(2)
                            }
                            updateParamsAccordingToTab()
                        }
                    }
                }, 200)
//                mBinding.lNoDataFound.tvNoDataMessage.text =
//                    if (tab?.position == 2) getString(R.string.no_suggestions_yet) else getString(R.string.no_data_found)
            }
        })
    }

    private fun setupObserver() {
        mViewModel.apply {
            showLoading.observe(viewLifecycleOwner, Observer {
                if (it == true) {
                    (requireActivity() as DashboardActivity).showProgressDialog()
                } else (requireActivity() as DashboardActivity).hideProgressDialog()
            })

            toastMessage.observe(viewLifecycleOwner, {
                Validator.showMessage(it)
            })

            followers.observe(viewLifecycleOwner, {
                if (it.isNotEmpty()) {
                    followersAdapter.setNewItems(it)
                }
            })

            followings.observe(viewLifecycleOwner, {
                if (it.isNotEmpty()) {
                    followingsAdapter.setNewItems(it)
                }

            })

            suggested.observe(viewLifecycleOwner, {
                if (it.isNotEmpty()) {
                    suggestedAdapter.setNewItems(it)
                }
            })
        }
    }


    inner class ClickHandler {
        fun onBackPress() {
            findNavController().navigateUp()
        }

        fun openUser(id: String) {
            if (Prefs.init().profileData?.id == id)
                findNavController().navigate(R.id.ProfileFragment)
            else
                findNavController().navigate(
                        R.id.OthersProfileFragment,
                        bundleOf(ParcelKeys.OTHER_USER_ID to id)
                )


//            if (id.isNotEmpty()) {
//                startActivity(
//                    Intent(
//                        context,
//                        DashboardActivity::class.java
//                    ).apply {
//                        putExtra(
//                            getString(R.string.intent_key_from),
//                            getString(R.string.intent_key_show_user)
//                        )
//                        putExtra(getString(R.string.intent_key_user_id), id)
//                    })
//            }
        }
    }

    private fun setupRecyclers() {
        mBinding.apply {
            rvFollowers.apply {
                adapter = mViewModel.followersAdapter
                itemAnimator?.changeDuration = 0
            }
            rvFollowing.apply {
                adapter = mViewModel.followingsAdapter
                itemAnimator?.changeDuration = 0
            }
            rvSuggested.apply {
                adapter = mViewModel.suggestedAdapter
                itemAnimator?.changeDuration = 0
            }
        }
    }
}
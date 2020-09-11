package com.demo.search

import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.demo.R
import com.demo.activity.DashboardActivity
import com.demo.base.BaseFragment
import com.demo.databinding.FragmentSearchBinding
import com.demo.profile.CommonPostsAdapter
import com.demo.util.ParcelKeys
import com.demo.util.Validator
import com.google.android.material.tabs.TabLayout
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class SearchFragment : BaseFragment(), KodeinAware {
    override val kodein: Kodein by closestKodein()
    private val viewModelFactory: SearchViewModelFactory by instance()
    private val mClickHandler = ClickHandler()
    private lateinit var mBinding: FragmentSearchBinding
    private lateinit var mViewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(this, viewModelFactory).get(SearchViewModel::class.java)
        mBinding = FragmentSearchBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@SearchFragment
            viewModel = mViewModel
            clickHandler = mClickHandler
        }
        mViewModel.setupClickHandler(mClickHandler)
        onTabSelectionListener()
        setupRecyclers()
        setupScrollListener()
        setupObserver()
        mBinding.etSearch.addTextChangedListener(mViewModel.textWatcher)
        mBinding.etSearch.requestFocus()
        (requireActivity() as DashboardActivity).showKeyboard()
        setupSearchClickListener()
        return mBinding.root
    }

    private fun setupScrollListener() {
        mBinding.apply {
            rvUsers.setOnScrollChangeListener { _, _, _, _, _ ->
                val view = rvUsers.getChildAt(rvUsers.childCount - 1)
                val diff = view.bottom - (rvUsers.height + rvUsers.scrollY)
                val offset = mViewModel.getCurrentTabListOffset()
                if (diff == 0 && offset % 20 == 0 && !mViewModel.allUsersLoaded) {
                    mViewModel.setupPaginationParams()
                    mViewModel.getSearchResults(offset)
                }
            }

            rvVideos.setOnScrollChangeListener { _, _, _, _, _ ->
                val view = rvVideos.getChildAt(rvVideos.childCount - 1)
                val diff = view.bottom - (rvVideos.height + rvVideos.scrollY)
                val offset = mViewModel.getCurrentTabListOffset()
                if (diff == 0 && offset % 20 == 0 && !mViewModel.allVideosLoaded) {
                    mViewModel.setupPaginationParams()
                    mViewModel.getSearchResults(offset)
                }
            }

            rvHashTags.setOnScrollChangeListener { _, _, _, _, _ ->
                val view = rvHashTags.getChildAt(rvHashTags.childCount - 1)
                val diff = view.bottom - (rvHashTags.height + rvHashTags.scrollY)
                val offset = mViewModel.getCurrentTabListOffset()
                if (diff == 0 && offset % 20 == 0 && !mViewModel.allHashTagsLoaded) {
                    mViewModel.setupPaginationParams()
                    mViewModel.getSearchResults(offset)
                }
            }
        }
    }

    private fun onTabSelectionListener() {
        mBinding.apply {
            tabs.selectTab(tabs.getTabAt(mViewModel.currentTab.get()))
            tabs.addOnTabSelectedListener(object :
                TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    Handler().postDelayed(Runnable {
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
                }
            })
        }
    }

    private fun setupObserver() {
        mViewModel.apply {
            showLoading.observe(viewLifecycleOwner, Observer {
                if (it == true) {
                    (requireActivity() as DashboardActivity).showProgressDialog()
                } else (requireActivity() as DashboardActivity).hideProgressDialog()
            })

            toastMessage.observe(viewLifecycleOwner, Observer {
                Validator.showMessage(it)
            })

            users.observe(viewLifecycleOwner, Observer {
                if (it.isNotEmpty()) {
                    usersAdapter.setNewItems(it)
                }
            })

            videos.observe(viewLifecycleOwner, Observer {
                if (it.isNotEmpty()) {
                    videosAdapter.setNewItems(it)
                }
            })

            hashTags.observe(viewLifecycleOwner, Observer {
                if (it.isNotEmpty()) {
                    hashTagsAdapter.setNewItems(it)
                }
            })
        }
    }


    inner class ClickHandler : CommonPostsAdapter.OnItemClickPosts {
        fun onBackPress() {
            findNavController().navigateUp()
        }

        fun search() {
            (requireActivity() as DashboardActivity).hideKeyboard()
        }

        override fun onPostClick(position: Int, id: String) {
            findNavController().navigate(
                R.id.OthersProfileFragment,
                bundleOf(ParcelKeys.OTHER_USER_ID to id)
            )
        }

        fun openUser(id: String) {
            findNavController().navigate(
                R.id.OthersProfileFragment,
                bundleOf(ParcelKeys.OTHER_USER_ID to id)
            )
        }

        fun openHashTag(id: String, categoryId: String) {
            findNavController().navigate(
                R.id.allHashTagPostsFragment
                , bundleOf(
                    ParcelKeys.SELECTED_HASHTAG_ID to id
                    , ParcelKeys.SELECTED_CATEGORY_ID to categoryId
                )
            )
        }
    }

    private fun setupRecyclers() {
        mBinding.apply {
            rvUsers.apply {
                adapter = mViewModel.usersAdapter
                itemAnimator?.changeDuration = 0
            }
            rvHashTags.apply {
                adapter = mViewModel.hashTagsAdapter
                itemAnimator?.changeDuration = 0
            }
            rvVideos.apply {
                layoutManager = GridLayoutManager(requireContext(), 2)
                adapter = mViewModel.videosAdapter
                itemAnimator?.changeDuration = 0
                mViewModel.videosAdapter.click = this@SearchFragment.mClickHandler
            }
        }
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as DashboardActivity).hideKeyboard()
    }

    private fun setupSearchClickListener() {
        mBinding.etSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(
                v: TextView?,
                actionId: Int,
                event: KeyEvent?
            ): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mClickHandler.search()
                    return true
                }
                return false
            }
        })
    }

}
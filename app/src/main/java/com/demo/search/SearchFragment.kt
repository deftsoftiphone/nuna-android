package com.demo.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.demo.BindingAdapters
import com.demo.R
import com.demo.activity.DashboardActivity
import com.demo.base.BaseFragment
import com.demo.databinding.FragmentSearchBinding
import com.demo.model.response.baseResponse.DataHolder
import com.demo.profile.CommonPostsAdapter
import com.demo.util.*
import com.demo.util.Util.Companion.hasInternet
import com.demo.viewPost.home.ViewPostActivity
import com.demo.webservice.APIService
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
//        if(!::mBinding.isInitialized) {
        mViewModel = ViewModelProvider(this, viewModelFactory).get(SearchViewModel::class.java)
        mBinding = FragmentSearchBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@SearchFragment
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
        mViewModel.setupClickHandler(mClickHandler)
        setupRecyclers()
        mBinding.etSearch.addTextChangedListener(mViewModel.textWatcher)
        mBinding.etSearch.requestFocus()
        setupSearchClickListener()
        setupObserver()
        setupScrollListener()
        onTabSelectionListener()
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is DashboardActivity) {
            (requireActivity() as DashboardActivity).showKeyboard()
        }
    }

//    private fun setTabsHeight() {
//        mBinding.apply {
//            if (Prefs.init().selectedLangId == 2)
//                tabs.layoutParams.height = TypedValue.applyDimension(
//                    TypedValue.COMPLEX_UNIT_DIP,
//                    30f,
//                    resources.displayMetrics
//                ).roundToInt()
//            else
//                tabs.layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
//        }
//    }

    private fun setupScrollListener() {
        mBinding.apply {
            rvUsers.setOnScrollChangeListener { _, _, _, _, _ ->
                val view = rvUsers.getChildAt(rvUsers.childCount - 1)
                val diff = view.bottom - (rvUsers.height + rvUsers.scrollY)
                val offset = mViewModel.getCurrentTabListOffset()
                if (diff == 0 && offset % 15 == 0 && !mViewModel.allUsersLoaded) {
                    mViewModel.setupPaginationParams()
                    mViewModel.getSearchResults(offset)
                }
            }

            rvVideos.setOnScrollChangeListener { _, _, _, _, _ ->
                val view = rvVideos.getChildAt(rvVideos.childCount - 1)
                val diff = view.bottom - (rvVideos.height + rvVideos.scrollY)
                val offset = mViewModel.getCurrentTabListOffset()
                if (diff == 0 && offset % 15 == 0 && !mViewModel.allVideosLoaded) {
                    mViewModel.setupPaginationParams()
                    mViewModel.getSearchResults(offset)
                }
            }

            rvHashTags.setOnScrollChangeListener { _, _, _, _, _ ->
                val view = rvHashTags.getChildAt(rvHashTags.childCount - 1)
                val diff = view.bottom - (rvHashTags.height + rvHashTags.scrollY)
                val offset = mViewModel.getCurrentTabListOffset()
                if (diff == 0 && offset % 15 == 0 && !mViewModel.allHashTagsLoaded) {
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

    /* override fun onDetach() {
         super.onDetach()
         requireContext().stopPreLoadingService()
     }*/

    @SuppressLint("FragmentLiveDataObserve")
    private fun setupObserver() {
        mViewModel.apply {
            showLoading.observe(viewLifecycleOwner, {
                if (it) {
                    MyProgress.show(childFragmentManager)
                } else MyProgress.hide(childFragmentManager)
            })

            toastMessage.observe(viewLifecycleOwner, {
                if (!TextUtils.isEmpty(it))
                    Validator.showMessage(it)
            })

            users.observe(this@SearchFragment, {
                if (it.isNotEmpty()) {
                    if (isAdded && !isRemoving) {
                        usersAdapter.setNewItems(it)
                    }
                }
            })

            videos.observe(this@SearchFragment, {
                if (isAdded && !isRemoving) {
                    if (it.isNotEmpty()) {
                        videosAdapter.setNewItems(it)
                        requireContext().startPreLoadingService(it)
                    }
                }
            })

            hashTags.observe(this@SearchFragment, {
                if (it.isNotEmpty()) {
                    if (isAdded && !isRemoving) {
                        hashTagsAdapter.setNewItems(it)
                    }
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

        fun openUser(id: String) {
            if (requireContext().hasInternet()) {
                val userId = Prefs.init().currentUser?.id
                if (id == userId)
                    findNavController().navigate(R.id.ProfileFragment)
                else
                    findNavController().navigate(
                        R.id.OthersProfileFragment,
                        bundleOf(ParcelKeys.OTHER_USER_ID to id)
                    )
            }
        }

        override fun onPostClick(position: Int, id: String) {
            if (requireContext().hasInternet()) {
                mViewModel.videos.let {
                    val bundle = Bundle()
                    DataHolder.data = it.value
                    bundle.putInt(getString(R.string.intent_key_post_position), position)
                    startActivity(
                        Intent(
                            requireContext(),
                            ViewPostActivity::class.java
                        ).apply {
                            putExtra(getString(R.string.intent_key_show_post), bundle)
                            putExtra(
                                ViewPostActivity.URL,
                                "${APIService.BASE_URL}${APIService.API_VERSION}api/dashboard/search"
                            )
                            putExtra(ViewPostActivity.QUERY_PARAM, mViewModel.queryParams)
                        }
                    )
                }
            }
        }

        override fun onUserClick(position: Int, id: String) {
            if (requireContext().hasInternet()) {
                val userId = Prefs.init().currentUser?.id
                if (id == userId)
                    findNavController().navigate(R.id.ProfileFragment)
                else
                    findNavController().navigate(
                        R.id.OthersProfileFragment,
                        bundleOf(ParcelKeys.OTHER_USER_ID to id)
                    )
            }
        }

        fun openHashTag(id: String, categoryId: String?) {
            if (requireContext().hasInternet()) {
                findNavController().navigate(
                    R.id.allHashTagPostsFragment, bundleOf(
                        ParcelKeys.SELECTED_HASHTAG_ID to id,
//                    ParcelKeys.SELECTED_CATEGORY_ID to categoryId         intentionally not sending the category id
                    )
                )
            }
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
        if (requireActivity() is DashboardActivity) {
            (requireActivity() as DashboardActivity).hideKeyboard()
            mViewModel.currentTab.get().let {
                if (it == 2)
                    mViewModel.clearSearchingTab()
            }
        }
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

    /* @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
     fun updatePost(event: Bundle) {
         EventBus.getDefault().removeStickyEvent(event)
         val position: Int = event.getInt(getString(R.string.eventbus_key_post))
         val post: PostAssociated = event.getParcelable(getString(R.string.eventbus_key_postion))!!

         mViewModel.apply {
             if (mViewModel.currentTab.get() == 1) {
                 videosAdapter.updateItem(post, position)

                 val videosOffset  = getCurrentTabListOffset()
                 if(getCurrentTabListOffset() - 1 == position){
                     getSearchResults(videosOffset)
                 }
             }
         }
     }

     override fun onAttach(context: Context) {
         super.onAttach(context)
         if (!EventBus.getDefault().isRegistered(this)) {
             EventBus.getDefault().register(this)
         }
     }

     override fun onDetach() {
         super.onDetach()
         EventBus.getDefault().unregister(this)
     }*/
}
package com.demo.dashboard_search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.BindingAdapters
import com.demo.R
import com.demo.base.BaseFragment
import com.demo.create_post.CategoryClickedListener
import com.demo.databinding.DashboardFragmentBinding
import com.demo.hashtag_tab.CategoriesRecyclerAdapter
import com.demo.model.response.ProfilePosts
import com.demo.model.response.baseResponse.DataHolder
import com.demo.model.response.baseResponse.PostCategory
import com.demo.profile.CommonPostsAdapter
import com.demo.util.*
import com.demo.util.Util.Companion.checkIfHasNetwork
import com.demo.util.Util.Companion.hasInternet
import com.demo.viewPost.home.ViewPostActivity
import com.demo.webservice.APIService
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class DashboardFragment : BaseFragment(), KodeinAware, CommonPostsAdapter.OnItemClickPosts,
    CategoryClickedListener {
    override val kodein: Kodein by closestKodein()
    lateinit var mBinding: DashboardFragmentBinding
    lateinit var mViewModel: DashboardSearchViewModel
    private val viewModelFactory: DashboardViewModelFactory by instance()
    var mEndlessFollowingRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null
    var mEndlessPopularRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null
    private var categoriesRecyclerAdapter: CategoriesRecyclerAdapter? = null
    private var categoryScrollListener: EndlessRecyclerViewScrollListener? = null
    lateinit var followingPostsAdapter: CommonPostsAdapter
    lateinit var popularPostsAdapter: CommonPostsAdapter
    lateinit var postsList: ArrayList<ProfilePosts>
    var categoryId: String? = null
    private var useRecombee = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
    }

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::mBinding.isInitialized) {
            mBinding = DashboardFragmentBinding.inflate(inflater, container, false).apply {
                lifecycleOwner = this@DashboardFragment
                viewModel = mViewModel

//                tabs.setTabsHeight()
                BindingAdapters.fontAccordingToLanguage(
                    tabs.getTextViewFromTabItem(0), getString(R.string.key_bold_font),
                    bold = false, italic = false, padding = 2
                )

                BindingAdapters.fontAccordingToLanguage(
                    tabs.getTextViewFromTabItem(1), getString(R.string.key_bold_font),
                    bold = false, italic = false, padding = 2
                )
            }
            setupRecyclerView()
            setupCategories()
            getFollowing(true, 0)
            onTabSelectionListener()
            setupObserver()
            setupSwipeListeners()
            updateDeviceToken()
        }

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().unregister(this)
        EventBus.getDefault().register(this)
    }

    private fun setupObserver() {
        mViewModel.toastMessage.observe(viewLifecycleOwner, {
            if (!TextUtils.isEmpty(it))
                Validator.showMessage(it)
        })

        mViewModel.showLoading.observe(viewLifecycleOwner,
            {
                if (checkIfHasNetwork()) {
                    if (it) {
                        MyProgress.show(childFragmentManager)
                        mBinding.lNoDataFound.visibility = GONE
                    } else MyProgress.hide(childFragmentManager)
                } else MyProgress.hide(childFragmentManager)
            })

        mViewModel.categories.observe(viewLifecycleOwner, {
            if (it.isNotEmpty())
                categoriesRecyclerAdapter?.setNewItems(it)
            else categoriesRecyclerAdapter?.clearData()
        })
    }

    override fun onResume() {
        super.onResume()
        requireContext().updateLanguage()
        mBinding.invalidateAll()
    }

    private fun updateDeviceToken() {
        mViewModel.apply {
            followingParams.authDeviceToken = requireContext().getUniqueToken()
            popularParams.authDeviceToken = requireContext().getUniqueToken()
        }
    }

    private fun setupSwipeListeners() {
        mBinding.apply {
            srlFollowing.setColorSchemeColors(resources.getColor(R.color.colortheme))
            srlPopular.setColorSchemeColors(resources.getColor(R.color.colortheme))

            srlFollowing.setOnRefreshListener {
                mViewModel.followingPosts.clear()
                followingPostsAdapter.clearData()
                recyclerViewFollowing.scrollToPosition(0)
                getFollowing(false, 0)
            }

            mBinding.srlPopular.setOnRefreshListener {
                useRecombee = true
                mViewModel.popularPosts.clear()
                popularPostsAdapter.clearData()
                recyclerViewPopular.scrollToPosition(0)
                getPopular(false, categoryId, 0)
            }
        }
    }

    private fun onTabSelectionListener() {
        mBinding.tabs.addOnTabSelectedListener(object :
            TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                mViewModel.currentTab = tab?.position!!
                if (tab?.position == 0) {
                    mBinding.rvCategories.visibility = GONE

//                    if (mViewModel.followingPosts.size == 0) {
                    if (mViewModel.followingPosts.size == 0) {
                        getFollowing(true, 0)
                    } else {
//                        mBinding.lNoDataFound.visibility = GONE
                    }
                    mBinding.lNoDataFound.visibility = GONE
                    mBinding.srlFollowing.visibility = VISIBLE
                    mBinding.srlPopular.visibility = GONE
                } else {
                    mBinding.rvCategories.visibility = VISIBLE
                    if (mViewModel.popularPosts.size == 0) {
//                        setupCategories()
                        getPopular(true, categoryId, 0)
                    } else {
//                        lNoDataFound.visibility = GONE
                    }
                    mBinding.lNoDataFound.visibility = GONE
                    mBinding.srlFollowing.visibility = GONE
                    mBinding.srlPopular.visibility = VISIBLE
                }
            }

        })
    }

    private fun setupCategories() {
        mViewModel.categories.value?.clear()
        mViewModel.getCategories(0)
        mBinding.apply {
            rvCategories.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            categoriesRecyclerAdapter =
                CategoriesRecyclerAdapter(
                    R.layout.layout_hashtag_category_recycler_item,
                    viewModel?.selectedCategoryPosition!!,
                    this@DashboardFragment
                )
            rvCategories.adapter = categoriesRecyclerAdapter
        }
        setupScrollListener()
    }

    /*  @SuppressLint("FragmentLiveDataObserve")
      private fun getCategories(showProgress: Boolean, offset: Int) = launch {
          if (showProgress) commonCallbacks?.showProgressDialog()
          mViewModel.getCategories(offset).observe(this@DashboardFragment, Observer {
              commonCallbacks?.hideProgressDialog()
              if (it.error == null) {
                  mViewModel.categories.clear()
                  val categories = it.data?.categories
                  if (categories != null && categories.isNotEmpty()) {
                      mViewModel.updateCategories(categories)
                      if (categoriesRecyclerAdapter != null)
  //                        categoriesRecyclerAdapter!!.addCategories(
  //                            categories.removeCategoriesWithNoPost(
  //                                getString(R.string.all_categories)
  //                            ).reversed()
  //                        )

  //                        categoriesRecyclerAdapter!!.updateCategories(categories.reversed())
                          categoriesRecyclerAdapter!!.updateCategories(mViewModel.categories.reversed())

                  }
              } else {
                  commonCallbacks?.hideProgressDialog()
                  it.error!!.message?.let { it1 -> handleAPIError(it1) }
              }
          })
      }
  */
    private fun setupScrollListener() {
        /*    mBinding.rvCategories.addOnScrollListener(object :
                PaginationScrollListener(mBinding.rvCategories.layoutManager as LinearLayoutManager) {
                override fun loadMoreItems() {
                    mViewModel.getCategories(mViewModel.categories.value?.size!!)
                }

                override val isLastPage: Boolean
                    get() = mViewModel.categoriesLoaded
                override val isLoading: Boolean
                    get() = mViewModel.showLoading.value!!

            })*/
    }

    private fun setupViewModel() {
        mViewModel =
            ViewModelProvider(this, viewModelFactory).get(DashboardSearchViewModel::class.java)
    }

    private fun setupRecyclerView() {
        followingPostsAdapter = CommonPostsAdapter(R.layout.dashboard_screen_item)
        popularPostsAdapter = CommonPostsAdapter(R.layout.dashboard_screen_item)

        followingPostsAdapter.click = this
        popularPostsAdapter.click = this

        //Following Recycler View
        mBinding.recyclerViewFollowing.layoutManager = GridLayoutManager(context, 2)
        mBinding.recyclerViewFollowing.adapter = followingPostsAdapter

        //Popular Recycler View
        mBinding.recyclerViewPopular.layoutManager = GridLayoutManager(context, 2)
        mBinding.recyclerViewPopular.adapter = popularPostsAdapter

        setScrollListeners()
    }


    @SuppressLint("FragmentLiveDataObserve")
    private fun getFollowing(showProgress: Boolean, offset: Int) = launch {
//        if (!Util.checkIfHasNetwork()) {
        if (showProgress) mViewModel.showLoading.postValue(true)
        if (offset == 0) resetFollowersList()
        mViewModel.getFollowing(offset)
            .observe(this@DashboardFragment, Observer {
                mViewModel.showLoading.postValue(false)
                mBinding.srlFollowing.isRefreshing = false
                if (it.error == null) {
                    val posts = it.data?.followingPosts
                    if (posts != null && posts.isNotEmpty()) {
                        mBinding.lNoDataFound.visibility = GONE
                        mViewModel.followingPosts.addAll(it.data?.followingPosts!!)
                        followingPostsAdapter.addNewItems(it.data?.followingPosts!!)
                        EventBus.getDefault().post(posts)
                        requireContext().startPreLoadingService(posts.listToArrayList())
                    } else {
                        if (followingPostsAdapter.itemCount == 0) {
                            mBinding.lNoDataFound.visibility = VISIBLE
                        } else {
                            mBinding.recyclerViewFollowing.visibility = VISIBLE
                            mBinding.lNoDataFound.visibility = GONE
                        }
                    }
                } else {
                    if (mViewModel.followingPosts.size == 0) {
//                        runWithDelay(500) {
                        mBinding.lNoDataFound.visibility = VISIBLE
//                        }
                    }
                    if (!Util.checkIfHasNetwork()) {
                        commonCallbacks?.hideProgressDialog()
                    }
                    if (it.error?.status!! != 404 && it.error?.status!! != 400)
                        it.error?.message?.let { it1 -> handleAPIError(it1) }
                }
            })
//        } else Validator.showMessage(getString(R.string.connectErr))
    }


    @SuppressLint("FragmentLiveDataObserve")
    private fun getPopular(showProgress: Boolean, id: String?, offset: Int) = launch {
//        if (!Util.checkIfHasNetwork()) {
        if (showProgress) mViewModel.showLoading.postValue(true)
        if (offset == 0) resetPopular()
        mViewModel.getPopular(offset, id, useRecombee)
            .observe(this@DashboardFragment, {
                mViewModel.showLoading.postValue(false)
                mBinding.srlPopular.isRefreshing = false
                if (it.success!!) {
                    val posts = it.data?.popularPosts
                    if (posts != null && posts.isNotEmpty()) {
                        mBinding.lNoDataFound.visibility = GONE
                        mViewModel.popularPosts.addAll(it.data?.popularPosts!!)
                        popularPostsAdapter.addNewItems(it.data?.popularPosts!!)
                        requireContext().startPreLoadingService(mViewModel.popularPosts.mutableToArrayList())
                        if (posts.size < 1)
                            initiateFetchFromDB()
                    } else
                        initiateFetchFromDB()
                } else {
                    if (it.error?.status != null && (it.error?.status!! != 404 && it.error?.status!! != 400))
                        it.error?.message?.let { it1 -> handleAPIError(it1) }
                    if (mViewModel.popularPosts.size == 0) {
                        initiateFetchFromDB()
                    }
                    if (!Util.checkIfHasNetwork()) {
                        commonCallbacks?.hideProgressDialog()
                    }
                }
            })
//        } else Validator.showMessage(getString(R.string.connectErr))
    }


    private fun initiateFetchFromDB() {
        if (useRecombee) {
            useRecombee = false
            getPostsAfterRecombeeFalse()
            mBinding.lNoDataFound.visibility = GONE
        } else runWithDelay(0) {
            if (popularPostsAdapter.itemCount == 0) mBinding.lNoDataFound.visibility = VISIBLE
        }
    }

    private fun getPostsAfterRecombeeFalse() {
        getPopular(true, categoryId, 0)
    }

    private fun resetFollowersList() {
        mEndlessFollowingRecyclerViewScrollListener?.resetState()

    }

    private fun resetPopular() {
        mEndlessPopularRecyclerViewScrollListener?.resetState()
    }

    private fun setScrollListeners() {
        mEndlessFollowingRecyclerViewScrollListener =
            object :
                EndlessRecyclerViewScrollListener(mBinding.recyclerViewFollowing.layoutManager as GridLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    val offset = mViewModel.followingPosts.size
                    if (offset % 10 == 0 && offset != 0) {
                        getFollowing(true, offset)
                    }
                }
            }
        mBinding.recyclerViewFollowing.addOnScrollListener(
            mEndlessFollowingRecyclerViewScrollListener!!
        )


        mEndlessPopularRecyclerViewScrollListener =
            object :
                EndlessRecyclerViewScrollListener(mBinding.recyclerViewPopular.layoutManager as GridLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
//                    if (mViewModel.popularPosts.size % 10 == 0 && mViewModel.followingPosts.size != 0) {
                    val offset = mViewModel.popularPosts.size
                    if (offset != 0) {
                        getPopular(true, categoryId, offset)
                    }
                }
            }

        mBinding.recyclerViewPopular.addOnScrollListener(
            mEndlessPopularRecyclerViewScrollListener!!
        )

    }

    override fun onPostClick(position: Int, id: String) {
        if (requireContext().hasInternet()) {
        try {
            val list =
                if (mViewModel.currentTab == 0) mViewModel.followingPosts else mViewModel.popularPosts
            val bundle = Bundle()
            DataHolder.data = list.mutableToArrayList()
            bundle.putInt(getString(R.string.intent_key_post_position), position)
            startActivity(
                Intent(
                    requireContext(),
                    ViewPostActivity::class.java
                ).apply {
                    this.putExtra(getString(R.string.intent_key_show_post), bundle)
                    if (mViewModel.currentTab == 0) {
                        putExtra(
                            ViewPostActivity.URL,
                            "${APIService.BASE_URL}${APIService.API_VERSION}api/dashboard/discover/following"
                        )
                        putExtra(ViewPostActivity.QUERY_PARAM, mViewModel.followingParams)
                    } else {
                        putExtra(
                            ViewPostActivity.URL,
                            "${APIService.BASE_URL}${APIService.API_VERSION}api/dashboard/discover/popular"
                        )
                        putExtra(ViewPostActivity.QUERY_PARAM, mViewModel.popularParams)
                    }
                }
            )
        } catch (e: Exception) {
            println("DashboardFragment.onPostClick EXCEPTION")
            e.printStackTrace()
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

    override fun selectedCategory(value: PostCategory) {
        useRecombee = true
        categoryId = if (value.id == null) null else value.id
        mViewModel.popularPosts.clear()
        popularPostsAdapter.clearData()
        mBinding.recyclerViewPopular.scrollToPosition(0)
        resetPopular()
        getPopular(true, value.id, 0)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true)
    fun onFollowingUpdate(offset: Int) {
        EventBus.getDefault().removeStickyEvent(offset)
        getFollowing(false, mViewModel.followingPosts.size)
    }

}
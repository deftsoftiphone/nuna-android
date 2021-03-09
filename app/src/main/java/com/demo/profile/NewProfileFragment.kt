package com.demo.profile

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.demo.R
import com.demo.activity.DashboardActivity
import com.demo.base.BaseActivity
import com.demo.databinding.FragmentNewProfileBinding
import com.demo.model.response.baseResponse.DataHolder
import com.demo.model.response.baseResponse.UserProfile
import com.demo.util.*
import com.demo.util.Util.Companion.hasInternet
import com.demo.viewPost.home.ViewPostActivity
import com.demo.view_others_profile.ProfileClickListener
import com.demo.webservice.APIService
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_new_profile.*
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class NewProfileFragment : com.demo.base.BaseFragment(), KodeinAware,
    BaseActivity.SetOnBackActionListener {
    override val kodein: Kodein by closestKodein()
    private val viewModelFactory: ProfileViewModelFactory by instance()
    val mClickHandler by lazy { ClickHandler() }
    lateinit var mBinding: FragmentNewProfileBinding
    lateinit var mViewModel: ProfileViewModel
    private lateinit var userPostsAdapter: ProfilePostsAdapter
    private lateinit var userBoardAdapter: ProfilePostsAdapter
    var user: UserProfile? = null
    private var preloadingServiceIntent: Intent? = null
    var fromPost = false

    fun enableScroll() {
        val params: AppBarLayout.LayoutParams =
            mBinding.collapsingbar.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = (AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
        mBinding.collapsingbar.layoutParams = params
    }

    fun disableScroll() {
        val params: AppBarLayout.LayoutParams =
            mBinding.collapsingbar.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = 0
        mBinding.collapsingbar.layoutParams = params
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        setBackListener()
    }

    private fun setBackListener() {
        (requireActivity() as BaseActivity).setOnBackActionListener = this@NewProfileFragment
    }

    private fun setUpSwipeListeners() {
        mBinding.swipeSavedPosts.setColorSchemeColors(resources.getColor(R.color.colortheme))
        mBinding.swipeUserPosts.setColorSchemeColors(resources.getColor(R.color.colortheme))

        //UserPosts
        mBinding.swipeUserPosts.setOnRefreshListener {
            mBinding.swipeUserPosts.isRefreshing = true
            mViewModel.userPosts.clear()
            userPostsAdapter.clearData()
            userPostsRecycler.scrollToPosition(0)
            getUserPosts(false, 0)
            mViewModel.postsLoaded = false
        }

        //SavedPosts
        mBinding.swipeSavedPosts.setOnRefreshListener {
            mBinding.swipeSavedPosts.isRefreshing = true
            mBinding.nestedSavedPosts.setBackgroundColor(resources.getColor(android.R.color.white))
            mViewModel.userBoard.value?.clear()
            userBoardAdapter.clearData()
            mBinding.rvUserBoard.scrollToPosition(0)
            mViewModel.getUserBoard(0)
            mViewModel.boardLoaded = false
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentNewProfileBinding.inflate(inflater, container, false).apply {
            viewModel = mViewModel
            clickHandler = ClickHandler()
        }
        setTabLayoutListener()
        setupRecyclerView()
        setUpSwipeListeners()
        getUserDetails(true)
        setupObservers()
        setupScrollListener()
        mViewModel.currentTab = 0
        if (Prefs.init().selectedLangId == 2)
            mBinding.tvUserName.setMargins(0, 30, 0, 0)
        else mBinding.tvUserName.setMargins(0, 55, 0, 0)

        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        getUserDetails(true)
        mViewModel.userPosts.clear()
        userPostsAdapter.clearData()
        mBinding.nestedUserPosts.fullScroll(View.FOCUS_UP);
        getUserPosts(true, 0)
        mViewModel.postsLoaded = false
        mViewModel.userBoard.value?.clear()
        userBoardAdapter.clearData()
        mBinding.nestedSavedPosts.setBackgroundColor(resources.getColor(android.R.color.white))
        mViewModel.getUserBoard(0)
        mViewModel.boardLoaded = false
    }

    private fun setupScrollListener() {
        mBinding.apply {
            nestedUserPosts.setOnScrollChangeListener { _, _, _, _, _ ->
                val view = nestedUserPosts.getChildAt(nestedUserPosts.childCount - 1)
                view?.let {
                    val diff = view.bottom - (nestedUserPosts.height + nestedUserPosts.scrollY)
                    val offset = mViewModel.userPosts.size
                    if (diff == 0 && offset % 10 == 0 && !mViewModel.postsLoaded) {
                        getUserPosts(true, offset)
                    }
                }
            }

            nestedSavedPosts.setOnScrollChangeListener { _, _, _, _, _ ->
                val view = nestedSavedPosts.getChildAt(nestedSavedPosts.childCount - 1)
                view?.let {
                    val diff = view.bottom - (nestedSavedPosts.height + nestedSavedPosts.scrollY)
                    val offset = mViewModel.userBoard.value!!.size
                    if (diff == 0 && offset % 10 == 0 && !mViewModel.boardLoaded) {
                        mViewModel.getUserBoard(offset)
                    }
                }
            }
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun setupObservers() {
        mViewModel.apply {

            toastMessage.observe(this@NewProfileFragment, {
                if (!TextUtils.isEmpty(it) && it != getString(R.string.key_no_post_found)) {
                    Validator.showMessage(it)
                }
            })

            userBoard.observe(this@NewProfileFragment, {
                if (it.isNotEmpty()) {
                    userBoardAdapter.setNewItems(it)
                    mBinding.lNoDataFoundBoard.visibility = GONE
                } else
                    userBoardAdapter.clearData()

                if (showNoBoards && currentTab == 1) {
                    mBinding.lNoDataFoundBoard.visibility = VISIBLE
                } else
                    userBoard.value?.let { it1 -> requireContext().startPreLoadingService(it1) }

                runWithDelay(if (Util.checkIfHasNetwork()) 300 else 100) {
                    mBinding.let { b ->
                        b.swipeSavedPosts.isRefreshing = false
                        b.nestedSavedPosts.setBackgroundColor(
                            requireContext().resources.getColor(
                                android.R.color.transparent
                            )
                        )
                    }
                }
//                else if (userBoardAdapter.itemCount < 1 && )
//                    mBinding.lNoDataFoundBoard.visibility = VISIBLE
                /*else if (userBoardAdapter.itemCount < 1 && currentTab == 1) {
                    userBoardAdapter.clearData()
                    mBinding.lNoDataFoundBoard.visibility = VISIBLE
                }*/
            })
/*
            removePost.observe(viewLifecycleOwner, Observer {
                if (it > -1) {
                    if (currentTab == 0) {
                        userPostsAdapter.removeItem(it)
                    } else {
                        userBoardAdapter.removeItem(it)
                    }
                    mViewModel.userPosts.clear()
                    mViewModel.userBoard.value = ArrayList()
                    userPostsAdapter.clearData()
                    userBoardAdapter.clearData()
                    getUserDetails(false)
                    getUserPosts(false, 0)
                    getUserBoard(0)
                }
            })*/

            showLoading.observe(viewLifecycleOwner, { show ->
                (requireActivity() as DashboardActivity).apply {
                    if (!Util.checkIfHasNetwork()) {
                        hideProgressDialog()
                    } else {
                        if (show) showProgressDialog() else {
                            hideProgressDialog()
                        }
                    }
                }
            })
        }
    }

    private fun setupRecyclerView() {
        mBinding.apply {
            //userPostsPostsAdapter
            userPostsAdapter = ProfilePostsAdapter(R.layout.profile_post_item)
            userPostsRecycler.apply {
                layoutManager = GridLayoutManager(context, 2)
                itemAnimator = DefaultItemAnimator()
                adapter = userPostsAdapter
            }
            userPostsAdapter.setupClickHandler(mClickHandler)

            //savedPostsAdapter
            userBoardAdapter = ProfilePostsAdapter(R.layout.profile_post_item)
            rvUserBoard.apply {
                layoutManager = GridLayoutManager(context, 2)
                itemAnimator = DefaultItemAnimator()
                adapter = userBoardAdapter
            }
            userBoardAdapter.setupClickHandler(mClickHandler)
        }
    }

    private fun setTabLayoutListener() {
        mBinding.tabs?.getTabAt(0)?.setIcon(R.drawable.ic_posts_selected)
            ?.icon?.setTint(resources.getColor(R.color.colortheme))
        mBinding.tabs?.getTabAt(1)?.setIcon(R.drawable.ic_savings_unselected)

        mBinding.run {
            tabs.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    mViewModel.currentTab = tab?.position!!
                    if (tab?.position == 0) {
                        swipeUserPosts.visibility = VISIBLE
                        swipeSavedPosts.visibility = GONE
                        mBinding.tabs.getTabAt(0)?.setIcon(R.drawable.ic_posts_selected)
                        mBinding.tabs.getTabAt(1)?.setIcon(R.drawable.ic_savings_unselected)
                        tab?.icon?.setTint(resources.getColor(R.color.colortheme))

                        mBinding.nestedUserPosts.visibility = VISIBLE
                        mBinding.nestedSavedPosts.visibility = GONE
                        mBinding.lNoDataFoundBoard.visibility = GONE

                        if (userPostsAdapter.itemCount < 1 && mViewModel.currentTab == 0) {
                            enableScroll()
                            mBinding.lNoDataFoundPost.visibility = VISIBLE
                        } else
                            mBinding.lNoDataFoundPost.visibility = GONE


                    } else {
                        swipeUserPosts.visibility = GONE
                        swipeSavedPosts.visibility = VISIBLE
                        if (!mViewModel.boardAlreadyLoading)
                            mViewModel.getUserBoard(0)
                        disableScroll()
                        mBinding.tabs.getTabAt(1)?.setIcon(R.drawable.ic_savings)
                        mBinding.tabs.getTabAt(0)?.setIcon(R.drawable.ic_posts)
                        tab?.icon?.setTint(resources.getColor(R.color.colortheme))
                        mBinding.nestedUserPosts.visibility = GONE
                        mBinding.nestedSavedPosts.visibility = VISIBLE
                        mBinding.lNoDataFoundPost.visibility = GONE

                        if (userBoardAdapter.itemCount < 1 && mViewModel.currentTab == 1) {
                            enableScroll()
                            mBinding.lNoDataFoundBoard.visibility = VISIBLE
                        } else
                            mBinding.lNoDataFoundBoard.visibility = GONE
                    }
                }

            })
        }
    }


    private fun setupViewModel() {
        mViewModel =
            ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)
                .apply { clickHandler = mClickHandler }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun getUserDetails(showProgress: Boolean) = launch {
        if (showProgress) mViewModel.showLoading.postValue(true)
        mViewModel.getUserDetails()
            .observe(this@NewProfileFragment, {
                if (it.error == null) {
                    val user = it.data?.userProfile
                    if (user != null) {
                        Prefs.init().profileData = user
                        Prefs.init().notificationAll = user.notificationAllowed!!
                        if (user.fullName.isNullOrEmpty()) {
                            user.fullName = user.userName
                        }
                        if (user.bioData.isNullOrEmpty()) {
                            user.bioData = getString(R.string.bio_placeholder)
                        }
                        user.userName = "@" + user.userName
                        mBinding.userImage = user.profilePicture?.mediaurl
                        mBinding.userDetails = user
                        mBinding.invalidateAll()
                    }
                } else if (it.error!!.message == getString(R.string.connectErr))
                    it.error?.message?.let { it1 -> Validator.showMessage(it1) }

                runWithDelay(200) {
                    mViewModel.showLoading.postValue(false)
                }
            })
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun getUserPosts(showProgress: Boolean, offset: Int) = launch {
        if (!mViewModel.postAlreadyLoading) {
            mViewModel.postAlreadyLoading = true
            if (showProgress) mViewModel.showLoading.postValue(true)
            mViewModel.getUserPosts(offset)
                .observe(this@NewProfileFragment, {
                    swipeUserPosts.isRefreshing = false
                    if (it.error == null) {
                        val posts = it.data?.posts
                        if (posts != null && posts.isNotEmpty()) {
                            enableScroll()
                            if (posts.size < 10)
                                mViewModel.postsLoaded = true

                            mBinding.lNoDataFoundPost.visibility = GONE
                            mBinding.nestedUserPosts.visibility = VISIBLE
                            mViewModel.userPosts.addAll(it.data?.posts!!)
                            userPostsAdapter.setNewItems(mViewModel.userPosts)
                            requireContext().startPreLoadingService(mViewModel.userPosts)
                        } else if (mViewModel.userPosts.isEmpty() && mViewModel.currentTab == 0) {
                            disableScroll()
                            mBinding.nestedUserPosts.visibility = GONE
                            mBinding.lNoDataFoundPost.visibility = VISIBLE
                            userPostsAdapter.clearData()
                        }
                    } else {
                        if (it.error?.status!! != 404)
                            it.error?.message?.let { it1 -> Validator.showMessage(it1) }

                        if (mViewModel.userPosts.isEmpty() && mViewModel.currentTab == 0) {
                            disableScroll()
                            mBinding.nestedUserPosts.visibility = GONE
                            mBinding.lNoDataFoundPost.visibility = VISIBLE
                            userPostsAdapter.clearData()
                        }
                    }
                    mViewModel.postAlreadyLoading = false
                    runWithDelay(200) {
                        mViewModel.showLoading.postValue(false)
                    }
                })
        }
    }

    inner class ClickHandler : ProfileClickListener {

        fun hideSavedPostSwipe() {
            mBinding.swipeSavedPosts.isRefreshing = false
        }

        fun onClickEditProfile() {
            if (requireContext().hasInternet()) {
                findNavController().navigate(
                    R.id.EditProfileFragment,
                    bundleOf("userProfile" to mBinding.userDetails)
                )
            }
        }

        fun onClickThreeDotedButton() {
            findNavController().navigate(R.id.SettingsFragment)
        }

        override fun onBackPressed() {
        }

        override fun onClickFollow() {}

        override fun onClickFollowers() {
            if (requireContext().hasInternet()) {
                findNavController().navigate(
                    R.id.FollowingFollowersFragment,
                    bundleOf(
                        getString(R.string.key_user) to "",
                        getString(R.string.key_user_name) to "@${Prefs.init().profileData?.userName}"
                    )
                )
            }
        }

        override fun removePost(id: String, index: Int) {
            mViewModel.removePost(id, index)
        }

        override fun onUserClick(position: Int, id: String) {
            if (requireContext().hasInternet()) {
                val userId = Prefs.init().currentUser?.id
                if (id != userId)
                    findNavController().navigate(
                        R.id.OthersProfileFragment,
                        bundleOf(ParcelKeys.OTHER_USER_ID to id)
                    )
            }
        }

        override fun showPost(position: Int) {
            if (requireContext().hasInternet()) {
                val posts =
                    if (mViewModel.currentTab == 1) mViewModel.userBoard.value else mViewModel.userPosts
                posts?.let {
                    if (it.isNotEmpty()) {
                        val bundle = Bundle()
//                        bundle.putSerializable(getString(R.string.intent_key_post), it)
                        DataHolder.data = it
                        bundle.putInt(getString(R.string.intent_key_post_position), position)
                        startActivity(
                            Intent(
                                requireContext(),
                                ViewPostActivity::class.java
                            ).apply {
                                putExtra(getString(R.string.intent_key_show_post), bundle)
                                if (mViewModel.currentTab == 0) {
                                    putExtra(
                                        ViewPostActivity.URL,
                                        "${APIService.BASE_URL}${APIService.API_VERSION}api/posts/listPost"
                                    )
                                    putExtra(
                                        ViewPostActivity.QUERY_PARAM,
                                        mViewModel.userPostParams
                                    )
                                } else {
                                    putExtra(
                                        ViewPostActivity.URL,
                                        "${APIService.BASE_URL}${APIService.API_VERSION}api/userProfileBoard/details"
                                    )
                                    putExtra(
                                        ViewPostActivity.QUERY_PARAM,
                                        mViewModel.queryParams
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BaseActivity).updateLanguage()

        Prefs.init().profileData.let {
            if (it != null) {
                if (it.fullName.isNullOrEmpty())
                    it.fullName = it.userName

                if (it.bioData.isNullOrEmpty())
                    it.bioData = getString(R.string.bio_placeholder)

                it.userName = "@${it.userName}"
                mBinding.userImage = it.profilePicture?.mediaurl
                mBinding.userDetails = it
            }
        }
        fromPost =
            arguments?.getString(getString(R.string.intent_key_from)) == getString(R.string.intent_key_show_user_from_post)
    }

    /*@Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun updatePost(event: Bundle) {
        EventBus.getDefault().removeStickyEvent(event)
        val position: Int = event.getInt(getString(R.string.eventbus_key_post))
        val post: PostAssociated = event.getParcelable(getString(R.string.eventbus_key_postion))!!

        if (mViewModel.currentTab == 0) {
            userPostsAdapter.updateItem(post, position)
            if (mViewModel.userPosts.size - 1 == position) {
                getUserPosts(false, mViewModel.userPosts.size)

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
    }
    */
    override fun onActionBack(): Boolean {
        return if (fromPost) {
            requireActivity().finish()
            true
        } else {
            false
        }
    }
}



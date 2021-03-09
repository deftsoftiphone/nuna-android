package com.demo.view_others_profile

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.demo.R
import com.demo.base.BaseActivity
import com.demo.base.BaseFragment
import com.demo.databinding.FragmentOthersProfileBinding
import com.demo.model.response.baseResponse.DataHolder
import com.demo.model.response.baseResponse.User
import com.demo.model.response.baseResponse.UserProfile
import com.demo.muebert.modal.UserFollowStatus
import com.demo.profile.ProfilePostsAdapter
import com.demo.profile.ProfileViewModel
import com.demo.profile.ProfileViewModelFactory
import com.demo.util.*
import com.demo.viewPost.home.ViewPostActivity
import com.demo.webservice.APIService
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_new_profile.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class OthersProfileFragment : BaseFragment(), KodeinAware, BaseActivity.SetOnBackActionListener {
    override val kodein: Kodein by closestKodein()
    lateinit var mBinding: FragmentOthersProfileBinding
    lateinit var mViewModel: ProfileViewModel
    lateinit var mAdapter: OtherProfileAdapter
    val mClickHandler by lazy { ClickHandler() }
    private val viewModelFactory: ProfileViewModelFactory by instance()
    lateinit var userPostsAdapter: ProfilePostsAdapter
    lateinit var userBoardAdapter: ProfilePostsAdapter
    var userId: String? = null
    var user: UserProfile? = null
    var posts: UserProfile? = null
    var isFollowed = false
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
    }

    private fun setupSwipeRefresh() {
        mBinding.srlPost.setColorSchemeColors(resources.getColor(R.color.colortheme))

        //UserPosts
        mBinding.srlPost.setOnRefreshListener {
            mViewModel.userPosts.clear()
            userPostsAdapter.clearData()
            userPostsRecycler.scrollToPosition(0)
            getUserPosts(false, 0)
            getUserDetails(false)
            mBinding.srlPost.isRefreshing = true
            mViewModel.boardLoaded = false
        }

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
        }
    }

    override fun onResume() {
        super.onResume()
        getUserDetails(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BaseActivity).setOnBackActionListener = this@OthersProfileFragment
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::mBinding.isInitialized) {
            mBinding = FragmentOthersProfileBinding.inflate(inflater, container, false).apply {
                lifecycleOwner = this@OthersProfileFragment
                viewModel = mViewModel
                clickHandler = ClickHandler()
            }
            setupRecyclerView()
            setTabLayoutListener()
//            getUserDetails(true)
            getUserPosts(true, 0)
            setupScrollListener()
            setupObservers()
            setupSwipeRefresh()
        }

//        EventBus.getDefault().unregister(this)
//        EventBus.getDefault().register(this)

        if (Prefs.init().selectedLangId == 2)
            mBinding.tvUserName.setMargins(0, 30, 0, 0)
        else
            mBinding.tvUserName.setMargins(0, 55, 0, 0)
        return mBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
//        EventBus.getDefault().unregister(this)
    }

    private fun setupObservers() {
        mViewModel.apply {
            showNoData.observe(viewLifecycleOwner, {

            })

            userBoard.observe(viewLifecycleOwner, {
                if (it.isNotEmpty())
                    userBoardAdapter.setNewItems(it)

//                userBoard.value?.let { it1 -> requireContext().startPreLoadingService(it1) }
            })
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mi_settings) {
            findNavController().navigate(R.id.SettingsFragment)
            return true
        } else return super.onOptionsItemSelected(item)
    }


    private fun setupRecyclerView() {
        //userPostsPostsAdapter
        userPostsAdapter = ProfilePostsAdapter(R.layout.profile_post_item)
        userPostsAdapter.setupClickHandler(mClickHandler)
        mBinding.userPostsRecycler.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = userPostsAdapter
        }


        //savedPostsAdapter
        userBoardAdapter = ProfilePostsAdapter(R.layout.profile_post_item)
        userBoardAdapter.setupClickHandler(mClickHandler)
        /* mBinding.rvUserBoard.apply {
             layoutManager = GridLayoutManager(context, 2)
             adapter = userBoardAdapter
         }*/
    }


    private fun setTabLayoutListener() {
        mBinding.tabs.getTabAt(0)?.setIcon(R.drawable.ic_posts_selected)
            ?.icon?.setTint(resources.getColor(R.color.colortheme))
        mBinding.tabs.getTabAt(1)?.setIcon(R.drawable.ic_savings_unselected)

        mBinding.run {
            tabs.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tab?.position == 0) {

                        mBinding.tabs.getTabAt(0)?.setIcon(R.drawable.ic_posts_selected)
                        mBinding.tabs.getTabAt(1)?.setIcon(R.drawable.ic_savings_unselected)
                        tab.icon?.setTint(resources.getColor(R.color.colortheme))


                        mBinding.nestedUserPosts.visibility = VISIBLE
                        mBinding.lNoDataFound.visibility = GONE

                        if (userBoardAdapter.itemCount < 1) {
//                            getUserPosts(true)
                            mBinding.lNoDataFound.visibility = VISIBLE
                        } else {
                            enableScroll()
                            mBinding.lNoDataFound.visibility = GONE
                        }

                    }
//                    else {
//                        mBinding.tabs.getTabAt(1)?.setIcon(R.drawable.ic_savings)
//                        mBinding.tabs.getTabAt(0)?.setIcon(R.drawable.ic_posts)
//                        tab?.icon?.setTint(resources.getColor(R.color.colortheme))
//
//                        disableScroll()
//                        mBinding.nestedUserPosts.visibility = GONE
////                        savedPostsRecycler.visibility = View.VISIBLE
//                        mBinding.lNoDataFound.visibility = VISIBLE
//
//
//                    }
                }

            })
        }
    }

    private fun setupViewModel() {
        mViewModel =
            ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)
    }


    @SuppressLint("FragmentLiveDataObserve")
    private fun getUserDetails(showProgress: Boolean) = launch {
        if (showProgress) commonCallbacks?.showProgressDialog()
        mViewModel.getOtherUserDetails(userId)
            .observe(this@OthersProfileFragment, {
                if (showProgress) commonCallbacks?.hideProgressDialog()
                if (it.success!!) {
                    posts = it.data?.userProfile
                    if (posts != null) {
                        if (posts?.fullName.isNullOrEmpty()) {
                            posts?.fullName = posts?.userName
                        }
                        if (posts?.bioData.isNullOrEmpty()) {
                            posts?.bioData = getString(R.string.bio_placeholder)
                        }
                        posts?.userName = "@" + posts?.userName
                        if (userId != Prefs.init().profileData?.id) {
                            followUserChange()
                        }
                        mBinding.userDetails = posts
                    }
                } else {
                    try {
                        it.error?.let { err ->
                            err.message?.let { it1 -> Validator.showMessage(it1) }
                            if (isVisible && !isRemoving && !isHidden && !isDetached && isAdded && isResumed)
                                if (err.status == 449) {
                                    if (fromPost) {
                                        activity?.finish()
                                    } else {
                                        findNavController().navigateUp()
                                    }
                                }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            })
    }

/*    override fun onStop() {
        super.onStop()
        requireContext().stopPreLoadingService()
    }*/

    @SuppressLint("FragmentLiveDataObserve")
    private fun getUserPosts(showProgress: Boolean, offset:Int) = launch {
        if (showProgress) commonCallbacks?.showProgressDialog()
        mViewModel.getOtherUserPosts(userId, offset)
            .observe(this@OthersProfileFragment, Observer {
                if (showProgress) commonCallbacks?.hideProgressDialog()
                mBinding.srlPost.isRefreshing = false
                if (it.success!!) {
                    val posts = it.data?.posts
                    if (posts != null && posts.isNotEmpty()) {
                        enableScroll()
                        mBinding.lNoDataFound.visibility = GONE
                        mBinding.nestedUserPosts.visibility = VISIBLE
                        mViewModel.userPosts.addAll(it.data?.posts!!)
                        userPostsAdapter.addNewItems(it.data?.posts!!)
                        mViewModel.userPosts?.let {
                            if (it.isNotEmpty())
                                requireContext().startPreLoadingService(it)
                        }
                    } else {
                        if (mViewModel.userPosts.isEmpty()) {
                            disableScroll()
                            mBinding.nestedUserPosts.visibility = GONE
                            mBinding.lNoDataFound.visibility = VISIBLE
//                            requireActivity().stopPreloadingService()
                        }
                    }
                } else {
                    if (mViewModel.userPosts.isEmpty()) {
                        disableScroll()
                        mBinding.nestedUserPosts.visibility = GONE
                        mBinding.lNoDataFound.visibility = VISIBLE
                    }
                }
            })
    }

    override fun onActionBack(): Boolean {
        if (fromPost) {
            requireActivity().finish()
            return true
        } else {
            return false
        }
    }

    inner class ClickHandler : ProfileClickListener {

        override fun onBackPressed() {
            if (fromPost) requireActivity().finish()
            else onFragBack()
        }

        override fun onClickFollow() {
            if (isNetworkConnected()!!) {
                followUser(true)
            } else {
                Validator.showMessage(getString(R.string.connectErr))
            }
        }

        override fun onClickFollowers() {
            if (Util.checkIfHasNetwork()) {
                findNavController().navigate(
                    R.id.FollowingFollowersFragment,
                    bundleOf(
                        getString(R.string.key_user) to posts?.id,
                        getString(R.string.key_user_name) to posts?.userName
                    )
                )
            } else Validator.showMessage(getString(R.string.connectErr))
        }

        override fun showPost(position: Int) {
            if (Util.checkIfHasNetwork()) {
                mViewModel.userPosts?.let {
                    val bundle = Bundle()
                    DataHolder.data = mViewModel.userPosts
                    bundle.putInt(getString(R.string.intent_key_post_position), position)
                    startActivity(
                        Intent(
                            requireContext(),
                            ViewPostActivity::class.java
                        ).apply {
                            putExtra(getString(R.string.intent_key_show_post), bundle)
                            putExtra(
                                ViewPostActivity.URL,
                                "${APIService.BASE_URL}${APIService.API_VERSION}api/posts/listPost"
                            )
                            putExtra(
                                ViewPostActivity.QUERY_PARAM,
                                mViewModel.queryParams
                            )
                        }
                    )
                }
            } else Validator.showMessage(getString(R.string.connectErr))
        }

        override fun removePost(id: String, index: Int) {}
        override fun onUserClick(position: Int, id: String) {
            if (Util.checkIfHasNetwork()) {
                val userId = Prefs.init().currentUser?.id
                if (id == userId)
                    findNavController().navigate(R.id.ProfileFragment)
                else if (id == mBinding.userDetails?.id!!)
                //Do Nothing
                else
                    findNavController().navigate(
                        R.id.OthersProfileFragment,
                        bundleOf(ParcelKeys.OTHER_USER_ID to id)
                    )
            } else Validator.showMessage(getString(R.string.connectErr))
        }
    }

    private fun followUser(showProgress: Boolean) =
        launch {
            if (showProgress) commonCallbacks?.showProgressDialog()
            val user = User()
            user.userId = posts?.id

            mViewModel.followUser(user).observe(this@OthersProfileFragment, Observer {
                if (showProgress) commonCallbacks?.hideProgressDialog()

                if (it.error == null) {
                    posts?.followedByMe = !posts?.followedByMe!!
                    EventBus.getDefault().post(UserFollowStatus(posts?.id, posts?.followedByMe))
                    followUserChange()
                    getUserDetails(true)

                } else {
                    it.error?.message?.let { it1 -> handleAPIError(it1) }
                }
            })
        }


    private fun followUserChange() {
        if (Prefs.init().selectedLangId == 2) {
            val face: Typeface = ResourcesCompat.getFont(requireContext(), R.font.gotham_pro)!!

            val face1: Typeface =
                ResourcesCompat.getFont(requireContext(), R.font.gotham_pro_medium)!!

            mBinding.apply {
                if (posts?.followedByMe!!) {
                    isFollowed = true
                    bFollow.isSelected = false
                    bFollow.text = getString(R.string.following)
                    bFollow.visibility = VISIBLE
                    bFollow.typeface = face
                } else {
                    isFollowed = false
                    bFollow.isSelected = true
                    bFollow.text = getString(R.string.follow)
                    bFollow.visibility = VISIBLE
                    bFollow.typeface = face1
                }
            }
        } else {
            val face: Typeface = ResourcesCompat.getFont(requireContext(), R.font.nirmala)!!

            val face1: Typeface = ResourcesCompat.getFont(requireContext(), R.font.nirmala_b)!!

            mBinding.apply {
                if (posts?.followedByMe!!) {
                    isFollowed = true
                    bFollow.isSelected = false
                    bFollow.text = getString(R.string.following)
                    bFollow.visibility = VISIBLE
                    bFollow.typeface = face
                } else {
                    isFollowed = false
                    bFollow.isSelected = true
                    bFollow.text = getString(R.string.follow)
                    bFollow.visibility = VISIBLE
                    bFollow.typeface = face1
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userId = arguments?.getString(ParcelKeys.OTHER_USER_ID)
        fromPost =
            arguments?.getString(getString(R.string.intent_key_from)) == getString(R.string.intent_key_show_user_from_post)
    }

}

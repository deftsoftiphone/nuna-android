package com.demo.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.demo.R
import com.demo.databinding.FragmentNewProfileBinding
import com.demo.model.response.baseResponse.UserProfile
import com.demo.util.Prefs
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_new_profile.*
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class NewProfileFragment : com.demo.base.BaseFragment(), KodeinAware {
    override val kodein: Kodein by closestKodein()
    private val viewModelFactory: ProfileViewModelFactory by instance()
    val mClickHandler by lazy { ClickHandler() }
    lateinit var mBinding: FragmentNewProfileBinding
    lateinit var mViewModel: ProfileViewModel
    lateinit var userPostsAdapter: ProfilePostsAdapter
    lateinit var savedPostsAdapter: ProfilePostsAdapter
    var user: UserProfile? = null


    fun enableScroll() {
        val params: AppBarLayout.LayoutParams =
            mBinding.collapsingbar.getLayoutParams() as AppBarLayout.LayoutParams
        params.setScrollFlags(
            AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        )
        mBinding.collapsingbar.setLayoutParams(params)
    }

    fun disableScroll() {
        val params: AppBarLayout.LayoutParams =
            mBinding.collapsingbar.getLayoutParams() as AppBarLayout.LayoutParams
        params.setScrollFlags(0)
        mBinding.collapsingbar.setLayoutParams(params)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()

    }

    fun setUpSwipeListeners() {

        mBinding.swipeSavedPosts.setColorSchemeColors(resources.getColor(R.color.colortheme))
        mBinding.swipeUserPosts.setColorSchemeColors(resources.getColor(R.color.colortheme))

        //UserPosts
        mBinding.swipeUserPosts.setOnRefreshListener {
            Log.e("RefreshUSerPosts", "refresh")
            mViewModel.userPosts.clear()
            userPostsAdapter.clearData()
            userPostsRecycler.scrollToPosition(0)
            getUserPosts(true)
        }


        //SavedPosts

        //UserPosts
        mBinding.swipeSavedPosts.setOnRefreshListener(object :
            SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                mBinding.swipeSavedPosts.isRefreshing = false
            }

        })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::mBinding.isInitialized) {
            mBinding = FragmentNewProfileBinding.inflate(inflater, container, false).apply {
                lifecycleOwner = this@NewProfileFragment
                viewModel = mViewModel
                clickHandler = ClickHandler()
            }

            setTabLayoutListener()
            setupRecyclerView()
            setUpSwipeListeners()
            getUserDetails(true)
            getUserPosts(true)


        }
        return mBinding.root

    }


    private fun setupRecyclerView() {
        //userPostsPostsAdapter
        userPostsAdapter = ProfilePostsAdapter(R.layout.profile_post_item)
        mBinding.userPostsRecycler.layoutManager = GridLayoutManager(context, 2)
        mBinding.userPostsRecycler.adapter = userPostsAdapter
        mBinding.userPostsRecycler.adapter = userPostsAdapter


        //savedPostsAdapter
        savedPostsAdapter = ProfilePostsAdapter(R.layout.profile_post_item)
        mBinding.savedPostsRecycler.layoutManager = GridLayoutManager(context, 2)
        mBinding.savedPostsRecycler.adapter = savedPostsAdapter
        mBinding.savedPostsRecycler.adapter = savedPostsAdapter


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
                    if (tab?.position == 0) {
                        swipeUserPosts.visibility = View.VISIBLE
                        swipeSavedPosts.visibility = View.GONE
                        mBinding.tabs.getTabAt(0)?.setIcon(R.drawable.ic_posts_selected)
                        mBinding.tabs.getTabAt(1)?.setIcon(R.drawable.ic_savings_unselected)

                        tab?.icon?.setTint(resources.getColor(R.color.colortheme))

                        mBinding.nestedUserPosts.visibility = View.VISIBLE
                        mBinding.nestedSavedPosts.visibility = View.GONE

                        if (mViewModel.userPosts.size == 0) {
                            getUserPosts(true)
                        } else {
                            enableScroll()
                            mBinding.lNoDataFound.visibility = View.GONE
                        }

                    } else {
                        swipeUserPosts.visibility = View.GONE
                        swipeSavedPosts.visibility = View.VISIBLE

                        disableScroll()
                        mBinding.tabs.getTabAt(1)?.setIcon(R.drawable.ic_savings)
                        mBinding.tabs.getTabAt(0)?.setIcon(R.drawable.ic_posts)
                        tab?.icon?.setTint(resources.getColor(R.color.colortheme))


                        mBinding.nestedUserPosts.visibility = View.GONE
//                        savedPostsRecycler.visibility = View.VISIBLE
                        mBinding.lNoDataFound.visibility = View.VISIBLE


                    }
                }

            })
        }
    }

    private fun setupViewModel() {
        mViewModel =
            ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)
    }


    private fun getUserDetails(showProgress: Boolean) = launch {


        if (showProgress) commonCallbacks?.showProgressDialog()
        mViewModel.getUserDetails()
            .observe(this@NewProfileFragment, Observer {
                if (showProgress) commonCallbacks?.hideProgressDialog()
                if (it.error == null) {
                    val posts = it.data?.userProfile
                    if (posts != null) {

                        Prefs.init().profileData = posts

                        if (posts.fullName.isNullOrEmpty()) {
                            posts.fullName = posts.userName
                        }
                        if (posts.bioData.isNullOrEmpty()) {
                            posts.bioData = getString(R.string.bio_placeholder)

                        }
                        posts.userName = "@" + posts.userName
                        mBinding.userImage = posts.profilePicture?.mediaurl
                        mBinding.userDetails = posts

                    }

                } else {
                    handleAPIError(it.error!!.message.toString())
                }
            })


    }

    private fun getUserPosts(showProgress: Boolean) = launch {


        if (showProgress) commonCallbacks?.showProgressDialog()
        mViewModel.getUserPosts()
            .observe(this@NewProfileFragment, Observer {
                if (showProgress) commonCallbacks?.hideProgressDialog()
                if (it.error == null) {

                    val posts = it.data?.posts
                    if (posts != null && posts.isNotEmpty()) {
                        enableScroll()
                        mBinding.lNoDataFound.visibility = View.GONE
                        mBinding.nestedUserPosts.visibility = View.VISIBLE
                        mViewModel.userPosts.addAll(it.data?.posts!!)
                        userPostsAdapter.addNewItems(it.data?.posts!!)
                        swipeUserPosts.isRefreshing = false
                    } else {
                        if (mViewModel.userPosts.size == 0) {
                            swipeUserPosts.isRefreshing = false

                            disableScroll()
                            mBinding.nestedUserPosts.visibility = View.GONE
                            mBinding.lNoDataFound.visibility = View.VISIBLE
                        }

                    }
                } else {
                    swipeUserPosts.isRefreshing = false

                    if (mViewModel.userPosts.size == 0) {
                        disableScroll()
                        mBinding.nestedUserPosts.visibility = View.GONE
                        mBinding.lNoDataFound.visibility = View.VISIBLE
                    }
//                    handleAPIError(it.error!!.message.toString())
                }
            })


    }


    inner class ClickHandler {


        fun onClickEditProfile() {
            findNavController().navigate(
                R.id.EditProfileFragment,
                bundleOf("userProfile" to mBinding.userDetails)
            )
        }

        fun onClickThreeDotedButton() {
            findNavController().navigate(R.id.SettingsFragment)

        }

        fun onClickFollowers() {
            findNavController().navigate(
                R.id.FollowingFollowersFragment,
                bundleOf(
                    getString(R.string.user) to "",
                    getString(R.string.user_name) to "@${Prefs.init().profileData?.userName}")
                )


        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        Prefs.init().profileData.let {
            if (it != null) {
                if (it.fullName.isNullOrEmpty()) {
                    it.fullName = it.userName
                }
                if (it.bioData.isNullOrEmpty()) {

                    it.bioData = getString(R.string.bio_placeholder)

                }
                it.userName = "@" + it.userName

                Log.e("profileUrl", it.profilePicture?.mediaurl + "")
                mBinding.userImage = it.profilePicture?.mediaurl
                mBinding.userDetails = it
            }

        }


    }


}



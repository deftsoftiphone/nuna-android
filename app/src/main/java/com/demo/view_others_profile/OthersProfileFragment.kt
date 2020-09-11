package com.demo.view_others_profile

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.VISIBLE
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.demo.R
import com.demo.base.BaseFragment
import com.demo.databinding.FragmentOthersProfileBinding
import com.demo.model.response.baseResponse.User
import com.demo.model.response.baseResponse.UserProfile
import com.demo.profile.ProfilePostsAdapter
import com.demo.profile.ProfileViewModel
import com.demo.profile.ProfileViewModelFactory
import com.demo.util.ParcelKeys
import com.demo.util.Prefs
import com.demo.util.Validator
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class OthersProfileFragment : BaseFragment(), KodeinAware {
    override val kodein: Kodein by closestKodein()
    lateinit var mBinding: FragmentOthersProfileBinding
    lateinit var mViewModel: ProfileViewModel
    lateinit var mAdapter: OtherProfileAdapter
    val mClickHandler by lazy { ClickHandler() }
    private val viewModelFactory: ProfileViewModelFactory by instance()
    lateinit var userPostsAdapter: ProfilePostsAdapter
    lateinit var savedPostsAdapter: ProfilePostsAdapter
    var userId: String? = null
    var user: UserProfile? = null
    var posts: UserProfile? = null
    var isFollowed = false


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
            getUserDetails(true)
            getUserPosts(true)


        }

        return mBinding.root

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


                        mBinding.nestedUserPosts.visibility = View.VISIBLE
                        mBinding.nestedSavedPosts.visibility = View.GONE

                        if (mViewModel.userPosts.size == 0) {
                            getUserPosts(true)
                        } else {
                            enableScroll()
                            mBinding.lNoDataFound.visibility = View.GONE
                        }

                    } else {
                        mBinding.tabs.getTabAt(1)?.setIcon(R.drawable.ic_savings)
                        mBinding.tabs.getTabAt(0)?.setIcon(R.drawable.ic_posts)
                        tab?.icon?.setTint(resources.getColor(R.color.colortheme))

                        disableScroll()
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
        mViewModel.getOtherUserDetails(userId)
            .observe(this@OthersProfileFragment, Observer {
                if (showProgress) commonCallbacks?.hideProgressDialog()
                if (it.error == null) {
                    posts = it.data?.userProfile
                    if (posts != null) {
                        if (posts?.fullName.isNullOrEmpty()) {
                            posts?.fullName = posts?.userName
                        }
                        if (posts?.bioData.isNullOrEmpty()) {
                            posts?.bioData = getString(R.string.bio_placeholder)
                        }
                        posts?.userName = "@" + posts?.userName

                        if(userId!=Prefs.init().profileData?.id){
                            followUserChange()

                        }
                        mBinding.userDetails = posts
                    }

                }
            })


    }

    private fun getUserPosts(showProgress: Boolean) = launch {


        if (showProgress) commonCallbacks?.showProgressDialog()
        mViewModel.getOtherUserPosts(userId)
            .observe(this@OthersProfileFragment, Observer {
                if (showProgress) commonCallbacks?.hideProgressDialog()
                if (it.error == null) {

                    val posts = it.data?.posts
                    if (posts != null && posts.isNotEmpty()) {
                        enableScroll()
                        mBinding.lNoDataFound.visibility = View.GONE
                        mBinding.nestedUserPosts.visibility = View.VISIBLE
                        mViewModel.userPosts.addAll(it.data?.posts!!)
                        Log.e("Postssize", "Post${it.data!!.posts.size}")
                        userPostsAdapter.addNewItems(it.data?.posts!!)
                    } else {
                        if (mViewModel.userPosts.size == 0) {
                            disableScroll()
                            mBinding.nestedUserPosts.visibility = View.GONE
                            mBinding.lNoDataFound.visibility = View.VISIBLE
                        }

                    }
                } else {
                    if (mViewModel.userPosts.size == 0) {
                        disableScroll()
                        mBinding.nestedUserPosts.visibility = View.GONE
                        mBinding.lNoDataFound.visibility = View.VISIBLE
                    }
                }
            })


    }


    inner class ClickHandler {


        fun onBackPressed() {
            onFragBack()
        }

        fun onClickFollow() {

            if (isNetworkConnected()!!) {
                followUser(true)

            } else {
                Validator.showMessage("No Internet Connection Available!!")
            }
        }

        fun onClickFollowers() {
            findNavController().navigate(R.id.FollowingFollowersFragment, bundleOf(getString(R.string.user) to posts?.id,getString(R.string.user_name) to posts?.userName))

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
                    followUserChange()
                    getUserDetails(true)

                } else {
                    handleAPIError(it.error!!.message.toString())
                }
            })
        }


    fun followUserChange(){
        val face: Typeface = Typeface.createFromAsset(
            requireContext().assets,
            "fonts/gotham_pro.ttf"
        )

        val face1: Typeface = Typeface.createFromAsset(
            requireContext().assets,
            "fonts/gothampro_medium.ttf"
        )
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userId = arguments?.getString(ParcelKeys.OTHER_USER_ID)
    }




}

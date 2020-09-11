package com.demo.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.R
import com.demo.base.BaseFragment
import com.demo.databinding.FragmentAllNotificationsBinding
import com.demo.model.response.baseResponse.User
import com.demo.providers.resources.ResourcesProvider
import com.demo.util.ParcelKeys
import com.demo.util.Validator
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class AllNotificationsFragment : BaseFragment(), KodeinAware,
    NotificationsSuggestionsAdapter.OnItemClickPosts {
    override val kodein: Kodein by closestKodein()
    val resourceProvider: ResourcesProvider by instance()
    lateinit var mBinding: FragmentAllNotificationsBinding
    lateinit var mViewModel: NotificationsViewModel
    private val viewModelFactory: NotificationsViewModelFactory by instance()
    lateinit var mAllActivityAdapter: NotificationsAllActivityAdapter
    lateinit var mAllSuggestionsAdapter: NotificationsSuggestionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()

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
        getNotifications(true)

        return mBinding.root

    }

    private fun setupViewModel() {
        mViewModel =
            ViewModelProvider(this, viewModelFactory).get(NotificationsViewModel::class.java)

    }


    inner class ClickHandler {


        //like/comment/follow
        fun onClickLike() {

            findNavController().navigate(
                R.id.particularNotificationFragment,
                bundleOf(getString(R.string.title) to getString(R.string.likes))
            )

        }

        fun onClickComments() {
            findNavController().navigate(
                R.id.particularNotificationFragment,
                bundleOf(getString(R.string.title) to getString(R.string.comments))
            )

        }

        fun onClickFollowers() {
            findNavController().navigate(
                R.id.particularNotificationFragment,
                bundleOf(getString(R.string.title) to getString(R.string.followers))
            )

        }

        fun onClickShare() {
            findNavController().navigate(
                R.id.particularNotificationFragment,
                bundleOf(getString(R.string.title) to getString(R.string.share))
            )

        }


    }


    private fun getNotifications(showProgress: Boolean) = launch {
        if (showProgress) commonCallbacks?.showProgressDialog()
        mViewModel.getNotifications("all").observe(this@AllNotificationsFragment, Observer {
            if (it.error == null) {

                //Activities
                val activities = it.data?.activity
                if (activities != null && activities.isNotEmpty()) {
                    mBinding.textAllActivity.visibility = View.VISIBLE
                    mBinding.lineAllActivity.visibility = View.VISIBLE
                    mBinding.lNoDataFound.visibility = View.GONE
                    mBinding.allActivityRecyclerView.visibility = View.VISIBLE
                    mAllActivityAdapter.addNewItems(activities)


                } else {
                    mBinding.textAllActivity.visibility = View.GONE
                    mBinding.lineAllActivity.visibility = View.VISIBLE
                    mBinding.lNoDataFound.visibility = View.VISIBLE
                    mBinding.allActivityRecyclerView.visibility = View.GONE

                }

                //Suggestions
                val suggestions = it.data?.suggestions
                if (suggestions != null && suggestions.isNotEmpty()) {
                    mBinding.textSuggested.visibility = View.VISIBLE
                    mBinding.suggestedRecyclerView.visibility = View.VISIBLE
                    mAllSuggestionsAdapter.addNewItems(suggestions)
                    if (showProgress) commonCallbacks?.hideProgressDialog()


                } else {
                    if (showProgress) commonCallbacks?.hideProgressDialog()

                    mBinding.suggestedRecyclerView.visibility = View.GONE


                }


            } else {
                if (showProgress) commonCallbacks?.hideProgressDialog()


                handleAPIError(it.error!!.message.toString())
            }
        })
    }

    private fun followUser(showProgress: Boolean, id: String?, position: Int, boolean: Boolean) =
        launch {
            if (showProgress) commonCallbacks?.showProgressDialog()
            val user = User()
            user.userId = id

            mViewModel.followUser(user).observe(this@AllNotificationsFragment, Observer {
                if (showProgress) commonCallbacks?.hideProgressDialog()

                if (it.error == null) {
                    mAllSuggestionsAdapter.list[position].noOfFollowers=it.data?.noOfFollowers?.toInt()
                    mAllSuggestionsAdapter.list[position].followedByMe = !boolean
                    mAllSuggestionsAdapter.notifyItemChanged(position)
                } else {

                    handleAPIError(it.error!!.message.toString())
                }
            })
        }


    private fun setupRecycler() {


        mAllActivityAdapter =
            NotificationsAllActivityAdapter(R.layout.notifications_item, "", requireContext())
        mAllSuggestionsAdapter = NotificationsSuggestionsAdapter(
            R.layout.suggestion_item,
            requireContext(),
            resourceProvider
        )
        val face1 = resourceProvider.getFont("fonts/gotham_pro.ttf")
        val face2 = resourceProvider.getFont("fonts/gothampro_medium.ttf")
        mAllSuggestionsAdapter.updateTypeface(face1,face2)


        mAllSuggestionsAdapter.click = this
        mAllActivityAdapter.click = this

        //Following Recycler View
        mBinding.allActivityRecyclerView.layoutManager = LinearLayoutManager(context)
        mBinding.allActivityRecyclerView.adapter = mAllActivityAdapter

        //Popular Recycler View
        mBinding.suggestedRecyclerView.layoutManager = LinearLayoutManager(context)
        mBinding.suggestedRecyclerView.adapter = mAllSuggestionsAdapter
        mBinding.suggestedRecyclerView.itemAnimator?.changeDuration = 0


    }

    override fun onFollowClick(position: Int, id: String?, isFollowed: Boolean) {

        if (isNetworkConnected()!!) {
            followUser(true, id, position, isFollowed)

        } else {
            Validator.showMessage(getString(R.string.no_internet))
        }
    }

    override fun openScreen(id: String, categoryId: String?) {
        findNavController().navigate(
            R.id.OthersProfileFragment,
            bundleOf(ParcelKeys.OTHER_USER_ID to id)
        )
    }

}

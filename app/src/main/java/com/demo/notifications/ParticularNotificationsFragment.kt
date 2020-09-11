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
import com.demo.databinding.FragmentParticularNotificationsBinding
import com.demo.model.response.baseResponse.User
import com.demo.providers.resources.ResourcesProvider
import com.demo.providers.resources.ResourcesProviderImpl
import com.demo.util.EndlessRecyclerViewScrollListener
import com.demo.util.ParcelKeys
import com.demo.util.Validator
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class ParticularNotificationsFragment : BaseFragment(), KodeinAware,
    NotificationsSuggestionsAdapter.OnItemClickPosts {
    override val kodein: Kodein by closestKodein()
    lateinit var mBinding: FragmentParticularNotificationsBinding
    lateinit var mViewModel: NotificationsViewModel
    val resourceProvider: ResourcesProvider by instance()
    private val viewModelFactory: NotificationsViewModelFactory by instance()
    lateinit var mAllActivityAdapter: NotificationsAllActivityAdapter
    lateinit var mAllSuggestionsAdapter: NotificationsSuggestionsAdapter
    lateinit var llm: LinearLayoutManager
    var mEndlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null
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

        mBinding.title.text = arguments?.getString(getString(R.string.title))
        setupRecycler()
        getNotifications()



        return mBinding.root

    }

    fun getNotifications() {
        val argument= arguments?.getString(R.string.title.toString())
        if (argument.equals(getString(R.string.likes))) {
            title = getString(R.string.liked)

        } else if (argument.equals(getString(R.string.comments))) {
            title = getString(R.string.commented)

        } else if (argument.equals(getString(R.string.followers))) {
            title = getString(R.string.followed)

        } else {
            title = getString(R.string.shared)

        }
        getNotifications(true, title)

    }

    private fun setupViewModel() {
        mViewModel =
            ViewModelProvider(this, viewModelFactory).get(NotificationsViewModel::class.java)

    }

    inner class ClickHandler {


        fun onBackPressed() {
            onFragBack()
        }


    }

    private fun resetList() {
        mEndlessRecyclerViewScrollListener?.resetState()
    }

    private fun getNotifications(showProgress: Boolean, type: String?) = launch {
        if (showProgress) commonCallbacks?.showProgressDialog()
        if (type != null) {
            mViewModel.getNotifications(type)
                .observe(this@ParticularNotificationsFragment, Observer {
                    if (it.error == null) {

                        //Activities
                        val activities = it.data?.activity
                        if (activities != null && activities.isNotEmpty()) {
                            mBinding.lNoDataFound.visibility = View.GONE
                            mBinding.allActivityRecyclerView.visibility = View.VISIBLE
                            mBinding.lineAllActivity.visibility = View.VISIBLE
                            mAllActivityAdapter.addNewItems(activities)


                        } else {
                            mBinding.lNoDataFound.visibility = View.VISIBLE
                            mBinding.lineAllActivity.visibility = View.VISIBLE

                            mBinding.allActivityRecyclerView.visibility = View.GONE

                        }

                        //Suggestions
                        val suggestions = it.data?.suggestions
                        if (suggestions != null && suggestions.isNotEmpty()) {
                            mBinding.textSuggested.visibility = View.VISIBLE
                            mBinding.suggestedRecyclerView.visibility = View.VISIBLE
                            mAllSuggestionsAdapter.addNewItems(suggestions)


                        } else {
                            mBinding.textSuggested.visibility = View.GONE

                            mBinding.suggestedRecyclerView.visibility = View.GONE


                        }

                        if (showProgress) commonCallbacks?.hideProgressDialog()


                    } else {
                        if (showProgress) commonCallbacks?.hideProgressDialog()


                        handleAPIError(it.error!!.message.toString())
                    }
                })
        }
    }


    private fun setupRecycler() {


        mAllActivityAdapter = NotificationsAllActivityAdapter(
            R.layout.notifications_item,
            mBinding.title.text.toString(),
            requireContext()
        )
        mAllSuggestionsAdapter = NotificationsSuggestionsAdapter(R.layout.suggestion_item,requireContext(),resourceProvider)
        mAllSuggestionsAdapter.click = this
        mAllActivityAdapter.click = this


        val face1 = resourceProvider.getFont("fonts/gotham_pro.ttf")
        val face2 = resourceProvider.getFont("fonts/gothampro_medium.ttf")
        mAllSuggestionsAdapter.updateTypeface(face1,face2)



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

    private fun followUser(showProgress: Boolean, id: String?, position: Int, boolean: Boolean) =
        launch {
            if (showProgress) commonCallbacks?.showProgressDialog()
            val user = User()
            user.userId = id

            mViewModel.followUser(user).observe(this@ParticularNotificationsFragment, Observer {
                if (showProgress) commonCallbacks?.hideProgressDialog()

                if (it.error == null) {
                    mAllSuggestionsAdapter.list[position].noOfFollowers=it.data?.noOfFollowers?.toInt()
                    mAllSuggestionsAdapter.list.get(position).followedByMe = !boolean
                    mAllSuggestionsAdapter.notifyItemChanged(position)


                } else {

                    handleAPIError(it.error!!.message.toString())
                }
            })
        }


}

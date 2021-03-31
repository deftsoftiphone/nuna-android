package com.demo.postList


import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.demo.R
import com.demo.base.AsyncViewController
import com.demo.base.BaseFragment
import com.demo.base.MyViewModelProvider
import com.demo.databinding.FragmentPostListBinding
import com.demo.followPopularUsers.postList.PostAdapter
import com.demo.model.response.Post
import com.demo.util.EndlessRecyclerViewScrollListener
import com.demo.util.ParcelKeys
import com.demo.webservice.ApiRegister


class PostListFragment : BaseFragment() {

    lateinit var mBinding: FragmentPostListBinding
    lateinit var mViewModel: PostsViewModel
    var mEndlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null
    val mClickHandler: ClickHandler by lazy { ClickHandler() }
    lateinit var context: FragmentActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        setupObserver()
        mViewModel.parseBundle(arguments)
        mViewModel.loadFirstPage()
        context=activity as FragmentActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::mBinding.isInitialized) {
            mBinding = FragmentPostListBinding.inflate(inflater, container, false).apply {
                lifecycleOwner = this@PostListFragment
                viewModel = mViewModel
                //clickHandler = ClickHandler()
            }

            mViewModel.loadFirstPage()
            setupRecycler()
        }
        return mBinding.root

    }

    private fun setupViewModel() {
        mViewModel =
            ViewModelProviders.of(this, MyViewModelProvider(commonCallbacks as AsyncViewController))
                .get(PostsViewModel::class.java)
    }

    fun getAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>? {
        return (mBinding.recyclerPosts.adapter)
    }

    private fun setupObserver() {
        mViewModel.responseGetPostList_NEW.observe(this, Observer {
            if (it.data != null) {
//                (getAdapter() as? MiniPostAdapter)?.setNewItems(it.data as List<Post>)
                (getAdapter() as? PostAdapter)?.setNewItems(it.data as List<Post>)


            } else {
//                (getAdapter() as? MiniPostAdapter)?.clearData()
                (getAdapter() as? PostAdapter)?.clearData()
            }
        })

        mViewModel.responseGetPostList_PAGING.observe(this, Observer {
            if (it.data?.size != 0) {
//                (getAdapter() as? MiniPostAdapter)?.addNewItems(it.data as List<Post>)
                (getAdapter() as? PostAdapter)?.addNewItems(it.data as List<Post>)
            }else{
//                (getAdapter() as? MiniPostAdapter)?.clearData()
                (getAdapter() as? PostAdapter)?.clearData()
            }
        })


        mViewModel.responseActionOnPost.observe(this, Observer {
            if (it.data != null) {
                (getAdapter() as? PostAdapter)?.clearData()
                mViewModel.loadFirstPage()
            }
        })
    }

    override fun onApiRequestFailed(apiUrl: String, errCode: Int, errorMessage: String): Boolean {
        if (apiUrl == ApiRegister.GETPOSTLIST) {
            return true
        } else return super.onApiRequestFailed(apiUrl, errCode, errorMessage)
    }

    private fun setupRecycler() {
        when (mViewModel.layoutType) {
            PostsViewModel.LAYOUT_TYPE_HORIZONTAL -> {

            /*    mBinding.recyclerPosts.adapter =
                    MiniPostAdapter(R.layout.layout_post_mini_item).apply {
                        addClickEventWithView(R.id.card_parent, mClickHandler::onClickPost)
                    }*/

                mBinding.recyclerPosts.apply {
                    layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                }
                mBinding.recyclerPosts.setItemAnimator(null)

                setScrollListenerForLLM()
            }

            PostsViewModel.LAYOUT_TYPE_VERTICAL -> {

                mBinding.recyclerPosts.adapter =
                    PostAdapter(R.layout.layout_dashboard_post_item).apply {
                        addClickEventWithView(R.id.card_parent, mClickHandler::onClickPost)
                        addClickEventWithView(R.id.iv_owner, mClickHandler::onClickPostOwnerProfile)
                        addClickEventWithView(R.id.imv_delete, mClickHandler::onClickPostDelete)
                        addClickEventWithView(R.id.imv_edit, mClickHandler::onClickEditPost)
                        addClickEventWithView(
                            R.id.imv_edit,
                            mClickHandler::onClickEditPost
                        )
                    }

                mBinding.recyclerPosts.apply {
                    layoutManager = StaggeredGridLayoutManager(
                        2,
                        StaggeredGridLayoutManager.VERTICAL
                    )
                }

                setScrollListenerForSM()
            }
        }
    }

    private fun resetList() {
        mEndlessRecyclerViewScrollListener?.resetState()
    }

    private fun setScrollListenerForLLM() {
        mEndlessRecyclerViewScrollListener =
            object :
                EndlessRecyclerViewScrollListener(mBinding.recyclerPosts.layoutManager as LinearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    mViewModel.loadNextPage()
                }
            }

        mBinding.recyclerPosts.addOnScrollListener(mEndlessRecyclerViewScrollListener!!)
    }


    private fun setScrollListenerForSM() {
        mEndlessRecyclerViewScrollListener =
            object :
                EndlessRecyclerViewScrollListener(mBinding.recyclerPosts.layoutManager as StaggeredGridLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    mViewModel.loadNextPage()
                }
            }

        mBinding.recyclerPosts.addOnScrollListener(mEndlessRecyclerViewScrollListener!!)
    }


    inner class ClickHandler {
        fun onClickPost(pos: Int, clickedPost: Post) {
            findNavController().navigate(
                R.id.PostDetailsFragment,
                bundleOf(ParcelKeys.PK_POST_ID to clickedPost.postId)
            )
        }

        fun onClickPostOwnerProfile(position: Int, clickedItem: Post) {
            navigate(R.id.EditProfileFragment, ParcelKeys.PK_PROFILE_ID to clickedItem.user.userId)
        }

        fun onClickPostDelete(position: Int, clickedPost: Post) {
            commonCallbacks!!.showAlertDialog(
                "",
                getString(R.string.delete_post),
                getString(R.string.yes),
                getString(R.string.no),
                DialogInterface.OnClickListener { _, which ->
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        mViewModel.deletePost(clickedPost.postId)
                    }
                })
        }



        fun onClickEditPost(position: Int, clickedItem: Post) {
            findNavController().navigate(
                R.id.EditPostFragment,
                bundleOf(ParcelKeys.PK_POST_ID to clickedItem.postId,Post::class.java.name to clickedItem)
            )
        }
    }
}

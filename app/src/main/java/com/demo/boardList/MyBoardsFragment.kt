package com.demo.boardList


import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.demo.R
import com.demo.base.AsyncViewController
import com.demo.base.BaseFragment
import com.demo.base.MyViewModelProvider
import com.demo.databinding.FragmentMyBoardsBinding
import com.demo.databinding.LayoutAddToBoardDialogBinding
import com.demo.databinding.LayoutBoardOptionsBinding
import com.demo.model.Pinboard
import com.demo.model.response.Post
import com.demo.util.ParcelKeys
import com.demo.webservice.ApiRegister
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout


class MyBoardsFragment : BaseFragment() {

    lateinit var mBinding: FragmentMyBoardsBinding
    lateinit var mViewModel: MyBoardsViewModel
    var mAdapter: MyBoardsAdapter? = null
    var selectedPinboard: Pinboard? = null
    var addNewBoardNameDialog: Dialog? = null

    val mClickHandler by lazy { ClickHandler() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentMyBoardsBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@MyBoardsFragment
        }
        mViewModel =
            ViewModelProviders.of(
                this,
                MyViewModelProvider(callback = commonCallbacks as AsyncViewController)
            )
                .get(MyBoardsViewModel::class.java)
        setupObserver()
        mViewModel.callGetUserBoardList(null)
        setupRecycler()
        return mBinding.root
    }

    private fun setupObserver() {
        mViewModel.responseGetUserBoards.observe(this, Observer {
            if (it.data != null) {
                feedData(it.data!!)
            }
        })

        mViewModel.responseEditPinboard.observe(this, Observer {
            if (it.data != null) {
                val newBoard = it.data!!
                mAdapter?.updateBoardName(newBoard.pinboardName, newBoard.pinboardId)
            }
        })

        mViewModel.responseDeletePinboard.observe(this, Observer {
            mAdapter?.removeItem { selectedPinboard!!.pinboardId == it.pinboardId }
        })

        mViewModel.responseRemovePostFromPinboard.observe(this, Observer {
            mViewModel.lastRequestRemovePinboardPost?.apply {
                mAdapter?.removePostFromPinboard(postId, pinboardId)
            }

        })


        mViewModel.responseLikeUnLikePost.observe(this, Observer {
            /*if (it.data != null) {
                val newValue = it.data?.like!!
                var post = mAdapter.getItem(mViewModel.position)
                post.postLiked = newValue
                post.likeCount = if (newValue) post.likeCount + 1 else post.likeCount - 1
                mAdapter.updatePost(post, mViewModel.position)
            }*/
        })
    }

    override fun onApiRequestFailed(apiUrl: String, errCode: Int, errorMessage: String): Boolean {
        if (apiUrl == ApiRegister.GET_USER_PINBOARD_LIST && ::mViewModel.isInitialized) {
            mViewModel.errNoData.value = true
            return true
        } else
            return super.onApiRequestFailed(apiUrl, errCode, errorMessage)
    }

    private fun setupRecycler() {
        mAdapter = MyBoardsAdapter(R.layout.layout_my_board_item, mClickHandler)
        mAdapter?.addClickEventWithView(R.id.iv_options, mClickHandler::onClickBoardOptions)
        mBinding.recyclerBoards.adapter = mAdapter
    }

    private fun feedData(list: List<Pinboard>) {
        mAdapter?.setNewItems(list)
    }

    fun addNewBoard(newboard: Pinboard) {
        mAdapter?.addNewItems(listOf(newboard), 0)
    }

    inner class ClickHandler {
        fun onClickBoardPost(pos: Int, clickedPost: Post) {
            findNavController().navigate(
                R.id.PostDetailsFragment,
                bundleOf(ParcelKeys.PK_POST_ID to clickedPost.postId)
            )
        }

        fun onClickEditOptionForBoard(dialog: BottomSheetDialog) {
            dialog.dismiss()
            val builder = AlertDialog.Builder(requireContext())
            LayoutAddToBoardDialogBinding.inflate(layoutInflater).apply {
                pinboardName = selectedPinboard!!.pinboardName

                clickHandler = View.OnClickListener {
                    var input = ""
                    if (it.id == R.id.button_cross) {
                        addNewBoardNameDialog?.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                        addNewBoardNameDialog?.dismiss()
                        return@OnClickListener
                    }
                    val til =
                        addNewBoardNameDialog?.findViewById<TextInputLayout>(R.id.til_name)?.apply {
                            input = editText?.text.toString()
                        }

                    if (input.isBlank()) {
                        til?.error = getString(R.string.err_board_name_missing)
                        return@OnClickListener
                    }

                    til?.error = ""
                    commonCallbacks?.hideKeyboard()
                    addNewBoardNameDialog?.dismiss()
                    mViewModel.callEditPinboardNameApi(selectedPinboard!!.pinboardId, input)

                }
                builder.setView(this.root)

            }
            addNewBoardNameDialog?.setCancelable(false)
            builder.setCancelable(false)
            addNewBoardNameDialog = builder.create()
            addNewBoardNameDialog?.show()

        }

        fun onClickDeleteOptionForBoard(dialog: BottomSheetDialog) {
            dialog.dismiss()

            commonCallbacks!!.showAlertDialog(
               "",
                getString(R.string.msg_delete_board, selectedPinboard!!.pinboardName),
                getString(R.string.yes),
                getString(R.string.no),
                DialogInterface.OnClickListener { _, which ->
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        mViewModel.callDeletePinboardApi(selectedPinboard!!.pinboardId)
                    }
                })

        }

        fun onClickBoardOptions(pos: Int, clickedBoard: Pinboard) {

            val dialog = BottomSheetDialog(requireContext())
            selectedPinboard = clickedBoard
            val binding =
                LayoutBoardOptionsBinding.inflate(LayoutInflater.from(requireContext())).apply {
                    this.dialog = dialog
                    this.clickHandler = mClickHandler
                }
            dialog.setContentView(binding.root)
            dialog.show()
        }


        fun onFavClick(position: Int, clickedItem: Post) {

            mViewModel.position = position
            mViewModel.callLikeUnlikePostApi(
                clickedItem.postId,
                !clickedItem.postLiked
            )

            Handler().postDelayed({

            }, 1000)


        }



        fun onClickDeleteBoardPost(
            pos: Int,
            clickedPost: Post,
            positionInParentAdapter: Int,
            correspondingBoard: Pinboard
        ) {
            commonCallbacks!!.showAlertDialog(
               "",
                getString(R.string.msg_delete_post_from_board),
                getString(R.string.yes),
                getString(R.string.no),
                DialogInterface.OnClickListener { _, which ->
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        mViewModel.callRemovePostFromBoardApi(
                            clickedPost.postId,
                            correspondingBoard.pinboardId
                        )
                    }
                })
        }
    }
}

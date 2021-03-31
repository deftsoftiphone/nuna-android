package com.demo.commentList

//import org.jcodec.common.logging.Logger.debug
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.R
import com.demo.base.AsyncViewController
import com.demo.base.BaseFragment
import com.demo.base.MyViewModelProvider
import com.demo.databinding.FragmentCommentListBinding
import com.demo.model.response.UserComment
import com.demo.post_details.UserCommentAdapter
import com.demo.util.AudioRecordView
import com.demo.util.ParcelKeys
import com.demo.util.Prefs
import com.demo.util.Validator
import com.google.gson.Gson
import kotlinx.android.synthetic.main.layout_item_language.*
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class CommentsFragment : BaseFragment(),
    AudioRecordView.RecordingListener
//    , AwsUploadImageListener
{
 /*   override fun onUploadProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
    }

    override fun onImageUploadSuccess() {
        Log.d("onImageUpload  ", "Success")
    }

    override fun onImageUploadFail(error: String) {
        Log.d("onImageUploadFail ", error)
    }*/

    var valuepermisstion: Boolean = false
    lateinit var recorder: MediaRecorder

    val TAG = "Comment"
    var audioPlayerName: String? = ""
    private var isRecording = false
    private var millis: Long? = null
    private var root: String? = null
    private var time: Long = 0

    private fun hasMyPermission(): Boolean {
        return (context?.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && context?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && context?.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)

        /*  return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || context?.checkSelfPermission(
              Manifest.permission.RECORD_AUDIO
          ) == PackageManager.PERMISSION_GRANTED*/
    }

    private fun askForAudioPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            VOICE_PERMISSION_REQUEST_CODE
        )
        /* if (!hasVoicePermission() || !hasStoragePermission()) //{/ if(!hasAudioPermission())
          {

          }
  */
    }

    override fun onRecordingStarted() {

        if (!hasMyPermission()) {
            valuepermisstion = false
            askForAudioPermission() // askForVoicePermission()
            onRecordingCanceled()

        } else {
            valuepermisstion = true
            val imm =
                requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(mBinding.recordingView.messageView.windowToken, 0)

            // showToast("started");
//            debug("started")

            time = System.currentTimeMillis() / 1000
            startRecording()
        }
    }

    private fun startRecording() {
        recorder = MediaRecorder()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        // ACC_ELD is supported only from SDK 16+.
        // You can use other encoders for lower vesions.
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        millis = Calendar.getInstance().timeInMillis
        audioPlayerName = root + "/" + millis + "audio.mp4"

        recorder.setOutputFile(audioPlayerName)
        try {
            recorder.prepare()
            recorder.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun stopRecording() {
        if (recorder != null) {
            recorder.release()
            isRecording = false
        }
    }


    override fun onRecordingLocked() {
    }


    override fun onRecordingCompleted() {
//        if (valuepermisstion) {
//            mBinding.recordingView.messageView.requestFocus()
//            debug("completed")
//            stopRecording()
//
//            if (!mBinding.recordingView.timeTextView.text.toString().equals("0:00")) {
//
//                val fileName =
//                    AmazonUtil.S3_MEDIA_AUDIO + audioPlayerName?.let { getFileName(it) }
//
//
//
//                audioPlayerName?.let { AmazonUtil.uploadFile(it, fileName, this) }
//
//                mViewModel.uploadingAudioUrl = Constant.AMZ_BASE_URL + fileName
//
//                mViewModel.requestSaveComment.set(
//                    RequestSaveComment(
//                        User(Prefs.init().loginData!!.userId),
//                        0,
//                        comment.commentId,
//                        comment.postId,
//                        mViewModel.uploadingAudioUrl,
//                        true
//                    )
//                )
//                mViewModel.callSaveCommentApi()
//            }
//        }
    }

    fun getFileName(path: String): String {
        return System.currentTimeMillis().toString() + path.substring(path.lastIndexOf("."))
    }

    override fun onRecordingCanceled() {
        if (valuepermisstion) {
            mBinding.recordingView.messageView.requestFocus()

            if (recorder != null) {
                recorder.release()
                isRecording = false
            }
        }
        if (hasMyPermission())
            Validator.showCustomToast(getString(R.string.cancelled))
    }

    val mViewModel by viewModels<CommentsViewModel> { MyViewModelProvider(commonCallbacks as AsyncViewController) }
    val mAdapterFragmentBridge: AdapterFragmentBridge by lazy { AdapterFragmentBridge() }
    lateinit var mBinding: FragmentCommentListBinding
    lateinit var mAdapter: UserCommentAdapter
    lateinit var comment: UserComment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupObserver()
        createDirectoriesIfNeeded()
        millis = Calendar.getInstance().timeInMillis
        audioPlayerName = root + "/" + millis + "audio.mp4"
        if (arguments != null) {
            comment = requireArguments().getSerializable(UserComment::class.java.name) as UserComment
        }
    }

    var dataRefreshCallback: (() -> Unit)? = null

    private fun setupObserver() {
        mViewModel.responseLikeUnLikeComment.observe(this, Observer lit@{
            if (it.data != null) {
                mViewModel.commentsData[mViewModel.position].commentLiked = it.data!!.like!!
                mViewModel.commentsData[mViewModel.position].likeCount =
                    if (mViewModel.commentsData[mViewModel.position].commentLiked) mViewModel.commentsData[mViewModel.position].likeCount!! + 1
                    else mViewModel.commentsData[mViewModel.position].likeCount!! - 1
                mAdapter.changeData(
                    mViewModel.commentsData[mViewModel.position],
                    mViewModel.position
                )
            }
        })
        mViewModel.responseLikeUnLikeReply.observe(this, Observer {
            if (mViewModel.commentsData[mViewModel.position].childComment != null) {
                for ((index, comment) in mViewModel.commentsData[mViewModel.position].childComment!!.withIndex()) {
                    if (comment.commentId == mViewModel.reply.commentId) {
                        comment.commentLiked = it.data!!.like!!
                        if (it.data!!.like!!)
                            comment.likeCount = comment.likeCount?.plus(1)
                        else comment.likeCount = comment.likeCount?.minus(1)
                    }
                }
                mAdapter.changeData(
                    mViewModel.commentsData[mViewModel.position],
                    mViewModel.position
                )
            }
        })

        mViewModel.responseSaveComment.observe(this, Observer {
            if (it.data != null) {
                val commentReply = Gson().fromJson(Gson().toJson(it.data), UserComment::class.java)
                val json = Gson().toJson(Prefs.init().loginData)
                commentReply.user =
                    Gson().fromJson(json, com.demo.model.response.User::class.java)
                if (comment.childComment == null) {
                    comment.childComment = ArrayList()
                    comment.childComment?.add(commentReply)
                } else
                    comment.childComment?.add(0, commentReply)
//increase reply count
                if (comment.replyCount == null)
                    comment.replyCount = 1
                else
                    comment.replyCount = comment.replyCount?.plus(1)
                mAdapter.changeData(userComment = comment, position = 0)
            }
        })
        mViewModel.responseGetCommentsList.observe(this, Observer {
            if (it.data != null)
                if (it.data?.size ?: 0 > 0) {
                    childFragmentManager.findFragmentById(R.id.fragment_comments)?.apply {
                        mBinding.invalidateAll()
                        (this as CommentsFragment).feedCommentsData(it!!.data!!)
//                        if (it.data != null)
//                            getReply(it.data!!, mViewModel.position++)
                    }
                }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentCommentListBinding.inflate(inflater, parent, false).let {
            mBinding = it
            setupRecycler(ArrayList(), true)
            it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniUi()
        if (::comment.isInitialized) {
            val data = ArrayList<UserComment>()
            data.add(comment)
            setupRecycler(data, false)
            mBinding.recordingView.visibility = View.VISIBLE
            mAdapter.isReply(true)

        }
    }

    fun feedCommentsData(data: List<UserComment>) {
        mViewModel.commentsData = data
        mAdapter.setNewData(mViewModel.commentsData)
    }

    private fun setupRecycler(list: List<UserComment>, limitedElements: Boolean) {
        mViewModel.areLimitedEntries = limitedElements
        mViewModel.commentsData = list
        mAdapter = UserCommentAdapter(requireContext(), mAdapterFragmentBridge, mViewModel)
        mAdapter.setNewData(mViewModel.commentsData)
        mBinding.recyclerComments.adapter = mAdapter
        mBinding.recyclerComments.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    fun toggleVisibility(showAll: Boolean) {
        mViewModel.areLimitedEntries = !showAll
        mAdapter.refresh()
    }

    fun feedCommentsReplyData(data: List<UserComment>, position: Int?) {
        mViewModel.commentsData[position!!].childComment = ArrayList(data)
        mAdapter.changeData(mViewModel.commentsData[position], position)
    }

    inner class AdapterFragmentBridge {
        fun onClickOwnerProfile(position: Int?) {

            findNavController().navigate(
                R.id.OthersProfileFragment,
                bundleOf(ParcelKeys.PK_PROFILE_ID to position)
            )
        }

        fun onClickUpdateList() {
            dataRefreshCallback?.invoke()
        }

        fun onClickReply(comment: UserComment, position: Int?) {
            /* if (!hasMyPermission())
             {  // valuepermisstion=false
                 askForAudioPermission() // askForVoicePermission()
                 // onRecordingCanceled()

             }else {*/


            findNavController().navigate(
                R.id.UserCommentFragment,
                bundleOf(
                    ParcelKeys.PK_PROFILE_ID to position,
                    UserComment::class.java.name to comment
                )
            )

        }
    }


    private fun iniUi() {
        /* if (!hasVoicePermission()) {
            askForVoicePermission()
        }
        if (hasExternalReadWritePermission()) {
            val external = File(Environment.getExternalStorageDirectory(), "Recorder")
            if (!external.exists()) external.mkdir()

            //record_view.setAudioDirectory(external)
        } else {
            askForStoragePermission()
        }*/
        mBinding.recordingView.recordingListener = this
        mBinding.recordingView.sendView.setOnClickListener {
            /**
             * Comment send
             */
            val messageText = getTextInput()
            if (messageText.trim { it <= ' ' }.length == 0) {
                return@setOnClickListener
            }

            mViewModel.requestSaveComment.get()!!.apply {
                user?.userId = Prefs.init().loginData!!.userId
                isUrl = false
                commentId = 0
                parentCommentId = comment.commentId
                postId = comment.postId
                commentText = mBinding.recordingView.getMessageView().getText().toString().trim()

            }
            mViewModel.callSaveCommentApi()
            clearTextInput()
        }
        mBinding.recordingView.messageView.setOnEditorActionListener { v, actionId, event -> false }
        mBinding.recordingView.attachmentView.setOnClickListener {
            /**
             * image save
             */
        }
    }

    private fun getTextInput(): String {
        return mBinding.recordingView.messageView.text.toString()
    }

    private fun clearTextInput() {
        mBinding.recordingView.messageView.setText("")
    }

    /*  override fun askForVoicePermission() {
          ActivityCompat.requestPermissions(
              requireActivity(),
              arrayOf(android.Manifest.permission.RECORD_AUDIO),
              VOICE_PERMISSION_REQUEST_CODE
          )
      }
  */
    private fun askForStoragePermission() {
        if (!hasExternalReadWritePermission()) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ), STORAGE_PERMISSION_REQUEST_CODE
            )
        }
    }

    val VOICE_PERMISSION_REQUEST_CODE = 100
    val STORAGE_PERMISSION_REQUEST_CODE = 101
    private fun hasExternalReadWritePermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || activity?.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                || activity?.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasVoicePermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || context?.checkSelfPermission(
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun createDirectoriesIfNeeded() {
        root = Environment.getExternalStorageDirectory().absolutePath
        val folder = File(
            root,
            "AudioRecord"
        )
        if (!folder.exists()) {
            folder.mkdir()
        }
        val audioFolder = File(folder.getAbsolutePath(), "Audio")
        if (!audioFolder.exists()) {
            audioFolder.mkdir()
        }
        root = audioFolder.getAbsolutePath()
    }

    override fun onPause() {
        super.onPause()
        onMyPause()
    }


    fun onMyPause() {
        if (mAdapter != null) {
            if (mAdapter.player != null) {
                mAdapter.player.pause()
                if (mAdapter.audioView != null)
                    mAdapter.audioView!!.btnPlayPause.setImageResource(R.drawable.play_button)

            }

            //    mAdapter.player.isPlaying = false;
            // mAdapter.onMediaFinished();
        }
    }

}


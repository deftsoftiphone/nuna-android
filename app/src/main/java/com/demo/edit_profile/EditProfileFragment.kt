package com.demo.edit_profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.demo.R
import com.demo.base.BaseFragment
import com.demo.databinding.ActivityEditProfileBinding
import com.demo.model.request.RequestSavePost
import com.demo.model.response.baseResponse.ProfilePicture
import com.demo.model.response.baseResponse.UserProfile
import com.demo.util.Prefs
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.io.File


class EditProfileFragment : BaseFragment(), KodeinAware {
    lateinit var mBinding: ActivityEditProfileBinding
    override val kodein: Kodein by closestKodein()
    private val viewModelFactory: EditProfileViewModelFactory by instance()
    var user: UserProfile? = null
    lateinit var mViewModel: EditProfileViewModel
    val mClickHandler by lazy { ClickHandler() }

    companion object {
        var user: UserProfile? = null
        private const val PROFILE_IMAGE_REQ_CODE = 101

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
        mBinding = ActivityEditProfileBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@EditProfileFragment
            viewModel = mViewModel
            clickHandler = ClickHandler()
        }

        return mBinding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        user = Prefs.init().profileData
        if (user != null) {
            if (user?.bioData.isNullOrEmpty()) {
                user?.bioData = null
            }
            mBinding.userProfile = user
            mBinding.userImage = user?.profilePicture?.mediaurl
            Log.e("userpro", "p${mBinding.userImage}")
        } else {
            user = UserProfile()
            user!!.profilePicture = ProfilePicture()
        }
    }

    private fun setupViewModel() {
        mViewModel =
            ViewModelProvider(this, viewModelFactory).get(EditProfileViewModel::class.java)
    }


    inner class ClickHandler {
        fun onClickChangePhoto() {
            selectPhoto()
        }


        fun onClickAddName() {
            val bundle = bundleOf(
                getString(R.string.type) to getString(R.string.name),
                getString(R.string.value) to (user?.fullName ?: getString(R.string.full_name))
            )
            findNavController().navigate(R.id.EditProfileEntityFragment, bundle)
        }

        fun onClickAddUsername() {
            val bundle = bundleOf(
                getString(R.string.type) to getString(R.string.user_name),
                getString(R.string.value) to (user?.userName ?: getString(R.string.user_name))
            )
            findNavController().navigate(R.id.EditProfileEntityFragment, bundle)
        }

        fun onClickAddBio() {
            val bundle = bundleOf(
                getString(R.string.type) to getString(R.string.bio),
                getString(R.string.value) to (user?.bioData ?: getString(R.string.add_your_bio))
            )
            findNavController().navigate(R.id.EditProfileEntityFragment, bundle)
        }

        fun onBackPressed() {
            onFragBack()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            // File object will not be null for RESULT_OK
            val file = ImagePicker.getFile(data)

            Log.e("TAG", "Path:${file?.absolutePath}")
            when (requestCode) {
                PROFILE_IMAGE_REQ_CODE -> {

                    if (file != null) {
                        updateProfilePic(true, file)
                    }


                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        }


    }


    fun selectPhoto() {
        ImagePicker.with(requireActivity())
            .cropSquare() // Crop Square image(Optional)
            .maxResultSize(
                620,
                620
            ) // Final image resolution will be less than 620 x 620(Optional)
            .start(PROFILE_IMAGE_REQ_CODE)
    }

    private fun updateProfilePic(showProgress: Boolean, file: File) = launch {

        val requestSavePost = RequestSavePost()

        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("profileImage", file.getName(), requestFile)

        requestSavePost.profileImage = body
        if (showProgress) commonCallbacks?.showProgressDialog()
        mViewModel.updateProfilePic(requestSavePost)
            .observe(this@EditProfileFragment, Observer {
                if (showProgress) commonCallbacks?.hideProgressDialog()
                if (it.error == null) {
                    user?.profilePicture = ProfilePicture(mediaurl = it.data?.profileUrl)
                    Prefs.init().profileData = user

                    Log.e("MediAurlPrefs", "P${Prefs.init().profileData!!.profilePicture!!.mediaurl}")

                    val glide = Glide.with(requireActivity()).load(file)
                    glide.apply(RequestOptions.circleCropTransform())
                        .into(mBinding.imageView2)

                }
            })


    }

}

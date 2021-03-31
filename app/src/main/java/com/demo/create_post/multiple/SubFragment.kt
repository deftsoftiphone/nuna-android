package com.demo.create_post.multiple

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.R
import com.demo.base.BaseFragment
import com.demo.photoeditor.EditImageActivity
//import com.sangcomz.fishbun.define.Define
import kotlinx.android.synthetic.main.fragment_sub.*
import java.io.File
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class SubFragment : BaseFragment() {
    val EDITIMAGELIST =123

    var path = ArrayList<Uri>()
   public var pos=0
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_sub, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(recyclerview) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            imageAdapter = ImageAdapter(this@SubFragment, ImageController(img_main), path)
            adapter = imageAdapter
            val filePath=arguments?.getParcelableArrayList<Uri>("strfile")
            if (filePath != null) {
                path=filePath
                imageAdapter.changePath(filePath)
            }

           /* commonCallbacks?.setupToolBar(
                toolbarLayout,
                false,
                getString(R.string.my_profile)
            )
            setHasOptionsMenu(true)*/
        }

        btn_add_images.setOnClickListener {
            val intent: Intent = Intent(activity, EditImageActivity::class.java)
            intent.putExtra("list", path)
            intent.putExtra("pos",pos)
            activity?.startActivityForResult(intent,EDITIMAGELIST)

         /*   FishBun.with(this@SubFragment)
                .setImageAdapter(PicassoAdapter())
                .setMaxCount(10)
                .setActionBarColor(Color.parseColor("#3F51B5"), Color.parseColor("#303F9F"))
                .setSelectedImages(path)
                .setCamera(true)
                .startAlbum()*/
        }
        imageShare.setOnClickListener {

            var bundle = bundleOf("files" to path)

            Handler(Looper.getMainLooper()).postDelayed({
                run {
                    findNavController().navigate(R.id.CreatePostFragment, bundle)
                }
            },200)

        }
        ivAppLogo.setOnClickListener {
            onFragBack()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
//            Define.ALBUM_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) {
//                path = data!!.getParcelableArrayListExtra(Define.INTENT_PATH)!!
//                imageAdapter.changePath(path)
//            }
        }


        when (requestCode) {
            EDITIMAGELIST -> if (resultCode == Activity.RESULT_OK) { // path = imageData.getStringArrayListExtra(Define.INTENT_PATH);
// you can get an image path(ArrayList<String>) on <0.6.2
                if (data != null) {


                    path.set(data.getIntExtra("pos",0),Uri.fromFile( File( data.getStringExtra("strfile"))))

                    imageAdapter.changePath(path)



                  /*  var bundle = bundleOf("strfile" to data.getStringExtra("strfile"))

                    Handler(Looper.getMainLooper()).postDelayed({
                        run {
                            findNavController().navigate(R.id.CreatePostFragment, bundle)
                        }
                    },200)*/


                }
                // you can get an image path(ArrayList<Uri>) on 0.6.2 and later

            }
        }

    }

    companion object {
        fun newInstance() = SubFragment()
    }
}
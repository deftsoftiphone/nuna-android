package com.demo.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.demo.R
import com.demo.base.MyViewModelProvider
import com.demo.databinding.ActivityPhotoViewBinding
import com.demo.post_details.PhotosAdapter
import com.demo.util.ParcelKeys

class PhotoViewActivity : com.demo.base.BaseActivity() {

    lateinit var mBinding: ActivityPhotoViewBinding
    lateinit var mAdapter: PhotosAdapter
    lateinit var mViewModel: PhotoViewViewModel

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        setupObserver()
        val b = intent.extras
        if (b != null) {
            mViewModel.photos =
                (b.getStringArrayList(ParcelKeys.PK_PICKED_IMAGES_PATH_ARRAY)) ?: ArrayList()
            mViewModel.targetPosition = (b.getInt(ParcelKeys.PK_TARGET_IMAGE_POSITION))
            mViewModel.isDataTypeUrl = (b.getBoolean(ParcelKeys.PK_IS_DATA_TYPE_URL))
        }

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_photo_view)
        setupToolBar()
        if (!mViewModel.photos.isEmpty()) {
            if (mViewModel.targetPosition != 0) {
                try {
                    val field =
                        ViewPager::class.java.getDeclaredField("mRestoredCurItem")
                    field.isAccessible = true
                    //  field[mBinding.photosPager] = mViewModel.targetPosition
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            mAdapter = PhotosAdapter(mViewModel.photos, true, false, "", this)
            /*  mBinding.photosPager.addOnPageChangeListener(object : OnPageChangeListener {
                  override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
                  override fun onPageSelected(i: Int) {
                      updateHeader()
                  }

                  override fun onPageScrollStateChanged(i: Int) {}
              })
              mBinding.photosPager.setAdapter(mAdapter)*/
        }
    }

    private fun setupViewModel() {
        mViewModel = ViewModelProviders.of(this, MyViewModelProvider(this))
            .get(PhotoViewViewModel::class.java)
    }

    private fun setupObserver() {
    }

    private fun setupToolBar() {
        setSupportActionBar(mBinding.toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        updateHeader()
    }

    private fun onItemDeleted() {
        //  mAdapter.removeItem(mBinding.photosPager.getCurrentItem())
        setResult()
        updateHeader()
        if (mAdapter.list.isEmpty()) finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun updateHeader() {
        // supportActionBar!!.setTitle("(" + (mBinding.photosPager.getCurrentItem() + 1).toString() + "/" + mViewModel.photos.size.toString() + ")")
    }

    private fun setResult() {
        val b = Bundle()
        b.putStringArrayList(ParcelKeys.PK_PICKED_IMAGES_PATH_ARRAY, mAdapter.removedItems)
        setResult(RESULT_OK, Intent().putExtras(b))
    }

    override fun onBackPressed() {
        closeActivity()
    }

    override fun setLang(strLang: String) {
    }

}